package cn.deercare.service.impl;

import cn.deercare.enums.UserType;
import cn.deercare.mapper.UserWechatMapper;
import cn.deercare.model.Project;
import cn.deercare.model.User;
import cn.deercare.mapper.UserMapper;
import cn.deercare.model.UserWechat;
import cn.deercare.service.UserService;
import cn.deercare.utils.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JiangYuan
 * @since 2019-09-23
 */
@Service
public class UserServiceImpl<T extends User> extends ServiceImpl<UserMapper, User> implements UserService<T> {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserWechatMapper userWechatMapper;

    public List<T> listByProject(Serializable id){
        return getListDetailed(userMapper.listByProject(id));
    }

    private List<T> getListDetailed(List<User> userList){
        List<T> userDetailedList = new ArrayList<T>();
        userList.forEach(user ->{
            // 获取用户类型
            UserType userType = UserUtil.getUserType(user.getType());
            // 查询详细
            switch (userType){
                case WECHAT:
                    UserWechat userWechat = userWechatMapper.selectOne(new QueryWrapper<UserWechat>()
                            .eq("user_id",user.getId())
                            .select("*"));
                    userWechat.setUser(user);
                    userDetailedList.add((T) userWechat);
                    break;
            }
        });
        return userDetailedList;
    }

}
