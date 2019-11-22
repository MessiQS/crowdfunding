package cn.deercare.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import cn.deercare.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@TableName("t_user")
public class User extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField(exist = false)
    private Long mainId;

    public void setId(Long id){
        this.id = id;
        this.mainId = id;
    }

    /**
     * 用户类型1微信
     */
    private Integer type;

    private String token;

    private BigDecimal amount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 用户今日收益
     */
    @TableField(exist = false)
    private BigDecimal income = new BigDecimal(0);

    public User(){}
    public User(Long id){
        this.id = id;
    }
    public User(Long id, Integer type){
        this.id = id;
        this.type = type;
    }
    public User(Integer type, String token){
        this.type = type;
        this.token = token;
    }


}
