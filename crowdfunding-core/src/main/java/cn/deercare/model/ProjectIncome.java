package cn.deercare.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import cn.deercare.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDate;
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
 * @since 2019-10-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_project_income")
public class ProjectIncome extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 本日收入
     */
    private BigDecimal amount;

    private String conversion;

    private LocalDate date;

    private LocalDateTime createTime;

    private Long projectId;

    public ProjectIncome(){}
    public ProjectIncome(Long projectId, BigDecimal amount, LocalDate date, String conversion){
        this.projectId = projectId;
        this.amount = amount;
        this.date = date;
        this.conversion = conversion;
    }


}
