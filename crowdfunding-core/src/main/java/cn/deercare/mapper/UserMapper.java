package cn.deercare.mapper;

import cn.deercare.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author JiangYuan
 * @since 2019-09-23
 */
public interface UserMapper extends BaseMapper<User> {

    @Select("select DISTINCT(tu.id) as 'distinct_id', tu.* from t_user tu " +
            "INNER JOIN t_user_project tup on tu.id = tup.user_id " +
            "where tup.project_id = 2")
    List<User> listByProject(Serializable id);

}
