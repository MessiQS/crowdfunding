package cn.deercare.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@TableName("t_project_hotel")
public class ProjectHotel extends Project {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    public void setId(Long id){
        this.id = id;
    }
    /**
     * b众筹开始,f众筹失败,s众筹成功,p工厂生产,l酒店铺设,o开始运营,t自行填补,r众筹失败退款
     */
    private String state;

    private String address;

    private String titleImg;

    private String detailedImg;

    /**
     * 坐标
     */
    private String coordinate;

    /**
     * 开业时间
     */
    private LocalDateTime openingTime;

    /**
     * 重新装修时间
     */
    private LocalDateTime renovationTime;

    /**
     * 房间数
     */
    private Integer roomCount;

    /**
     * 平均入住率
     */
    private String averageOccupancyRate;

    /**
     * 最高入住率
     */
    private String maximumOccupancyRate;

    /**
     * 最低入住率
     */
    private String minimumOccupancyRate;

    /**
     * 计划铺设设备数量
     */
    private Integer equipmentCount;

    /**
     * 最高价格
     */
    private BigDecimal maximumPrice;

    /**
     * 最低价格
     */
    private BigDecimal minimumPrice;

    /**
     * 特色
     */
    private String characteristic;

    /**
     * 预计回报率（ 年化%）
     */
    private Integer rateOfReturn;

    private String info;

    private Long projectId;

    public ProjectHotel(){}

    public ProjectHotel(Long k){
        super.setId(k);
        System.out.println(super.getId());
    }

    public void setProject(Project project){
        super.setId(project.getId());
        this.setName(project.getName());
        this.setAmount(project.getAmount());
        this.setType(project.getType());
        this.setPaymentId(project.getPaymentId());
        this.setCreateTime(project.getCreateTime());
        this.setUpdateTime(project.getUpdateTime());
        this.setProjectPayment(project.getProjectPayment());
        this.setAccountAmount(project.getAccountAmount());
        this.setPersonCount(project.getPersonCount());
    }


}
