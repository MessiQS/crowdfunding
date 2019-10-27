package cn.deercare.service;

import cn.deercare.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author JiangYuan
 * @since 2019-09-23
 */
public interface UserService<T extends User> extends IService<User> {

    List<T> listByProject(Serializable id);

}
