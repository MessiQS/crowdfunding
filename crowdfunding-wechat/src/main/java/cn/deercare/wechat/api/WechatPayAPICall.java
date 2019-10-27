package cn.deercare.wechat.api;

import cn.deercare.over.RestTemplate;
import cn.deercare.utils.StringUtil;
import cn.deercare.wechat.finals.WechatAccountInfo;
import cn.deercare.wechat.finals.WechatIFSInfo;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.DigestUtils;


import javax.net.ssl.SSLContext;
import javax.sound.midi.Soundbank;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WechatPayAPICall {

    // 支付终端标识（小程序)
    private static final String PAY_DEVICE_INFO = "PROGRAM";
    // 签名机密方式
    private static final String PAY_SIGN_TYPE = "MD5";
    // api秘钥（后台设置)
    private static final String PAY_API_SECRET = "kuIiYGo1Vp1iinSHfAS95aVWbEYK6jV9";
    // 支付币种
    private static final String PAY_CURRENCY = "CNY";
    // 交易类型 （小程序为JSAPI)
    private static final String PAY_TRADE_TYPE = "JSAPI";
    // 支付状态通知地址
    private static final String PAY_NOTIFY_URL = "http://a.deercare.cn/order-wechat/notify";
    // private static final String PAY_NOTIFY_URL = "http://y2701801c4.wicp.vip//order-wechat/notify";

    static{

    }

    public static String getSign(Map<String, Object> map){
        List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
        // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {
            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });
        // 构造签名键值对的格式
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> item : infoIds) {
            if (!StringUtils.isEmpty(item.getKey())) {
                String key = item.getKey();
                String val = item.getValue().toString();
                if (!StringUtils.isEmpty(val)) {
                    sb.append(key + "=" + val + "&");
                }
            }
        }
        // 拼接key
        sb.append("key=" + PAY_API_SECRET);
        String result = sb.toString();
        // System.out.println(result);
        // MD5加密
        result = DigestUtils.md5DigestAsHex(result.getBytes()).toUpperCase();
        return result;
    }

    /**
     * 获取预支付prepayId
     *
     * @return
     */
    public static String getPrepayId(String xml) {
        // String response = arrayToXml(sorted_map);
        // xmlToArray(response);
        Map<String, Object> result = StringUtil.xmlToArray(xml);
        String prepay_id = String.valueOf(result.get("prepay_id"));
        return prepay_id;
    }

    /**
     *
     * @param key 1.商品描述 2.商品详细 3.附加数据 4.系统内部订单号 5.订单总额（分）
     *            6.终端ip 7.系统内部商品id
     * @return
     */
    public static List<Object> unifiedOrder(Object... key){
        // 订单日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // 订单起始时间
        Calendar timeStart = Calendar.getInstance();
        Calendar timeExpire = timeStart;
        // 订单结束时间（30分钟)
        timeStart.add(Calendar.MINUTE, 30);
        SortedMap<String, Object> parMap = new TreeMap<String, Object>();
        // appid
        parMap.put("appid", WechatAccountInfo.DEERCARE_PROGRAM_APP_ID);
        // 商户号
        parMap.put("mch_id", WechatAccountInfo.PAY_MERCHANT);
        // 设备终端号（小程序)
        parMap.put("device_info", PAY_DEVICE_INFO);
        // 不超过32位的随机字符串
        parMap.put("nonce_str", StringUtil.createNoncestr());
        // 加密方式
        parMap.put("sign_type", PAY_SIGN_TYPE);
        // 商品描述
        parMap.put("body", key[0]);
        // 商品详细
        parMap.put("detail", key[1]);
        // 附加数据
        parMap.put("attach", key[2]);
        // 系统内部订单号
        parMap.put("out_trade_no", key[3]);
        // 币种（人民币)
        parMap.put("fee_type", PAY_CURRENCY);
        // 订单总额（单位：分)
        parMap.put("total_fee", key[4]);
        // 终端ip
        parMap.put("spbill_create_ip", key[5]);
        // 交易起始时间(订单生产时间)
        parMap.put("time_start", sdf.format(timeStart.getTime()));
        // 交易结束时间（订单失效时间)
        parMap.put("time_expire", sdf.format(timeExpire.getTime()));
        // 通知地址（异步接收微信支付结果通知的回调地址）
        parMap.put("notify_url", PAY_NOTIFY_URL);
        // 交易类型（小程序为JSAPI）
        parMap.put("trade_type", PAY_TRADE_TYPE);
        // 系统内部商品ID
        parMap.put("product_id", key[6]);
        // 指定支付方式（不限制信用卡)
        parMap.put("limit_pay","no_credit");
        // 用户标识（openId)
        parMap.put("openid", key[7]);
        // 获取签名
        parMap.put("sign", getSign(parMap));
        // 转换xml格式
        String xml = StringUtil.arrayToXml(parMap);
        RestTemplate restTemplate = RestTemplate.getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(xml, headers);
        // 发起请求获取结果
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(WechatIFSInfo.POST_PAY_UNIFIED_ORDER, entity, String.class);
        String result = responseEntity.getBody();
        String  req = parMap.toString();
        String  res = result;
        // System.out.println(result);
        // 获取prepayId
        String prepayId = getPrepayId(result).toString();
        // 二次签名，返回给客户端
        String nonceStr = StringUtil.createNoncestr();
        Object timeStamp = Calendar.getInstance().getTimeInMillis() / 1000;
        String packages = "prepay_id=" + prepayId;
        parMap.clear();
        parMap.put("appId", WechatAccountInfo.DEERCARE_PROGRAM_APP_ID);
        parMap.put("nonceStr", nonceStr);
        parMap.put("timeStamp", timeStamp.toString());
        parMap.put("signType", WechatPayAPICall.PAY_SIGN_TYPE);
        parMap.put("package", packages);
        String signAgain = getSign(parMap);
        // 返回二次签名信息(前端使用)
        JSONObject signAgainJSON = new JSONObject();
        signAgainJSON.put("nonceStr", nonceStr);
        signAgainJSON.put("timeStamp", timeStamp.toString());
        signAgainJSON.put("package", packages);
        signAgainJSON.put("paySign", signAgain);
        return Arrays.asList(
                req, // 统一下单请求参数
                res, // 统一下单返回结果
                parMap.toString(), // 二次签名参数
                // 二次签名信息（前端使用）
                signAgainJSON
                );
    }

    public static Map<String, Object> orderQuery(String order){
        SortedMap<String, Object> parMap = new TreeMap<String, Object>();
        // appid
        parMap.put("appid", WechatAccountInfo.DEERCARE_PROGRAM_APP_ID);
        // 商户号
        parMap.put("mch_id", WechatAccountInfo.PAY_MERCHANT);
        parMap.put("out_trade_no", order);
        parMap.put("nonce_str", StringUtil.createNoncestr());
        parMap.put("sign_type", PAY_SIGN_TYPE);
        parMap.put("sign", getSign(parMap));
        String xml = StringUtil.arrayToXml(parMap);
        RestTemplate restTemplate = RestTemplate.getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(xml, headers);
        // 发起请求获取结果
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(WechatIFSInfo.POST_PAY_ORDER_QUERY, entity, String.class);
        String result = responseEntity.getBody();
        return StringUtil.xmlToArray(result);
        // return JSONObject.parseObject(result);
    }
/*

    <xml><return_code><![CDATA[SUCCESS]]></return_code>
<return_msg><![CDATA[OK]]></return_msg>
<appid><![CDATA[wx4436516d451b7de9]]></appid>
<mch_id><![CDATA[1556309821]]></mch_id>
<device_info><![CDATA[PROGRAM]]></device_info>
<nonce_str><![CDATA[UVXKuLFqxpbSUeEa]]></nonce_str>
<sign><![CDATA[846A29A9A34430FEA7A291FEF7E7ACEB]]></sign>
<result_code><![CDATA[SUCCESS]]></result_code>
<prepay_id><![CDATA[wx01181446091248d4d088179a1075436200]]></prepay_id>
<trade_type><![CDATA[JSAPI]]></trade_type>
</xml>
*/

    public static void main(String[] args) {
        SortedMap<String, Object> parMap = new TreeMap<String, Object>();
        System.out.println(Calendar.getInstance().getTimeInMillis() / 1000);
        parMap.put("appid", WechatAccountInfo.DEERCARE_PROGRAM_APP_ID);
        // 商户号
        parMap.put("mch_id", WechatAccountInfo.PAY_MERCHANT);
        parMap.put("out_trade_no", "631970807387521024");
        parMap.put("nonce_str", StringUtil.createNoncestr());
        parMap.put("sign_type", PAY_SIGN_TYPE);
        parMap.put("sign", getSign(parMap));
        System.out.println(parMap.toString());
        String xml = StringUtil.arrayToXml(parMap);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(xml, headers);
        System.out.println("------");
        // 发起请求获取结果
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(WechatIFSInfo.POST_PAY_ORDER_QUERY, entity, String.class);
        String result = responseEntity.getBody();
        System.out.println(result);
        System.out.println("0----------------");
    }
}
