package cn.deercare.job;

import cn.deercare.controller.ProjectController;
import cn.deercare.enums.ProjectHotelState;
import cn.deercare.finals.wechat.WechatAccountInfo;
import cn.deercare.model.*;
import cn.deercare.service.*;
import cn.deercare.utils.ProjectIncomeUtils;
import cn.deercare.utils.ProjectUtil;
import cn.deercare.wechat.api.WechatAPICall;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    @Autowired
    private UserWechatService userWechatService = null;
    @Autowired
    private ProjectIncomeService projectIncomeService = null;
    @Autowired
    private UserProjectService userProjectService = null;

    @Scheduled(cron = "${schedules.userWechatInfo}")
    private void projectPaySuccess(){
        logger.info("开始---判断众筹项目是否成功");
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
            logger.error("错误---判断众筹项目是否成功");
            logger.error(e.toString(), e);
        }
        logger.info("结束---判断众筹项目是否成功");
    }



    @Scheduled(cron = "${schedules.projectIncome}")
    private void projectIncome(){
        logger.info("开始项目收益推送");
        try{
            // 需要推送的用户
            Map<Long, UserWechat> userMap = new HashMap<Long, UserWechat>();
            // 获取参与众筹的用户列表
            List<UserProject> userProjectList = userProjectService.listUser(null);
            userProjectList.forEach(userProject -> {
                // 查询用户微信信息
                UserWechat userWechat = userWechatService.getOne(Wrappers.<UserWechat>query()
                        .select("*")
                        .eq("user_id", userProject.getUserId()));
                // 获取用户参与的项目
                List<Project> projectList = projectService.listDetailedByUser(new User(userWechat.getUserId()));
                projectList.forEach(project -> {
                    if(project instanceof ProjectHotel){
                        ProjectHotel ph = (ProjectHotel) project;
                        if(ProjectHotelState.O != ProjectUtil.getProjectHotelState(ph.getState())){
                            logger.info("该项目不属于运营阶段，不作处理");
                            return;
                        }
                        // 查询该项目的今日收益
                        ProjectIncome projectIncome = projectIncomeService.getOne(Wrappers.<ProjectIncome>query()
                                .eq("project_id", ph.getMainId())
                                .eq("date", LocalDate.now().minusDays(1)));
                        // 查询用户在该项目中的占比
                        BigDecimal proportionAmount = projectService.getProjectProportionByUser(new User(userWechat.getUserId()), project);
                        // 计算出该用户的今日收益
                        BigDecimal income = ProjectIncomeUtils.getIncomeByOne(projectIncome.getAmount(),proportionAmount);
                        userWechat.setIncome(userWechat.getIncome().add(income));
                        // 添加进需要推送的集合
                        userMap.put(userWechat.getId(), userWechat);
                    }
                });

            });
            logger.info("====");
            // 进行推送
            userMap.values().forEach(user ->{
                // 发送收益推送
                WechatAPICall.sendTemplateMessage(
                        WechatAccountInfo.DEERCARE_TEMPLATE_INCOME_ID,
                        user.getAccountOpenid(),"https://www.baidu.com",
                        "您所参与的项目今日收益已到账","感谢您的支持！",
                        user.getIncome().toString(), LocalDate.now().minusDays(1).toString());
            });
        }catch (Exception e){
            logger.error("错误---项目收益推送");
            logger.error(e.toString(), e);
        }
        logger.info("结束项目收益推送");
    }


    public static void main(String[] args) {

    }
}
