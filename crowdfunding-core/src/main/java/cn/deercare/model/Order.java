package cn.deercare.model;

import java.math.BigDecimal;

import cn.deercare.finals.OrderState;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import cn.deercare.model.BaseModel;
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
    private String state = OrderState.ORDER_UNPAID;

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

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Order(){}
    public Order(String name, Integer type, BigDecimal amountPayable, BigDecimal amountPay,
                 Integer mode, String number){
        this.name = name;
        this.type = type;
        this.amountPayable = amountPayable;
        this.amountPay = amountPay;
        this.mode = mode;
        this.number = number;
    }


}
