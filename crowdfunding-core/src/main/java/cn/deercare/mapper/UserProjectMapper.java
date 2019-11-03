package cn.deercare.mapper;

import cn.deercare.model.Project;
import cn.deercare.model.User;
import cn.deercare.model.UserProject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author JiangYuan
 * @since 2019-10-07
 */
public interface UserProjectMapper extends BaseMapper<UserProject> {

    @Select("SELECT distinct(tup.project_id), tup.user_id  from t_user_project tup where tup.user_id = #{id}")
    public List<UserProject> listByUser(User user);

    @Select("SELECT DISTINCT(tup.project_id)  from t_user_project tup")
    public List<UserProject> listProject(Project project);

    @Select("SELECT distinct(tup.user_id) from t_user_project tup ")
    public List<UserProject> listUser(User user);

}
