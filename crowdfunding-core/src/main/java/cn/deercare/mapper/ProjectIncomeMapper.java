package cn.deercare.mapper;

import cn.deercare.model.ProjectIncome;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author JiangYuan
 * @since 2019-10-13
 */
public interface ProjectIncomeMapper extends BaseMapper<ProjectIncome> {

    /**
     * 查询项目的历史收益总和
     * @param id 项目id
     * @param date 截止历史日期
     * @return
     */
    @Select("select ifnull(sum(tpi.amount), 0) as 'amount' from t_project_income tpi " +
            "where tpi.project_id = #{id} and tpi.date <= #{date}")
    BigDecimal getProjectIncomeHistory(Serializable id, String date);

}
