package cn.deercare.mapper;

import cn.deercare.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author JiangYuan
 * @since 2019-09-23
 */
public interface UserMapper extends BaseMapper<User> {

    @Select("select DISTINCT(tu.id) as 'distinct_id', tu.* from t_user tu " +
            "INNER JOIN t_user_project tup on tu.id = tup.user_id " +
            "where tup.project_id = #{id}")
    List<User> listByProject(Serializable id);

    /**
     * 增加用户余额
     * @param amount 增加的金额
     * @param id 用户id
     */
    @Update("update t_user set amount = amount + #{amount} where id = #{id}")
    void userAmountPlus(String amount, Serializable id);

    /**
     * 减少用户余额
     * @param amount 减少的金额
     * @param id 用户id
     */
    @Update("update t_user set amount = amount - #{amount} where id = #{id}")
    void userAmountMinus(String amount, Serializable id);

}
