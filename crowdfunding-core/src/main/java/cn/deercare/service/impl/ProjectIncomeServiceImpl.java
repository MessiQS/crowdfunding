package cn.deercare.service.impl;

import cn.deercare.model.ProjectIncome;
import cn.deercare.mapper.ProjectIncomeMapper;
import cn.deercare.service.ProjectIncomeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JiangYuan
 * @since 2019-10-13
 */
@Service
public class ProjectIncomeServiceImpl extends ServiceImpl<ProjectIncomeMapper, ProjectIncome> implements ProjectIncomeService {

    @Autowired
    private ProjectIncomeMapper projectIncomeMapper;

    @Override
    public BigDecimal getProjectIncomeHistory(Serializable id, String date) {
        return projectIncomeMapper.getProjectIncomeHistory(id, date);
    }
}
