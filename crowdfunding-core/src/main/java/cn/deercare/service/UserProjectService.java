package cn.deercare.service;

import cn.deercare.model.Project;
import cn.deercare.model.User;
import cn.deercare.model.UserProject;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author JiangYuan
 * @since 2019-10-07
 */
public interface UserProjectService extends IService<UserProject> {

    /**
     * 用户参与的项目
     * @param user 用户
     * @return
     */
    List<UserProject> listByUser(User user);

    /**
     * 参与众筹的项目
     * @param project 项目
     * @return
     */
    List<UserProject> listProject(Project project);

    /**
     * 参与众筹的用户
     * @param user 用户
     * @return
     */
    List<UserProject> listUser(User user);

}
