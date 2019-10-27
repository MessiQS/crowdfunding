package cn.deercare.service;

import cn.deercare.model.Project;
import cn.deercare.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author JiangYuan
 * @since 2019-09-23
 */
public interface ProjectService<T extends  Project> extends IService<Project> {

    List<T> listDetailedByUser(User user);

    List<T> listDetailed();

    T getDetailedById(Serializable id);

    /**
     * 该项目已众筹款项
     * @param id 该众筹项目id
     * @return
     */
    Project getProjectAccountAmount(Serializable id);

    /**
     * 查询用户在某项目中所占的比例
     * @param user 用户信息
     * @param project 项目信息
     * @return
     */
    BigDecimal getProjectProportionByUser(User user, Project project);

    /**
     * 查询用户在某项目中投资的本金
     * @param user 用户信息
     * @param project 项目信息
     * @return
     */
    BigDecimal getProjectPrincipalByUser(User user, Project project);

}
