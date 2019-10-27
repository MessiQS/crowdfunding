package cn.deercare.service;

import cn.deercare.model.Project;
import cn.deercare.model.ProjectPayment;
import cn.deercare.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author JiangYuan
 * @since 2019-10-05
 */
public interface ProjectPaymentService extends IService<ProjectPayment> {

    Object pay(Project project, BigDecimal amount, HttpServletRequest request);

}
