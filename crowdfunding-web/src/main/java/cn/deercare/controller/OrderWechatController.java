package cn.deercare.controller;


import cn.deercare.finals.OrderState;
import cn.deercare.finals.WechatPayVerification;
import cn.deercare.model.Order;
import cn.deercare.model.OrderWechat;
import cn.deercare.model.UserProject;
import cn.deercare.service.OrderService;
import cn.deercare.service.OrderWechatService;
import cn.deercare.service.UserProjectService;
import cn.deercare.utils.StringUtil;
import cn.deercare.wechat.api.WechatPayAPICall;
import cn.deercare.wechat.finals.WechatAccountInfo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

import cn.deercare.controller.base.BaseController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author JiangYuan
 * @since 2019-10-06
 */
@RestController
@RequestMapping("/order-wechat")
@ApiIgnore
public class OrderWechatController extends BaseController {
/*
    <xml>
  <appid><![CDATA[wx4436516d451b7de9]]></appid>
  <attach><![CDATA[{"orderNum":"630457777826824192"}]]></attach>
  <bank_type><![CDATA[CFT]]></bank_type>
  <cash_fee><![CDATA[2]]></cash_fee>
  <device_info><![CDATA[PROGRAM]]></device_info>
  <fee_type><![CDATA[CNY]]></fee_type>
  <is_subscribe><![CDATA[N]]></is_subscribe>
  <mch_id><![CDATA[1556309821]]></mch_id>
  <nonce_str><![CDATA[ri3eea21mkbqw3u8pw3sphpb58r6etq6]]></nonce_str>
  <openid><![CDATA[oWNYK4z5kiLWHy3B3LIVCwO2iouk]]></openid>
  <out_trade_no><![CDATA[630457777826824192]]></out_trade_no>
  <result_code><![CDATA[SUCCESS]]></result_code>
  <return_code><![CDATA[SUCCESS]]></return_code>
  <sign><![CDATA[C29A84F6ECC784B300648A132D03EBA4]]></sign>
  <time_end><![CDATA[20191006173500]]></time_end>
  <total_fee>2</total_fee>
  <trade_type><![CDATA[JSAPI]]></trade_type>
  <transaction_id><![CDATA[4200000421201910060352972675]]></transaction_id>
</xml>
    */

    @Autowired
    private UserProjectService userProjectService;
    @Autowired
    private OrderWechatService orderWechatService;
    @Autowired
    private OrderService orderService;

    @PostMapping("notify")
    public Object notify(HttpServletRequest request, HttpServletResponse response){
        try{
            // 获取响应内容体
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    request.getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer strBuf = new StringBuffer();
            while ((line = in.readLine()) != null) {
                strBuf.append(line);
            }
            //
            in.close();
            // 解析
             Map<String, Object> resultMap = StringUtil.xmlToArray(strBuf.toString());
            if(String.valueOf(resultMap.get("return_code")).equals("SUCCESS")){// 通信成功
                // 开启另外线程处理，订单状态，为了立刻给返回微信通信成功
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 获取预留的信息
                        JSONObject outTradeNo = JSONObject.parseObject(String.valueOf(resultMap.get("attach")));
                        // 获取订单号码
                        String orderNum = outTradeNo.getString("orderNum");
                        // 获取订单id
                        Long orderId = outTradeNo.getLong("orderId");
                        // 保存通知信息
                        orderWechatService.update(Wrappers.<OrderWechat>update()
                                .set("result_notify", strBuf.toString())
                                .set("state", WechatAccountInfo.PAY_STATE_SUCCESS)
                                .eq("order_id", orderId));
                        // 查询微信订单，支付结果
                        Map<String, Object> orderQueryMap = WechatPayAPICall.orderQuery(orderNum);
                        if(orderQueryMap.get("trade_state") != null &&
                                orderQueryMap.get("trade_state").equals(WechatAccountInfo.PAY_STATE_SUCCESS)){
                            // 支付成功，修改用户与项目的关系状态
                            // 获取关系id
                            Long userProjectId = outTradeNo.getLong("userProjectId");
                            // 修改为验证通过
                            userProjectService.update(Wrappers.<UserProject>update()
                                    .set("verification", WechatPayVerification.VERIFICATION_SUCCESS)
                                    .eq("id", userProjectId));
                            // 修改状态为已付款
                            orderService.update(Wrappers.<Order>update()
                                    .set("state", OrderState.ORDER_PAY)
                                    .eq("id", orderId));
                        }
                    }
                }).start();
                // 返回微信 通知成功
                String ret = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
                response.setCharacterEncoding("utf-8");
                response.setHeader("Cache-Control", "no-cache");
                response.getWriter().write(ret);
                response.getWriter().flush();
                response.getWriter().close();
            }else{
                // 通信失败
                System.out.println(strBuf);
            }
        }catch (Exception e){

        }
        return null;
    }

}
