package cn.deercare.service;

import cn.deercare.model.User;
import cn.deercare.model.UserWechat;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author JiangYuan
 * @since 2019-09-23
 */
public interface UserService<T extends User> extends IService<User> {

    List<T> listByProject(Serializable id);

    /**
     * 用户提现操作
     * @param user 用户信息
     * @param amount 提现金额
     */
    void userWithdrawCash(UserWechat user, BigDecimal amount);

    /**
     * 用户增加余额
     * @param amount 增加的金额
     * @param id 用户id
     */
    void userAmountPlus(String amount, Serializable id);

}
