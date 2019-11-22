package cn.deercare.controller;


import cn.deercare.annotation.Authorization;
import cn.deercare.common.ResultCode;
import cn.deercare.enums.UserType;
import cn.deercare.finals.OrderFinals;
import cn.deercare.finals.wechat.WechatAccountInfo;
import cn.deercare.finals.wechat.WechatPayVerification;
import cn.deercare.model.*;
import cn.deercare.service.*;
import cn.deercare.utils.TokenUtils;
import cn.deercare.utils.UserUtil;
import cn.deercare.vo.RestResult;
import cn.deercare.wechat.api.WechatAPICall;
import cn.deercare.wechat.api.WechatPayAPICall;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.deercare.controller.base.BaseController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author JiangYuan
 * @since 2019-09-23
 */
@RestController
@RequestMapping("/user")
@Api(value = "/user", tags = {"用户"})
public class UserController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserWechatService userWechatService;
    @Autowired
    private UserProjectService userProjectService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderWechatService orderWechatService;

    private static final Integer PAY_SMALL_CHANGE_MAX_AMOUNT = 5000;
    private static final Integer PAY_SMALL_CHANGE_MIN_AMOUNT = 1;
    // 上次调用提现接口时间
    private static final LocalDateTime lastRunTime =  null;

    /**
     * 用户提现操作
     * @param request
     * @return
     */
    @GetMapping("/cash/{amount}")
    @ApiOperation(value = "提现操作", notes = "{}", response = RestResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="path", name = "amount", value = "提现金额", example = "1000",required = true, dataTypeClass = String.class)
    })
    @Authorization
    public Object withdrawCash(@ApiIgnore HttpServletRequest request,
                       @PathVariable("amount") BigDecimal amount) {
        Map<String , Object> json = createJson();
        logBefore(logger, "用户提现操作");
        try {
            logger.info("判断提现金额是否在微信允许的范围内");
            if(amount.doubleValue() > PAY_SMALL_CHANGE_MAX_AMOUNT ||
                amount.doubleValue() < PAY_SMALL_CHANGE_MIN_AMOUNT){
                this.setJson(json, ResultCode.REQ_FAIL, "提现金额不在微信允许的范围内");
                return json;
            }
            logger.info("获取用户信息");
            UserWechat userWechat = UserUtil.getUser(request);
            logger.info("判断用户本次提现金额是否充足");
            if(userWechat.getAmount().doubleValue() < amount.doubleValue()){
                this.setJson(json, ResultCode.REQ_FAIL, "可提现金额不足");
                return json;
            }
            // 提现操作
            userService.userWithdrawCash(userWechat, amount);
            this.setJson(json, ResultCode.REQ_SUCCESS, "成功");
            return json;
        }catch (Exception e){
            logger.error(e.toString(), e);
            this.setJson(json, ResultCode.REQ_FAIL, "失败");
        }finally {
            logAfter(logger, json);
        }
        return json;
    }

    /**
     * 登录操作
     * @param jsonObject
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "登录操作", notes = "{\"code\":\"\",\"type\":\"1\"}", response = RestResult.class)
    @ApiOperationSupport(params = @DynamicParameters(name = "User",properties = {
            @DynamicParameter(name = "code",value = "微信code", example = "abcd",required = true,dataTypeClass = String.class),
            @DynamicParameter(name = "type",value = "固定为1", example = "1",required = true,dataTypeClass = int.class)
            // @DynamicParameter(name = "userInfo",value = "用户信息",dataTypeClass = User.class)
    }))
    public Object login(@RequestBody JSONObject jsonObject) {
        Map<String, Object> json = createJson();
        logBefore(logger, "用户登录");
        try {
            logger.info("查询用户");
            UserType userType = UserUtil.getUserType(jsonObject.getInteger("type"));
            switch (userType){
                case WECHAT:
                   return wechatLogin(jsonObject, json);
            }
            // 登录失败
            this.setJson(json, ResultCode.REQ_FAIL, "登录失败");
            return json;
        }catch (Exception e) {
            logger.error(e.toString(), e);
            this.setJson(json, ResultCode.REQ_FAIL, "登录失败");
        }finally {
            logAfter(logger, json);
        }
        return json;
    }

    private Map<String, Object> wechatLogin(JSONObject jsonObject, Map<String, Object> json){
        logger.info("微信用户登录");
        // WechatUser wechatUser = jsonObject.toJavaObject(WechatUser.class);
        // code获取openId
        logger.info("获取openId信息");
        JSONObject wechatJson = WechatAPICall.getUserInfoByProgram(jsonObject.getString("code"));
        // 获取openId失败
        if(wechatJson.getString("openid") == null){
            this.setJson(json, ResultCode.REQ_SUCCESS, "登录失败，检测code码是否有效");
            return json;
        }
        logger.info("获取openId为" + wechatJson.getString("openid"));
        logger.info("从库中查询微信用户信息");
        // 获取微信用户信息
        UserWechat userWechat = userWechatService.getOne(Wrappers.<UserWechat>query()
                .eq("unionid",wechatJson.getString("unionid"))
                .select("*"));
        UserWechat uw = jsonObject.toJavaObject(UserWechat.class);
        uw.setSessionKey(wechatJson.getString("session_key"));
        uw.setProgramOpenid(wechatJson.getString("openid"));
        uw.setUnionid(wechatJson.getString("unionid"));
        if(null == userWechat){// 没有用户信息，新增
            logger.info("没用用户信息，新增");
            // 先保存user表信息获取主键
            User user = new User();
            user.setType(jsonObject.getInteger("type"));
            userService.save(user);
            // 主键绑定，保存微信用户信息
            uw.setUserId(user.getId());
            // 清空user表信息
            uw.cleanUser();
            userWechatService.save(uw);
            // 生产token
            user.setToken(TokenUtils.createJwtToken(user.getId(), user.getType()));
            json.put("data", user);
            this.setJson(json, ResultCode.REQ_SUCCESS, "登录成功");
            return json;
        }
        // 微信用户在库中，则更新微信用户信息
        logger.info("微信用户在库中，则更新微信用户信息");
        // 清空user表信息
        uw.setUserId(userWechat.getUserId());
        uw.cleanUser();
        userWechatService.update(uw, Wrappers.<UserWechat>update()
                .eq("unionid",uw.getUnionid()));
        // 生产token保存
        logger.info("产生token并保存");
        User user = new User(uw.getUserId(), jsonObject.getInteger("type"));
        user.setToken(TokenUtils.createJwtToken(user.getId(), user.getType()));
        user.setUpdateTime(LocalDateTime.now());
        userService.updateById(user);
        json.put("data", user);
        this.setJson(json, ResultCode.REQ_SUCCESS, "登录成功");
        logger.info("查询未验证的此用户参与的项目，并依次验证");
        // 查询未验证的此用户参与的项目，并依次验证
        List<UserProject> userProjectList = userProjectService.list(Wrappers.<UserProject>query()
                .eq("user_id", user.getId())
                .eq("verification", WechatPayVerification.UNVERIFIED));
        wechatOrderVerification(userProjectList);
        return json;
    }

    /**
     * 依次验证用户参与的项目
     * @param userProjectList
     */
    private void wechatOrderVerification(List<UserProject> userProjectList){
        userProjectList.forEach(userProject -> {
            // 查询订单
            Order order = orderService.getById(userProject.getOrderId());
            // 向微信查询支付结果
             Map<String, Object> orderQueryMap = WechatPayAPICall.orderQuery(order.getNumber());
            // 验证通过
            if(orderQueryMap.get("trade_state") != null &&
                    orderQueryMap.get("trade_state").equals(WechatAccountInfo.PAY_STATE_SUCCESS)){
                // 修改为验证已通过
                userProjectService.update(Wrappers.<UserProject>update()
                        .set("verification", WechatPayVerification.VERIFICATION_SUCCESS)
                        .eq("order_id",order.getId()));
                // 修改订单状态为已付款
                orderService.update(Wrappers.<Order>update()
                        .set("state", OrderFinals.ORDER_PAY)
                        .eq("id", order.getId()));
                // 修改微信订单状态为支付成功
                orderWechatService.update(Wrappers.<OrderWechat>update()
                        .set("state", WechatAccountInfo.PAY_STATE_SUCCESS)
                        .eq("order_id",order.getId()));
            }else{
                // 修改为验证未通过
                userProjectService.update(Wrappers.<UserProject>update()
                        .set("verification", WechatPayVerification.VERIFICATION_FAILED)
                        .eq("order_id",order.getId()));
                // 修改订单状态为关闭
                orderService.update(Wrappers.<Order>update()
                        .set("state", OrderFinals.ORDER_CLOSE)
                        .eq("id", order.getId()));
                // 不修改微信订单状态
            }
        });
    }

    public static void main(String[] args) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("s","f");
        System.out.println(obj.get("f") == null);
    }
}
