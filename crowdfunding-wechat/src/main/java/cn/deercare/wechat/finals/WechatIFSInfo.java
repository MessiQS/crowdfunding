package cn.deercare.wechat.finals;

public class WechatIFSInfo {

    // 获取access_token
    public static final String GET_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={APPID}&secret={APPSECRET}";

    // code获取用户openid等信息
    public static final String GET_PROGRAM_USER_AUTH = "https://api.weixin.qq.com/sns/jscode2session?appid={APPID}&secret={APPSECRET}&js_code={JSCODE}&grant_type=authorization_code";

    // 发送模板消息
    public static final String POST_TEMPLATE_MESSAGE_SEND = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token={ACCESS_TOKEN}";

    // 获取服务号关注者列表
    public static final String GET_USER_LIST = "https://api.weixin.qq.com/cgi-bin/user/get?access_token={ACCESS_TOKEN}&next_openid={NEXT_OPENID}";

    // 获取用户信息
    public static final String GET_ACCOUNT_USER_INFO = "https://api.weixin.qq.com/cgi-bin/user/info?access_token={ACCESS_TOKEN}&openid={OPENID}&lang=zh_CN";

    // 统一下单接口
    public static final String POST_PAY_UNIFIED_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    // 订单查询接口
    public static final String POST_PAY_ORDER_QUERY = "https://api.mch.weixin.qq.com/pay/orderquery";


}
