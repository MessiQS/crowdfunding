package cn.deercare.mapper;

import cn.deercare.model.Project;
import cn.deercare.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author JiangYuan
 * @since 2019-09-23
 */
public interface ProjectMapper extends BaseMapper<Project> {
    /**
     * 查询用户参与的项目
     * @param user
     * @return
     */
    @Select("select DISTINCT(tp.id),tp.*,tpp.min,tpp.type as 'payment' from t_project tp " +
            "INNER JOIN t_project_payment tpp on tpp.id = tp.payment_id " +
            "INNER JOIN t_user_project tup on tup.project_id = tp.id " +
            "INNER JOIN t_user tu on tup.user_id = tu.id " +
            "where tu.id = #{id} and tup.verification = 2")
    List<Project> listByUser(User user);

    /**
     * 查询项目已筹集金额
     * @param id 项目id
     * @return
     */
    @Select(" select ifnull(sum(torder.amount_payable), 0) as 'accountAmount' from t_user_project tup " +
            " INNER JOIN t_order torder on torder.id = tup.order_id " +
            " where tup.verification = 2 and tup.project_id = #{id}")
    Project getProjectAccountAmount(Serializable id);


    /**
     * 查询参与项目的人数
     * @param id 项目id
     * @return
     */
    @Select("select count(DISTINCT(tup.user_id)) as 'persionCount' from t_project tp " +
            "INNER JOIN t_user_project tup on tup.project_id = tp.id " +
            "where tp.id = #{id} and tup.verification = 2")
    Integer getProjectPersonCount(Serializable id);

    /**
     * 查询用户在某项目中所占的比例
     * @param user 用户信息
     * @param project 项目信息
     * @return
     */
    @Select("select ifnull(sum(tup.proportion), 0) as 'proportion' from t_user_project tup " +
            "where tup.project_id = #{project.mainId} and tup.user_id = #{user.id} " +
            "and tup.verification = 2")
    BigDecimal getProjectProportionByUser(@Param("user") User user, @Param("project") Project project);

    /**
     * 查询用户在某项目中投资的本金
     * @param user 用户信息
     * @param project 项目信息
     * @return
     */
    @Select("select ifnull(sum(tor.amount_pay), 0) as 'principal' from t_user_project tup " +
            "INNER JOIN t_order tor on tor.id = tup.order_id " +
            "where tup.project_id = #{project.mainId} and tup.user_id = #{user.id}  " +
            "and tup.verification = 2")
    BigDecimal getProjectPrincipalByUser(@Param("user") User user, @Param("project") Project project);

}
