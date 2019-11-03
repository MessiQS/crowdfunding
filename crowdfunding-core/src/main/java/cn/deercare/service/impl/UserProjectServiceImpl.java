package cn.deercare.service.impl;

import cn.deercare.model.Project;
import cn.deercare.model.User;
import cn.deercare.model.UserProject;
import cn.deercare.mapper.UserProjectMapper;
import cn.deercare.service.UserProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JiangYuan
 * @since 2019-10-07
 */
@Service
public class UserProjectServiceImpl extends ServiceImpl<UserProjectMapper, UserProject> implements UserProjectService {

    @Autowired
    private UserProjectMapper userProjectMapper;

    public List<UserProject> listByUser(User user){
        return userProjectMapper.listByUser(user);
    }

    @Override
    public List<UserProject> listProject(Project project) {
        return userProjectMapper.listProject(project);
    }

    @Override
    public List<UserProject> listUser(User user) {
        return userProjectMapper.listUser(user);
    }

}
