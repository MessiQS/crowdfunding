package cn.deercare.utils;

import cn.deercare.enums.UserType;
import cn.deercare.model.User;
import cn.deercare.model.UserWechat;
import cn.deercare.service.UserService;
import cn.deercare.service.UserWechatService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class UserUtil {


    public static UserType getUserType(Integer type){
        switch (type){
            case 1:
                return UserType.WECHAT;
        }
        return null;
    }

    public static <T extends User> T getUser(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        UserService userService = SpringUtil.getBean(UserService.class);
        // 查询user信息
        User user = (User) userService.getOne(Wrappers.<User>query()
                .eq("id", TokenUtils.getUserId(token))
                .select("*"));


        // 判断user类型
        switch (getUserType(Integer.parseInt(TokenUtils.getUserType(token)))){
            case WECHAT:
                 return (T) getUserWechat(user, request);
        }
        return null;
    }

    private static UserWechat getUserWechat(User user, HttpServletRequest request){
        UserWechatService userWechatService = SpringUtil.getBean(UserWechatService.class);
        UserWechat userWechat = userWechatService.getOne(Wrappers.<UserWechat>query()
                .eq("user_id", TokenUtils.getUserId(request.getHeader("Authorization")))
                .select("*"));
        userWechat.setUser(user);
        return userWechat;
    }

}
