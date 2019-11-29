package cn.deercare.service.impl;

import cn.deercare.enums.ProjectType;
import cn.deercare.finals.wechat.WechatPayVerification;
import cn.deercare.mapper.ProjectHotelMapper;
import cn.deercare.mapper.ProjectMapper;
import cn.deercare.mapper.ProjectPaymentMapper;
import cn.deercare.mapper.UserProjectMapper;
import cn.deercare.model.*;
import cn.deercare.service.ProjectService;
import cn.deercare.utils.ProjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
public class ProjectServiceImpl<T extends Project> extends ServiceImpl<ProjectMapper, Project> implements ProjectService<T>{

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ProjectHotelMapper projectHotelMapper;
    @Autowired
    private ProjectPaymentMapper projectPaymentMapper;
    @Autowired
    private UserProjectMapper userProjectMapper;

    @Override
    public List<T> listDetailedByUser(User user) {
        List<T> list = getListDetailed(projectMapper.listJustByUser(user));
        // 循环获取每个项目的参与细节
        list.forEach(obj -> {
            List<UserProject> userProjectList = userProjectMapper.selectList(new QueryWrapper<UserProject>()
                    .eq("verification", WechatPayVerification.VERIFICATION_SUCCESS)
                    .eq("project_id", obj.getId()));
            obj.setUserProject(userProjectList);
        });
        return list;
    }
    @Override
    public List<T> listDetailed(){
        return getListDetailed(projectMapper.selectList(null));
        // return getListDetailed(projectMapper.list());
    }
    @Override
    public T getDetailedById(Serializable id){
        return getListDetailed(Arrays.asList(projectMapper.selectById(id))).get(0);
        // return getListDetailed(Arrays.asList(projectMapper.getById(id))).get(0);
    }

    @Override
    public Project getProjectAccountAmount(Serializable id) {
        return projectMapper.getProjectAccountAmount(id);
    }

    @Override
    public BigDecimal getProjectProportionByUser(User user, Project project) {
        return projectMapper.getProjectProportionByUser(user, project);
    }

    @Override
    public BigDecimal getProjectPrincipalByUser(User user, Project project) {
        return projectMapper.getProjectPrincipalByUser(user, project);
    }

    // 根据众筹项目类别查询详细
    private List<T> getListDetailed(List<Project> projectList){
        List<T> projectDetailedList = new ArrayList<T>();
        projectList.forEach(project -> {
            // 查询项目付款方式
            ProjectPayment projectPayment = projectPaymentMapper.selectById(project.getPaymentId());
            project.setProjectPayment(projectPayment);
            // 查询该项目已众筹款项
            project.setAccountAmount(projectMapper.getProjectAccountAmount(project.getId()).getAccountAmount());
            // 查询该项目已参与的人数
            project.setPersonCount(projectMapper.getProjectPersonCount(project.getId()));
            // 获取项目类型
            ProjectType projectType = ProjectUtil.getProjectType(project.getType());
            // 查询详细
            switch (projectType){
                case HOTEL:
                    ProjectHotel projectHotel = projectHotelMapper.selectOne(new QueryWrapper<ProjectHotel>()
                            .eq("project_id",project.getId())
                            .select("*"));
                    projectHotel.setProject(project);
                    projectDetailedList.add((T) projectHotel);
                    break;
            }
        });
        return projectDetailedList;
    }

    public static void main(String[] args) {

    }


}
