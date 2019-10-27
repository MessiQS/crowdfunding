package cn.deercare.utils;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * 计算项目收益
 */
public class ProjectIncomeUtils {

    /**
     * 计算本项目获取收益 (传入今日总收益，计算为今日收益金额，传入历史总收益，计算为历史收益金额)
     * 收益金额 = 总收益 * 0.5（平台占比50%） * 本人占比 * 0.01（100%）
     * @param amount 总收益
     * @param proportionAmount 本人占比
     * @return 某人该项目收益
     */
    public static BigDecimal getIncomeByOne(BigDecimal amount, BigDecimal proportionAmount){
        return amount.multiply(cn.deercare.finals.ProjectIncome.PLATFORM_PROPORTION)
                .multiply(proportionAmount.multiply(new BigDecimal(0.01)));
    }

    /**
     * 计算收益率（传入今日总收益，计算为今日收益率，传入历史总收益，计算为历史收益率)
     * 收益率 = 历史收益 / 本金 * 100
     * 使用四舍五入，保留小数点后两位
     * @param amount 总收益
     * @param principal 本金
     * @return 某人收益率
     */
    public static BigDecimal getIncomeProportionByOne(BigDecimal amount, BigDecimal principal){
        return amount.divide(principal, 2, ROUND_HALF_UP).multiply(new BigDecimal(100));
    }
}
