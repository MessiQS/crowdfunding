package cn.deercare.controller;


import cn.deercare.annotation.Authorization;
import cn.deercare.common.ResultCode;
import cn.deercare.enums.ProjectHotelState;
import cn.deercare.model.*;
import cn.deercare.service.ProjectIncomeService;
import cn.deercare.service.ProjectService;
import cn.deercare.service.UserService;
import cn.deercare.service.UserWechatService;
import cn.deercare.utils.ProjectIncomeUtils;
import cn.deercare.utils.ProjectUtil;
import cn.deercare.utils.StringUtil;
import cn.deercare.utils.UserUtil;
import cn.deercare.vo.RestResult;
import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

import cn.deercare.controller.base.BaseController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.math.BigDecimal.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author JiangYuan
 * @since 2019-10-13
 */
@RestController
@RequestMapping("/project-income")
@Api(value = "/project-income", tags = {"项目收益相关"})
public class ProjectIncomeController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ProjectIncomeController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserWechatService userWechatService;
    @Autowired
    private ProjectIncomeService projectIncomeService;
    @Autowired
    private ProjectService projectService;

    @GetMapping("input/{id}/{date}/{amount}/{conversion}")
    @ApiOperation(value = "输入项目收益", notes = "", response = RestResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="path", name = "id", value = "项目id", required = true, dataTypeClass = long.class),
            @ApiImplicitParam(paramType="path", name = "amount", value = "金额", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(paramType="path", name = "date", value = "收益日期", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(paramType="path", name = "conversion", value = "转换率", required = true, dataTypeClass = String.class)
    })
    public Object input(@PathVariable("id") Long id, @PathVariable("amount") BigDecimal amount,
                        @PathVariable("date") String date, @PathVariable("conversion") String conversion,
                        HttpServletRequest request){
        Map<String, Object> json = createJson();
        logBefore(logger, "输入项目收益");
        try{
            // 验证独有token
            String token = request.getHeader("Authorization");
            if(!token.equals("6B664999626C7726B90FDF313CF6BD62") &&
                !token.equals("91D82764F5C1769D316D2B3538910587")){
                this.setJson(json, ResultCode.REQ_FAIL, "错误--非法操作");
                return json;
            }
            // 查询当前项目必须为已运营状态
            Project project = projectService.getDetailedById(id);
            if(project instanceof  ProjectHotel){
                ProjectHotel ph = (ProjectHotel) project;
                if(ProjectHotelState.O != ProjectUtil.getProjectHotelState(ph.getState())){
                    this.setJson(json, ResultCode.REQ_FAIL, "错误--该项目尚未开始运营");
                    return json;
                }
                // 当天已经填写过收益则覆盖
                ProjectIncome projectIncome = new ProjectIncome(ph.getProjectId(), amount, LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")), conversion);
                projectIncomeService.saveOrUpdate(projectIncome, Wrappers.<ProjectIncome>update()
                        .eq("date", projectIncome.getDate())
                        .eq("project_id", ph.getProjectId())
                );
            }
            logger.info("获取参与该项目的用户");
            List<User> userList = userService.listByProject(project.getMainId());
            userList.forEach(user -> {
                logger.info("开始---计算该用户本项目的今日收益");
                logger.info("查询用户在该项目中所占比例");
                BigDecimal proportionAmount = projectService.getProjectProportionByUser(new User(user.getMainId()), project);
                logger.info("计算本项目获取的收益 总收益*0.5平台占比*本人占比");
                BigDecimal proIncome = ProjectIncomeUtils.getIncomeByOne(amount,proportionAmount);
                logger.info("用户本项目今日收益更新到余额");
                userService.userAmountPlus(proIncome.toString(), user.getMainId());
                logger.info("结束---计算该用户本项目的今日收益");
            });
            this.setJson(json, ResultCode.REQ_SUCCESS, "成功");
        }catch (Exception e){
            logger.error(e.toString(), e);
            this.setJson(json, ResultCode.REQ_FAIL, "失败--填写id为" + id + "的众筹项目的今日收益失败");
        }finally {
            logAfter(logger, json);
        }
        return json;
    }

    @GetMapping("history")
    @ApiOperation(value = "个人历史收益", notes = "主要字段：historyIncome（项目总收益)，historyIncomeProportion（项目总平均收益率）。----前端取每个项目的收益数据，计算出所有项目的总收益数据", response = RestResult.class)
    public Object projectIncomeHistory(@ApiIgnore  HttpServletRequest request){
        Map<String, Object> json = createJson();
        logBefore(logger, "个人历史收益");
        try{
            // 用户参与项目的id集合
            List<Long> projectIdList = new ArrayList<Long>();
            logger.info("获取用户信息");
            UserWechat userWechat = UserUtil.getUser(request);
            logger.info("查询用户参与的项目");
            List<Project> projectList = projectService.listDetailedByUser((User)userWechat);
            if(projectList == null || projectList.size() == 0){
                this.setJson(json, "用户没参与任何项目，所有数值均为0");
                return json;
            }
            projectList.forEach(p ->{projectIdList.add(p.getMainId());});

            logger.info("用户参与的项目转map");
            // userlist.stream().collect(Collectors.toMap(User::getAge,User::getName))
            Map<Long, Project> projectMap = projectList.stream().collect(Collectors.toMap(Project::getMainId, Project -> Project));
            logger.info("查询项目用户参与的项目历史收益");
            List<ProjectIncome> projectIncomeList = projectIncomeService.list(Wrappers.<ProjectIncome>query()
                    .in("project_id", projectIdList)
                    .select("*"));
            logger.info("计算出用户每个项目的历史收益");
            projectIdList.forEach(str ->{
                Project p = projectMap.get(str);
                logger.info("查询项目历史的收益总和");
                BigDecimal projectHistoryIncome = projectIncomeService.getProjectIncomeHistory(p.getMainId(), new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                logger.info("查询用户在该项目中所占比例");
                BigDecimal proportionAmount = projectService.getProjectProportionByUser(new User(userWechat.getUserId()), p);
                logger.info("计算个人项目历史的收益总和 总收益*0.5平台占比*本人占比");
                BigDecimal historyIncome = ProjectIncomeUtils.getIncomeByOne(projectHistoryIncome, proportionAmount);
                p.setHistoryIncome(historyIncome);
                logger.info("查询用户在该项目中的投资本金");
                BigDecimal principal = projectService.getProjectPrincipalByUser(new User(userWechat.getUserId()), p);
                logger.info("计算历史平均收益率，历史收益/本金*100");
                BigDecimal incomeProportion = ProjectIncomeUtils.getIncomeProportionByOne(historyIncome, principal);
                p.setHistoryIncomeProportion(incomeProportion);
                projectMap.put(p.getMainId(), p);
            });
            json.put("data", projectMap.values());
            this.setJson(json, ResultCode.REQ_SUCCESS, "成功");
        }catch (Exception e){
            this.setJson(json, "错误--个人历史收益");
            logger.error(e.toString(), e);
        }finally {
            logAfter(logger, json);
        }
        return json;
    }

    @GetMapping("daily/{date}")
    @ApiOperation(value = "日收益", notes = "主要字段：income（当日收益）", response = RestResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="path", name = "date", value = "收益日期", required = true, dataTypeClass = String.class),
    })
    @Authorization
    public Object projectIncomeDetailedByOne(@PathVariable("date")String date,
                                  @ApiIgnore HttpServletRequest request){
        Map<String, Object> json = createJson();
        logBefore(logger, "项目收益网页推送");
        try {
            // 用户参与项目的id集合
            List<Long> projectIdList = new ArrayList<Long>();
            // 获取用户信息
            BigDecimal income = new BigDecimal(0);
            // 我们自己投的话，酒店给3，我们7
            // 众筹的话，投资人5，酒店3，我们2
            // 你说的那个5应该是，提交酒店资源的，给5个点，是5%
            logger.info("获取用户信息");
            UserWechat userWechat = UserUtil.getUser(request);
            if(null == userWechat){
                this.setJson(json, "错误--没查询到用户");
                return json;
            }
            logger.info("查询用户参与的项目");
            List<Project> projectList = projectService.listDetailedByUser((User)userWechat);
            if(projectList == null || projectList.size() == 0){
                this.setJson(json, "用户没参与任何项目，所有数值均为0");
                return json;
            }
            projectList.forEach(p ->{projectIdList.add(p.getMainId());});

            logger.info("用户参与的项目转map");
            // userlist.stream().collect(Collectors.toMap(User::getAge,User::getName))
            Map<Long, Project> projectMap = projectList.stream().collect(Collectors.toMap(Project::getMainId, Project -> Project));
            logger.info("查询项目用户参与的项目今日收益");
            List<ProjectIncome> projectIncomeList = projectIncomeService.list(Wrappers.<ProjectIncome>query()
                    .in("project_id", projectIdList)
                    .eq("date", date));
            logger.info("计算出用户历史收益");
            projectIncomeList.forEach(projectIncome -> {
                logger.info("获取项目日收益");
                BigDecimal amount = projectIncome.getAmount();
                logger.info("查询用户在该项目中所占比例");
                Project p = projectMap.get(projectIncome.getProjectId());
                BigDecimal proportionAmount = projectService.getProjectProportionByUser(new User(userWechat.getUserId()), p);
                logger.info("计算本项目获取的收益 总收益*0.5平台占比*本人占比");
                BigDecimal proIncome = ProjectIncomeUtils.getIncomeByOne(amount,proportionAmount);
                p.setIncome(proIncome);
                // 添加进map
                projectMap.put(p.getMainId(), p);
            });
            /*
            // 测试微信付款到零钱
        String result = WechatPayAPICall.paySmallChange(String.valueOf(new SnowflakeIdWorker(OrderNumberFinal.WORKER_ID, OrderNumberFinal.DATA_CENTER_ID).nextId()),
                "oWNYK4z5kiLWHy3B3LIVCwO2iouk", AmountUtils.changeY2F("1"),"提现");
        System.out.println(result);
             */
            json.put("data", projectMap.values());
            this.setJson(json, ResultCode.REQ_SUCCESS, "成功");
        }catch (Exception e){
            this.setJson(json, "错误--项目收益网页推送");
            logger.error(e.toString(), e);
        }finally {
            logAfter(logger, json);
        }
        return json;
    }

    public static void main(String[] args) {
        BigDecimal b1 = new BigDecimal(10);
        BigDecimal b2 = new BigDecimal(3.827);
        System.out.println(b1.divide(b2, 6, ROUND_HALF_UP).doubleValue());
        System.out.println(b1.divide(b2, 20, ROUND_HALF_UP).doubleValue());

        /*
        BigDecimal historyIncome = projectHistoryIncome.multiply(cn.deercare.finals.ProjectIncome.PLATFORM_PROPORTION)
                        .multiply(proportionAmount.multiply(new BigDecimal(0.01)));
         */
    }

}
