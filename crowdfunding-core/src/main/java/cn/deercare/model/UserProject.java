package cn.deercare.model;

import cn.deercare.finals.wechat.WechatPayVerification;
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
 * @since 2019-10-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_user_project")
public class UserProject extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long projectId;

    private Long orderId;

    /**
     * 本次投资占比
     */
    private Double proportion;

    /**
     * 1未验证，2已验证通过，3验证未通过（查询微信支付，返回支付成功为验证通过，不信任
     */
    private Integer verification = WechatPayVerification.UNVERIFIED;

    private LocalDateTime createTime;

    public UserProject(){}
    public UserProject(Long userId, Long projectId, Long orderId, Double proportion){
        this.userId = userId;
        this.projectId = projectId;
        this.orderId = orderId;
        this.proportion = proportion;
    }

}
