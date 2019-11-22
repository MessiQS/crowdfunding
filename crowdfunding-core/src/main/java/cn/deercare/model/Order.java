package cn.deercare.model;

import java.math.BigDecimal;

import cn.deercare.finals.OrderNumberFinal;
import cn.deercare.finals.OrderFinals;
import cn.deercare.utils.SnowflakeIdWorker;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author JiangYuan
 * @since 2019-10-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_order")
public class Order extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    /**
     * 订单类型1众筹付款2收益提现
     */
    private Integer type;

    /**
     * u未付款,p已付款
     */
    private String state = OrderFinals.ORDER_UNPAID;

    /**
     * 应付金额
     */
    private BigDecimal amountPayable;

    /**
     * 实付金额
     */
    private BigDecimal amountPay;

    /**
     * 支付方式1微信
     */
    private Integer mode;

    /**
     * 系统备注
     */
    private String systemRemarks;

    /**
     * 用户备注
     */
    private String userRemarks;

    /**
     * 订单号码
     */
    private String number;

    /**
     * 用户id
     */
    private Long userId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Order(){}
    public Order(String name, Integer type, BigDecimal amountPayable, BigDecimal amountPay,
                 Integer mode, String number, Long userId){
        this.name = name;
        this.type = type;
        this.amountPayable = amountPayable;
        this.amountPay = amountPay;
        this.mode = mode;
        this.number = number;
        this.userId = userId;
    }

    public static Order getCommonOrder(String name, BigDecimal amountPayable, BigDecimal amountPay, Long userId){
        // 生产系统内部订单号
        String orderNum = String.valueOf(new SnowflakeIdWorker(OrderNumberFinal.WORKER_ID, OrderNumberFinal.DATA_CENTER_ID).nextId());
        return new Order(name, 1, amountPayable, amountPay, 1, orderNum, userId);
    }

    public static Order getWithdrawCashOrder(BigDecimal amount, Long userId){
        // 生产系统内部订单号
        String orderNum = String.valueOf(new SnowflakeIdWorker(OrderNumberFinal.WORKER_ID, OrderNumberFinal.DATA_CENTER_ID).nextId());
        return new Order(OrderFinals.ORDER_WITHDRAW_CASH, 2, amount, amount, 1, orderNum, userId);
    }



}
