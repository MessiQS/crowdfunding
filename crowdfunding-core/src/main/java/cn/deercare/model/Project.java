package cn.deercare.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author JiangYuan
 * @since 2019-09-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_project")
public class Project extends BaseModel {

    private static final long serialVersionUID = 1L;

    public Project(){}

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(exist = false)
    private Long mainId;

    public void setId(Long id){
        this.id = id;
        this.mainId = id;
    }

    private String name;

    /**
     * 目标金额
     */
    private BigDecimal amount;

    private Integer type;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer paymentId;

    /**
     * 已众筹款项
     */
    @TableField(exist = false)
    private BigDecimal accountAmount;
    @TableField(exist = false)
    private ProjectPayment projectPayment;
    @TableField(exist = false)
    private List<UserProject> userProject;
    /**
     * 已参与众筹人数
     */
    @TableField(exist = false)
    private Integer personCount;
    /**
     * 今日收益（计算后，个人的收益）
     */
    @TableField(exist = false)
    private BigDecimal income;

    /**
     * 历史收益率
     */
    @TableField(exist = false)
    private BigDecimal incomeProportion;


}
