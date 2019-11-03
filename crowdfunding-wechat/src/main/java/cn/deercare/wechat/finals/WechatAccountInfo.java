package cn.deercare.wechat.finals;

import org.springframework.util.DigestUtils;

public class WechatAccountInfo {
    // --------- 服务号信息
    public static final String DEERCARE_APP_ID = "wx9e780c9f70da8465";

    public static final String DEERCARE_APP_SECRET = "d518e3d6bdbf37c30c548ef461dadab1";

    // 项目状态变更
    public static final String DEERCARE_TEMPLATE_STATECHANGE_ID = "TjvcpLf7ZUgVjYSebwF7qXKc8lfWD_ilMIIzu5F5nkU";
    // 项目众筹失败
    public static final String DEERCARE_TEMPLATE_FAIL_ID = "DhHXS8BNsJLeykfA6MHHIIMyNjJEiWOuSc20P2gU56E";
    // 项目众筹成功
    public static final String DEERCARE_TEMPLATE_SUCCESS_ID = "KlMIiQ8icZ23aGoj2ihenP_Lga1Dq1B3JEKRMgjlw8w";
    // 项目收益
    public static final String DEERCARE_TEMPLATE_INCOME_ID = "cUua00yeKc_WpOF2fyQU5sWFvL0KmwcV2AxDEJ84WYo";
    // 项目消费成功通知
    public static final String DEERCARE_TEMPLATE_PAY_ID = "aKCLt5D3junUl1Uvq9Mj_nwwUKgoffikGJ71PJ1UJ-8";
    // 项目提现通知
    public static final String DEERCARE_TEMPLATE_ENCHASHMENT_ID = "pPM3YaTWvhtmV_rjPrNLMl5GVUnOepU_b3JmwDjeCIE";
    // 项目众筹失败退款
    public static final String DEERCARE_TEMPLATE_REFUND_ID = "tCxlpa0xUC3KXVSVT7DDlqoiJcmyYWCmMGrrvVHMzPg";

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
