package cn.deercare.wechat.finals;

import org.springframework.util.DigestUtils;

public class WechatAccountInfo {
    // --------- 服务号信息
    public static final String DEERCARE_APP_ID = "wx9e780c9f70da8465";

    public static final String DEERCARE_APP_SECRET = "d518e3d6bdbf37c30c548ef461dadab1";

    // 项目收益推送模板
    public static final String DEERCARE_TEMPLATE_CASH_OUT_IDCash_ID = "diqIVvf_JRDQbGph9GlOCeMXwxGX2EASlptFL5MOAiU";


    // --------- 小程序信息
    public static final String DEERCARE_PROGRAM_APP_ID = "wx4436516d451b7de9";

    public static final String DEERCARE_PROGRAM_APP_SECRET = "f577325ca266a421fe60751d71f25e70";

    // --------- 微信支付信息

    public static final String PAY_MERCHANT = "1556309821";

    // 操作密码（程序中用不到）
    public static final String PWD = "Caozuo123！";

    // 支付状态
    public static final String PAY_STATE_SUCCESS = "SUCCESS";
    public static final String PAY_STATE_REFUND = "REFUND";
    public static final String PAY_STATE_NOTPAY = "NOTPAY";
    public static final String PAY_STATE_CLOSED = "CLOSED";
    public static final String PAY_STATE_REVOKED = "REVOKED";
    public static final String PAY_STATE_USERPAYING = "USERPAYING";
    public static final String PAY_STATE_PAYERROR = "PAYERROR";

}
