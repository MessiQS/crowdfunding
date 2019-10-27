package cn.deercare.job;

import cn.deercare.model.User;
import cn.deercare.model.UserWechat;
import cn.deercare.service.UserService;
import cn.deercare.service.UserWechatService;
import cn.deercare.wechat.api.WechatAPICall;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Configuration
@EnableScheduling
// PropertySource 只是单纯的添加配置文件到环境变量，如果变量是配置在application.yml，则不需要这个配置
// @PropertySource(value = "classpath:application.yml")
public class WechatJob {

    private static Logger logger = LoggerFactory.getLogger(WechatJob.class);

    @Autowired
    private UserWechatService userWechatService;
    @Autowired
    private UserService userService;


    // @Scheduled(cron = "${devops.web.admin}")
    @Scheduled(cron = "${schedules.userWechatInfo}")
    private void WechatInfoByAccount() {
        logger.info("开始---同步服务号用户信息");
        try {
            List<String> list = WechatAPICall.getAccountOpenIdList();
            // 获取用户信息
            list.forEach(str ->{
                JSONObject jsonObject = WechatAPICall.getUserInfoByAccount(str);
                UserWechat userWechat = userWechatService.getOne(Wrappers.<UserWechat>query()
                        .eq("unionid", jsonObject.getString("unionid"))
                        .select("*"));
                // 插入微信用户信息
                UserWechat uw = jsonObject.toJavaObject(UserWechat.class);
                uw.setAvatarUrl(jsonObject.getString("headimgurl"));
                if(userWechat == null){
                    // 先插入操作user表
                    User user = new User();
                    user.setType(1); // 微信用户
                    userService.save(user);
                    // 主键绑定，保存微信用户信息
                    uw.setUserId(user.getId());
                    userWechatService.save(uw);
                    return;
                }
                userWechatService.update(uw, Wrappers.<UserWechat>update()
                        .eq("unionid",uw.getUnionid()));
                /*
                userWechatService.update(null, Wrappers.<UserWechat>update()
                        .eq("unionid", jsonObject.getString("unionid"))
                        .set("account_openid", jsonObject.getString("openid"))
                        .set("subscribe", jsonObject.getString("subscribe"))
                        .set("subscribe_time", jsonObject.getString("subscribe_time"))
                        .set("remark", jsonObject.getString("remark"))
                        .set("groupid", jsonObject.getString("groupid"))
                        .set("tagid_list", jsonObject.getString("tagid_list"))
                        .set("subscribe_scene", jsonObject.getString("subscribe_scene"))
                        .set("qr_scene", jsonObject.getString("qr_scene"))
                        .set("qr_scene_str", jsonObject.getString("qr_scene_str")));

                 */
            });
        }catch (Exception e) {
            logger.error("错误---同步服务号用户信息");
            logger.error(e.toString(), e);
        }
        logger.info("结束---同步服务号用户信息");
    }
}
