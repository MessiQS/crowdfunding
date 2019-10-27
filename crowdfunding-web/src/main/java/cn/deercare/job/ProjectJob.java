package cn.deercare.job;

import cn.deercare.controller.ProjectController;
import cn.deercare.enums.ProjectHotelState;
import cn.deercare.model.Project;
import cn.deercare.model.ProjectHotel;
import cn.deercare.model.User;
import cn.deercare.model.UserWechat;
import cn.deercare.service.ProjectService;
import cn.deercare.service.UserService;
import cn.deercare.utils.ProjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Configuration
@EnableScheduling
// PropertySource 只是单纯的添加配置文件到环境变量，如果变量是配置在application.yml，则不需要这个配置
// @PropertySource(value = "classpath:application.yml")
public class ProjectJob {

    private static Logger logger = LoggerFactory.getLogger(ProjectJob.class);

    private static Integer CROWDFUNDING_DAY = 14;

    @Autowired
    private ProjectService projectService = null;
    @Autowired
    private UserService userService = null;

    @Scheduled(cron = "${schedules.userWechatInfo}")
    private void projectPaySuccess(){
        logger.info("开始---判断众筹项目是否支付成功");
        try{
            // 获取众筹项目列表
            List<Project> projectList = projectService.listDetailed();
            for(int i = 0; i < projectList.size(); ++ i){
                Project project = projectList.get(i);
                // project的id，此project对象实为子类对象，id也为子类id
                Long projectId = null;
                if(project instanceof ProjectHotel){
                    ProjectHotel ph = (ProjectHotel) project;
                    projectId = ph.getProjectId();
                    if(ProjectHotelState.B != ProjectUtil.getProjectHotelState(ph.getState())){
                        // 获取该项目不属于众筹阶段，不作处理
                        continue;
                    }
                }
                // 获取项目众筹开始日期
                LocalDate beginTime = project.getCreateTime().toLocalDate();
                // 获取项目众筹结束日期
                LocalDate endTime = project.getCreateTime().plusDays(CROWDFUNDING_DAY).toLocalDate();
                // 获取当前日期
                LocalDate nowTime = LocalDate.now();
                if(endTime.isBefore(nowTime) || endTime.isEqual(nowTime)){
                    // 时间已过, 进入下一状态
                    new ProjectController().projectNextState(projectId, null);
                }
            }
        }catch (Exception e){
            logger.error("错误---判断众筹项目是否支付成功");
            logger.error(e.toString(), e);
        }
        logger.info("结束---判断众筹项目是否支付成功");
    }



    @Scheduled(cron = "${schedules.projectIncome}")
    private void projectIncome(){
        logger.info("开始项目收益推送");
        try{
            // 需要推送的用户
            Map<Long, UserWechat> userMap = new HashMap<Long, UserWechat>();
            // 获取众筹项目列表
            List<Project> projectList = projectService.listDetailed();
            for(int i = 0; i < projectList.size(); ++ i){
                Project project = projectList.get(i);
                if(project instanceof ProjectHotel){
                    ProjectHotel ph = (ProjectHotel) project;
                    if(ProjectHotelState.O != ProjectUtil.getProjectHotelState(ph.getState())){
                        logger.info("该项目不属于运营阶段，不作处理");
                        continue;
                    }
                    // 查询参与该项目的用户，发送推送
                    List<User> userList = userService.listByProject(ph.getProjectId());
                    userList.forEach(user -> {
                        // 判断用户类型
                        if(user instanceof UserWechat){
                            UserWechat uw = (UserWechat) user;
                            // 查询项目今日收益
                            // 添加进需要推送的用户集合中
                            userMap.put(uw.getUserId(), uw);
                        }
                    });
                }
            }
            logger.info("====");
            // 进行推送
            userMap.values().forEach(user ->{
                // 发送收益推送
                        /*
                        WechatAPICall.sendTemplateMessage(
                                WechatAccountInfo.DEERCARE_TEMPLATE_CASH_OUT_IDCash_ID,
                                "oPBltwI1mcGmi63mNbeU50sQ6L1E","https://www.baidu.com/",
                                "收益","remark","key1","key2","key3","key4");
                         */
            });

        }catch (Exception e){
            logger.error("错误---项目收益推送");
            logger.error(e.toString(), e);
        }
        logger.info("结束项目收益推送");

    }

    public static void main(String[] args) {
        LocalDateTime l = LocalDateTime.now();
        LocalDateTime s = l.minusDays(1);
        System.out.println(l);
        System.out.println(s);
        LocalDate ll = l.toLocalDate();
        LocalDate ss = s.toLocalDate();
        System.out.println(ll);
        System.out.println(ss);
        System.out.println(ll.isEqual(LocalDate.now()));

    }
}
