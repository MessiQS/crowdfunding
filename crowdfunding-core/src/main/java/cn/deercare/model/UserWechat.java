package cn.deercare.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.omg.CORBA.INTERNAL;

/**
 * <p>
 * 
 * </p>
 *
 * @author JiangYuan
 * @since 2019-09-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_user_wechat")
public class UserWechat extends User {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    public void setId(Long id){
        this.id = id;
    }

    @JSONField(name="unionid")
    private String unionid;

    /**
     * 服务号openid
     */
    @JSONField(name="openid")
    private String accountOpenid;

    /**
     * 小程序openid
     */
    private String programOpenid;

    private String sessionKey;
    @JSONField(name="nickname")
    private String nickname;
    @JSONField(name="sex")
    private Integer gender;
    @JSONField(name="language")
    private String language;
    @JSONField(name="city")
    private String city;
    @JSONField(name="province")
    private String province;
    @JSONField(name="country")
    private String country;

    @TableField("avatarUrl")
    private String avatarUrl;

    /**
     * 用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
     */
    @TableField("subscribe")
    private Integer subscribe;

    /**
     * 用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间
     */
    @TableField("subscribe_time")
    private String subscribeTime;

    /**
     * 公众号运营者对粉丝的备注，公众号运营者可在微信公众平台用户管理界面对粉丝添加备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 用户所在的分组ID（兼容旧的用户分组接口）
     */
    @TableField("groupid")
    private Integer groupid;

    /**
     * 用户被打上的标签ID列表
     */
    @TableField("tagid_list")
    private String tagidList;

    /**
     * 返回用户关注的渠道来源，ADD_SCENE_SEARCH 公众号搜索，ADD_SCENE_ACCOUNT_MIGRATION 公众号迁移，ADD_SCENE_PROFILE_CARD 名片分享，ADD_SCENE_QR_CODE 扫描二维码，ADD_SCENE_PROFILE_ LINK 图文页内名称点击，ADD_SCENE_PROFILE_ITEM 图文页右上角菜单，ADD_SCENE_PAID 支付后关注，ADD_SCENE_OTHERS 其他
     */
    @TableField("subscribe_scene")
    private String subscribeScene;

    /**
     * 二维码扫码场景（开发者自定义）
     */
    @TableField("qr_scene")
    private Integer qrScene;

    /**
     * 二维码扫码场景描述（开发者自定义）
     */
    @TableField("qr_scene_str")
    private String qrSceneStr;

    private Long userId;


    public UserWechat(){}

    public UserWechat(String programOpenid){
        this.programOpenid = programOpenid;
    }



    public UserWechat(String programOpenid, Long userId){
        this.programOpenid = programOpenid;
        this.userId = userId;
    }

    public void cleanUser(){
        super.setId(null);
        super.setType(null);
        super.setToken(null);
        super.setCreateTime(null);
        super.setUpdateTime(null);
    }

    public void setUser (User user){
        super.setId(user.getId());
        super.setType(user.getType());
        super.setToken(user.getToken());
        super.setCreateTime(user.getCreateTime());
        super.setUpdateTime(user.getUpdateTime());
    }


}
