package cn.deercare.controller;


import cn.deercare.annotation.Authorization;
import cn.deercare.common.ResultCode;
import cn.deercare.enums.ProjectHotelState;
import cn.deercare.model.ProjectHotel;
import cn.deercare.model.User;
import cn.deercare.model.UserWechat;
import cn.deercare.service.ProjectHotelService;
import cn.deercare.service.UserService;
import cn.deercare.utils.ProjectUtil;
import cn.deercare.utils.SpringUtil;
import cn.deercare.utils.UserUtil;
import cn.deercare.vo.RestResult;
import cn.deercare.wechat.api.WechatAPICall;
import cn.deercare.wechat.finals.WechatAccountInfo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.deercare.controller.base.BaseController;
import cn.deercare.service.ProjectService;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import static cn.deercare.enums.ProjectHotelState.P;
import static cn.deercare.enums.ProjectHotelState.O;
import static cn.deercare.enums.ProjectHotelState.L;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author JiangYuan
 * @since 2019-09-23
 */
@RestController
@RequestMapping("/project")
@Api(value = "/project", tags = {"众筹项目"})
@Authorization
public class ProjectController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectHotelService projectHotelService;
    @Autowired
    private UserService userService;

    private static final Integer PAGE_SIZE = 20;
    private static final ProjectHotelState [] NOTIFY_STATE = {P,L,O};

    @GetMapping("list/{page}")
    @ApiOperation(value = "众筹项目列表", notes = "", response = RestResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="path", name = "page", value = "页数", required = true, dataTypeClass = int.class)
    })
    public Object list(@PathVariable("page") Integer page){
        Map<String, Object> json = createJson();
        logBefore(logger, "众筹项目列表");
        try {
            PageHelper.startPage(page, PAGE_SIZE);
            // 查询操作
            List<Object> proList = projectService.listDetailed();
            json.put("data", proList);
            this.setJson(json, ResultCode.REQ_SUCCESS, "查询成功");
            return json;
        }catch (Exception e) {
            logger.error(e.toString(), e);
            this.setJson(json, ResultCode.REQ_FAIL, "查询失败");
        }finally {
            logAfter(logger, json);
        }
        return json;
    }

    @GetMapping("listByUser/{page}")
    @ApiOperation(value = "用户参与的众筹项目列表", notes = "", response = RestResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="path", name = "page", value = "页数", required = true, dataTypeClass = int.class)
    })
    public Object listByUser(@PathVariable("page") Integer page, HttpServletRequest request){
        Map<String, Object> json = createJson();
        logBefore(logger, "用户参与的众筹项目列表");
        try {
            PageHelper.startPage(page, PAGE_SIZE);
            // 查询操作
            User user = UserUtil.getUser(request);
            List<Object> proList = projectService.listDetailedByUser(user);
            json.put("data", proList);
            this.setJson(json, ResultCode.REQ_SUCCESS, "查询成功");
            return json;
        }catch (Exception e) {
            logger.error(e.toString(), e);
            this.setJson(json, ResultCode.REQ_FAIL, "查询失败");
        }finally {
            logAfter(logger, json);
        }
        return json;
    }

    @GetMapping("nextState/{id}")
    @ApiOperation(value = "众筹项目进入下一状态", notes = "", response = RestResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="path", name = "id", value = "项目id", required = true, dataTypeClass = long.class)
    })
    public Object nextState(@PathVariable("id") Long id){
        Map<String, Object> json = createJson();
        logBefore(logger, "众筹项目进入下一状态");
        try {
            return projectNextState(id, json);
        }catch (Exception e){
            logger.error(e.toString(), e);
            this.setJson(json, ResultCode.REQ_FAIL, "失败--众筹项目进入下一状态");
        }finally {
            logAfter(logger, json);
        }
        return json;
    }

    public Object projectNextState(Long id, Map<String, Object>  json){
        projectService = projectService == null? SpringUtil.getBean(ProjectService.class):projectService;
        Object ph = projectService.getDetailedById(id);
        // 酒店项目
        if(ph instanceof ProjectHotel){
            ProjectHotel projectHotel = (ProjectHotel) ph;
            ProjectHotelState nextState = projectHotelNextState(projectHotel);
            if(json == null){
                return null;
            }
            if(nextState == null){
                this.setJson(json, ResultCode.REQ_FAIL, "失败--众筹项目没有下一状态");
                return json;
            }
            json.put("data", nextState.getValue());
            this.setJson(json, ResultCode.REQ_SUCCESS, "成功--众筹项目进入下一状态");
        }
        return json;
    }

    private ProjectHotelState projectHotelNextState(ProjectHotel projectHotel){
        // 获取当前状态
        ProjectHotelState projectHotelState = ProjectUtil.getProjectHotelState(projectHotel.getState());
        // 获取下一个状态
        ProjectHotelState nextProjectHotelSate = ProjectUtil.getProjectHotelNextState(projectHotel);
        // 没有下一个状态
        if(null == nextProjectHotelSate){
            return null;
        }
        projectHotel.setState(nextProjectHotelSate.getValue());
        // 保存状态
        projectHotelService = projectHotelService == null? SpringUtil.getBean(ProjectHotelService.class):projectHotelService;
        projectHotelService.update(Wrappers.<ProjectHotel>update()
                .eq("id", projectHotel.getId())
                .set("state", projectHotel.getState()));
        // 获取相关用户列表
        userService = userService == null? SpringUtil.getBean(UserService.class):userService;
        List<Object> userList = userService.listByProject(projectHotel.getProjectId());
        // 判断用户类型
        userList.forEach(user ->{
            if(user instanceof UserWechat){
                UserWechat uw = (UserWechat) user;
                // 根据状态发送推送
                switch (nextProjectHotelSate){
                    case P:
                        // 发送工厂生产推送
                        WechatAPICall.sendTemplateMessage(
                                WechatAccountInfo.DEERCARE_TEMPLATE_CASH_OUT_IDCash_ID,
                                "oPBltwI1mcGmi63mNbeU50sQ6L1E","https://www.baidu.com/",
                                "工厂生产","remark","key1","key2","key3","key4");
                        break;
                    case L:
                        // 发送酒店铺设推送
                        WechatAPICall.sendTemplateMessage(
                                WechatAccountInfo.DEERCARE_TEMPLATE_CASH_OUT_IDCash_ID,
                                "oPBltwI1mcGmi63mNbeU50sQ6L1E","https://www.baidu.com/",
                                "酒店铺设","remark","key1","key2","key3","key4");
                        break;
                    case O:
                        // 发送正式运营推送
                        WechatAPICall.sendTemplateMessage(
                                WechatAccountInfo.DEERCARE_TEMPLATE_CASH_OUT_IDCash_ID,
                                "oPBltwI1mcGmi63mNbeU50sQ6L1E","https://www.baidu.com/",
                                "正式运行","remark","key1","key2","key3","key4");
                        break;
                }
                /*
                if(nextProjectHotelSate == P){
                    // 发送工厂生产推送
                    WechatAPICall.sendTemplateMessage(
                            WechatAccountInfo.DEERCARE_TEMPLATE_CASH_OUT_IDCash_ID,
                            "oPBltwI1mcGmi63mNbeU50sQ6L1E","https://www.baidu.com/",
                            "工厂生产","remark","key1","key2","key3","key4");
                }else if(nextProjectHotelSate == L){
                    // 发送酒店铺设推送
                    WechatAPICall.sendTemplateMessage(
                            WechatAccountInfo.DEERCARE_TEMPLATE_CASH_OUT_IDCash_ID,
                            "oPBltwI1mcGmi63mNbeU50sQ6L1E","https://www.baidu.com/",
                            "酒店铺设","remark","key1","key2","key3","key4");
                }else if(nextProjectHotelSate == O){
                    // 发送正式运营推送
                    WechatAPICall.sendTemplateMessage(
                            WechatAccountInfo.DEERCARE_TEMPLATE_CASH_OUT_IDCash_ID,
                            "oPBltwI1mcGmi63mNbeU50sQ6L1E","https://www.baidu.com/",
                            "正式运行","remark","key1","key2","key3","key4");
                }
                 */
            }
        });
        return nextProjectHotelSate;
    }

    public static void main(String[] args) {
        String k = String.format(MessageFormat.format("fffff{0}", "----"));
        System.out.println(k);
    }
}
