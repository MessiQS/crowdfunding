package cn.deercare.wechat.api;


import cn.deercare.over.RestTemplate;
import cn.deercare.wechat.finals.WechatAccountInfo;
import cn.deercare.wechat.finals.WechatIFSInfo;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WechatAPICall {

    private static Logger logger = LoggerFactory.getLogger(WechatAPICall.class);

    private static String accessToken = "accessToken";

    private static Map<String, Object> parMap = new HashMap<String, Object>();
    private static RestTemplate restTemplate = RestTemplate.getRestTemplate();

    // 发送模板消息失败重试次数
    private static Integer templateMsgRetry = 3;

    /**/
    public static void getAccessToken(){
        parMap.clear();
        parMap.put("APPID", WechatAccountInfo.DEERCARE_APP_ID);
        parMap.put("APPSECRET",WechatAccountInfo.DEERCARE_APP_SECRET);
        String result = restTemplate.getForObject(WechatIFSInfo.GET_ACCESS_TOKEN_URL, String.class, parMap);
        accessToken = JSONObject.parseObject(result).getString("access_token");
    }

    public static JSONObject getUserInfoByProgram(String code){
        parMap.clear();
        parMap.put("APPID", WechatAccountInfo.DEERCARE_PROGRAM_APP_ID);
        parMap.put("APPSECRET",WechatAccountInfo.DEERCARE_PROGRAM_APP_SECRET);
        parMap.put("JSCODE", code);
        String result = restTemplate.getForObject(WechatIFSInfo.GET_PROGRAM_USER_AUTH, String.class, parMap);
        return JSONObject.parseObject(result);
    }

    // 获取公众号关注列表
    public static List<String> getAccountOpenIdList(){
        String nextOpenId = "";
        int count = 0;
        List<String> list = null;
        List<String> resultList = new ArrayList<String>();
        do{
            parMap.clear();
            parMap.put("ACCESS_TOKEN", accessToken);
            parMap.put("NEXT_OPENID", nextOpenId);
            String result = restTemplate.getForObject(WechatIFSInfo.GET_USER_LIST, String.class, parMap);
            int k = 0;
            while (getAgainAccessToken(result) && k < templateMsgRetry){
                k ++;
                logger.info("{}重新获取accessToken", "服务号获取用户openList");
                parMap.clear();
                parMap.put("ACCESS_TOKEN", accessToken);
                parMap.put("NEXT_OPENID", nextOpenId);
                result = restTemplate.getForObject(WechatIFSInfo.GET_USER_LIST, String.class, parMap);
            }
            // 解析返回结果
            nextOpenId = JSONObject.parseObject(result).getString("next_openid");
            count = JSONObject.parseObject(result).getInteger("count");
            if(count > 0){
                // 获取openId列表
                list = JSONObject.parseObject(result).getJSONObject("data").getJSONArray("openid").toJavaList(String.class);
                resultList.addAll(list);
            }
        }while(count > 0);
        return list;
    }

    public static JSONObject getUserInfoByAccount(String openId){
        parMap.clear();
        parMap.put("ACCESS_TOKEN", accessToken);
        parMap.put("OPENID", openId);
        String result = restTemplate.getForObject(WechatIFSInfo.GET_ACCOUNT_USER_INFO, String.class, parMap);
        int k = 0;
        while(getAgainAccessToken(result) && k < templateMsgRetry){
            k ++;
            logger.info("{}重新获取accessToken", "服务号获取用户信息");
            parMap.clear();
            parMap.put("ACCESS_TOKEN", accessToken);
            parMap.put("OPENID", openId);
            result = restTemplate.getForObject(WechatIFSInfo.GET_ACCOUNT_USER_INFO, String.class, parMap);
        }
        return JSONObject.parseObject(result);
    }

    /**
     * 发送推送信息
     * @param templateId 模板id
     * @param opendId 推送用户的openId
     * @param key 第一个为first，第二个为remark，其他为中间信息
     */
    public static void sendTemplateMessage(String templateId, String opendId, String url,
                                           String firstV, String remarkV, String... key){
        String blueColor = "#173177";
        String redColor = "#FF0000";
        JSONObject json = new JSONObject();
        JSONObject data = new JSONObject();
        json.put("touser", opendId);
        json.put("template_id", templateId);
        json.put("url", url);
        json.put("topcolor", blueColor);
        JSONObject first = new JSONObject();
        first.put("value", firstV);
        first.put("color", blueColor);
        data.put("first", first);
        JSONObject remark = new JSONObject();
        remark.put("value", remarkV);
        remark.put("color", blueColor);
        data.put("remark", remark);
        for(int i = 0; i < key.length; ++ i){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", key[i]);
            jsonObject.put("color", blueColor);
            data.put("keyword" + (i + 1), jsonObject);
        }
        json.put("data", data);
        parMap.clear();
        parMap.put("ACCESS_TOKEN", accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(WechatIFSInfo.POST_TEMPLATE_MESSAGE_SEND, entity, String.class, parMap);
        String result = responseEntity.getBody();
        int k = 0;
        while(getAgainAccessToken(result) && k < templateMsgRetry){
            k ++;
            logger.info("{}重新获取accessToken", "推送模板消息");
            parMap.clear();
            parMap.put("ACCESS_TOKEN", accessToken);
            responseEntity = restTemplate.postForEntity(WechatIFSInfo.POST_TEMPLATE_MESSAGE_SEND, entity, String.class, parMap);
            result = responseEntity.getBody();
        }
        logger.info(result);
    }

    // 是否需要重新获取access_token
    private static boolean getAgainAccessToken(String result){
        JSONObject jsonObject = JSONObject.parseObject(result);
        if(jsonObject.getString("errcode") != null){
             if(jsonObject.getString("errcode").equals("40001")
                     || jsonObject.getString("errcode").equals("41001") ){
                 getAccessToken();
                 return true;
             }
        }
        return false;
    }
}
