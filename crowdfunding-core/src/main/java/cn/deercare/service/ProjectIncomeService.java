package cn.deercare.service;

import cn.deercare.model.Project;
import cn.deercare.model.ProjectIncome;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author JiangYuan
 * @since 2019-10-13
 */
public interface ProjectIncomeService extends IService<ProjectIncome> {


    /**
     * 查询项目的历史收益总和
     * @param id 项目id
     * @param date 截止历史日期
     * @return
     */
    BigDecimal getProjectIncomeHistory(Serializable id, String date);



}
