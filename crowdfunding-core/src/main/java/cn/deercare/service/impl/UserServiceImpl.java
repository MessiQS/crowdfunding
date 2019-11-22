package cn.deercare.service.impl;

import cn.deercare.enums.UserType;
import cn.deercare.finals.OrderFinals;
import cn.deercare.finals.wechat.WechatAccountInfo;
import cn.deercare.mapper.OrderMapper;
import cn.deercare.mapper.OrderWechatMapper;
import cn.deercare.mapper.UserWechatMapper;
import cn.deercare.model.Order;
import cn.deercare.model.OrderWechat;
import cn.deercare.model.User;
import cn.deercare.mapper.UserMapper;
import cn.deercare.model.UserWechat;
import cn.deercare.service.OrderService;
import cn.deercare.service.UserService;
import cn.deercare.utils.AmountUtils;
import cn.deercare.utils.StringUtil;
import cn.deercare.utils.UserUtil;
import cn.deercare.wechat.api.WechatPayAPICall;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JiangYuan
 * @since 2019-09-23
 */
@Service
@Transactional
public class UserServiceImpl<T extends User> extends ServiceImpl<UserMapper, User> implements UserService<T> {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserWechatMapper userWechatMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderWechatMapper orderWechatMapper;

    public List<T> listByProject(Serializable id){
        return getListDetailed(userMapper.listByProject(id));
    }

    @Override
    public void userWithdrawCash(UserWechat user, BigDecimal amount) {
        // WechatPayAPICall.paySmallChange();
        // -----扣除用户余额
        userMapper.userAmountMinus(amount.toString(), user.getMainId());
        // -----微信提现信息记录入库
        // 保存订单信息
        Order order = Order.getWithdrawCashOrder(amount, user.getMainId());
        orderMapper.insert(order);
        // 重新查询订单信息，获取id
        order = orderMapper.selectOne(Wrappers.<Order>query().eq("number", order.getNumber()));
        // ------执行提现操作
        List<Object> list = WechatPayAPICall.paySmallChange(order.getNumber(), user.getProgramOpenid(), AmountUtils.changeY2F(amount.toString()), "收益提现");
        // ------查询提现是否成功
        List<Object> resultList = WechatPayAPICall.paySmallChangeQuery(order.getNumber());
        String result = resultList.get(1).toString();
        // ------生产微信订单信息
        orderWechatMapper.insert(new OrderWechat(
                list.get(0).toString(),
                list.get(1).toString(),
                result,
                order.getId(),
                order.getNumber()));
        Map<String, Object> resultMap = StringUtil.xmlToArray(result);
        if(resultMap.get("status").toString().equals("SUCCESS")){
            // 修改订单状态为已付款
            orderMapper.update(null, new UpdateWrapper<Order>()
                    .set("state", OrderFinals.ORDER_PAY)
                    .eq("id", order.getId()));
            // 修改微信订单状态为支付成功
            orderWechatMapper.update(null, new UpdateWrapper<OrderWechat>()
                    .set("state", WechatAccountInfo.PAY_STATE_SUCCESS)
                    .eq("order_id",order.getId()));
        }else{
            // 修改订单状态为关闭
            orderMapper.update(null, new UpdateWrapper<Order>()
                    .set("state", OrderFinals.ORDER_CLOSE)
                    .eq("id", order.getId()));
            // 修改微信订单状态为失败
            orderWechatMapper.update(null, new UpdateWrapper<OrderWechat>()
                    .set("state", WechatAccountInfo.PAY_STATE_FAILED)
                    .set("result_smallchange", result)
                    .eq("order_id",order.getId()));
        }
    }

    @Override
    public void userAmountPlus(String amount, Serializable id) {
        userMapper.userAmountPlus(amount, id);
    }

    private List<T> getListDetailed(List<User> userList){
        List<T> userDetailedList = new ArrayList<T>();
        userList.forEach(user ->{
            // 获取用户类型
            UserType userType = UserUtil.getUserType(user.getType());
            // 查询详细
            switch (userType){
                case WECHAT:
                    UserWechat userWechat = userWechatMapper.selectOne(new QueryWrapper<UserWechat>()
                            .eq("user_id",user.getId())
                            .select("*"));
                    userWechat.setUser(user);
                    userDetailedList.add((T) userWechat);
                    break;
            }
        });
        return userDetailedList;
    }

    public static void main(String[] args) {
        String xml = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[]]></return_msg><mch_appid><![CDATA[wxec38b8ff840bd989]]></mch_appid><mchid><![CDATA[10013274]]></mchid><device_info><![CDATA[]]></device_info><nonce_str><![CDATA[lxuDzMnRjpcXzxLx0q]]></nonce_str><result_code><![CDATA[SUCCESS]]></result_code><partner_trade_no><![CDATA[10013574201505191526582441]]></partner_trade_no><payment_no><![CDATA[1000018301201505190181489473]]></payment_no><payment_time><![CDATA[2015-05-19 15：26：59]]></payment_time></xml>";
        Map<String, Object> wcResultMap = StringUtil.xmlToArray(xml);
        System.out.println(wcResultMap.get("return_code").toString());
        System.out.println(wcResultMap.get("result_code").toString());
    }

}
