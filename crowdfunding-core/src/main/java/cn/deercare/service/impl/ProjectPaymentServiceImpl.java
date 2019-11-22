package cn.deercare.service.impl;

import cn.deercare.finals.OrderNumberFinal;
import cn.deercare.mapper.OrderMapper;
import cn.deercare.mapper.OrderWechatMapper;
import cn.deercare.mapper.UserProjectMapper;
import cn.deercare.model.*;
import cn.deercare.mapper.ProjectPaymentMapper;
import cn.deercare.service.OrderService;
import cn.deercare.service.ProjectPaymentService;
import cn.deercare.utils.AmountUtils;
import cn.deercare.utils.SnowflakeIdWorker;
import cn.deercare.utils.StringUtil;
import cn.deercare.utils.UserUtil;
import cn.deercare.wechat.api.WechatPayAPICall;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.sf.jsqlparser.util.deparser.OrderByDeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JiangYuan
 * @since 2019-10-05
 */
@Service
@Transactional
public class ProjectPaymentServiceImpl extends ServiceImpl<ProjectPaymentMapper, ProjectPayment> implements ProjectPaymentService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderWechatMapper orderWechatMapper;
    @Autowired
    private UserProjectMapper userProjectMapper;

    @Override
    public Object pay(Project project, BigDecimal amount, HttpServletRequest request) {
        // 判断项目类型
        if(project instanceof ProjectHotel){
            // 酒店类型
            ProjectHotel ph = (ProjectHotel) project;
            // 查询用户的信息
            UserWechat uw = (UserWechat)UserUtil.getUser(request);
            return projectHotelPay(ph, amount, uw, StringUtil.getIpAddress(request));
        }
        return null;
    }

    private JSONObject projectHotelPay(ProjectHotel projectHotel, BigDecimal amount, UserWechat userWechat,
                                 String ip){
        // 生产系统内部订单号
        String orderNum = String.valueOf(new SnowflakeIdWorker(OrderNumberFinal.WORKER_ID, OrderNumberFinal.DATA_CENTER_ID).nextId());
        // 保存订单信息
        Order order = Order.getCommonOrder(projectHotel.getName(), amount, amount, userWechat.getMainId());
        orderMapper.insert(order);
        // 重新查询订单信息
        order = orderMapper.selectOne(Wrappers.<Order>query().eq("number", orderNum));
        // 计算本次投资占比(本次金额/目标金额*100,保留小数点后1位，其他砍掉，不适用四舍五入)
        Double proportion = amount.divide(projectHotel.getAmount(), 1, RoundingMode.FLOOR).multiply(new BigDecimal(new Integer(100).toString())).doubleValue();
        // 建立用户与众筹项目的关系
        UserProject userProject = new UserProject(userWechat.getUserId(), projectHotel.getProjectId(), order.getId(),
                proportion);
        userProjectMapper.insert(userProject);
        // 微信附加数据
        JSONObject json = new JSONObject();
        // 订单id
        json.put("orderId", order.getId());
        // 项目名称
        json.put("name", projectHotel.getName());
        // 用户的公众号openid
        json.put("account_openid", userWechat.getAccountOpenid());
        json.put("userProjectId", userProject.getId());
        // 调用微信支付统一下单接口
        List<Object> list = WechatPayAPICall.unifiedOrder(
                projectHotel.getName(), projectHotel.getName(), json.toString(),
                orderNum, AmountUtils.changeY2F(amount.toString()),
                ip, projectHotel.getId(), userWechat.getProgramOpenid());
        // 保存微信订单信息
        orderWechatMapper.insert(new OrderWechat(
                list.get(0).toString(),
                list.get(1).toString(),
                list.get(2).toString(),
                order.getId(),
                order.getNumber()));
        // 返回前端需要参数
        return (JSONObject)list.get(3);
    }

    public static void main(String[] args) {
        Double a = 2.6;
        Double b = 80.0;
        BigDecimal bi1 = new BigDecimal(a.toString());
        BigDecimal bi2 = new BigDecimal(b.toString());
        BigDecimal divide = bi1.divide(bi2, 3, RoundingMode.HALF_UP);
        BigDecimal divide2 = bi1.divide(bi2, 10, RoundingMode.HALF_UP);
        BigDecimal re = divide.multiply(new BigDecimal(new Integer(100).toString()));
        System.out.println(divide.doubleValue());
        System.out.println(divide2);
        System.out.println(bi1.divide(bi2, 3, RoundingMode.FLOOR));
        System.out.println(re.doubleValue());
    }
}
