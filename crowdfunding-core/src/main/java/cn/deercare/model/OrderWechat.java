package cn.deercare.model;

import cn.deercare.wechat.finals.WechatAccountInfo;
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
@TableName("t_order_wechat")
public class OrderWechat extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 请求参数（统一下单接口）
     */
    private String request;

    /**
     * 返回参数（统一下单接口）
     */
    private String result;

    /**
     * 请求参数（二次签名）
     */
    private String requestSecond;

    /**
     * 微信通知
     */
    private String resultNotify;

    /**
     * 微信支付状态
     */
    private String state = WechatAccountInfo.PAY_STATE_NOTPAY;

    private Long orderId;

    private String orderNum;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public OrderWechat(){}
    public OrderWechat(String request, String result, String requestSecond,
                       Long orderId, String orderNum){
        this.request = request;
        this.result = result;
        this.requestSecond = requestSecond;
        this.orderId = orderId;
        this.orderNum = orderNum;
    }


}
