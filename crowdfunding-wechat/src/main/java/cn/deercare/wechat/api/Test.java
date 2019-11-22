package cn.deercare.wechat.api;

import cn.deercare.finals.wechat.WechatAccountInfo;
import cn.deercare.finals.wechat.WechatIFSInfo;
import cn.deercare.over.RestTemplate;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

    public static void openList(){
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> parMap = new HashMap<String, Object>();
        parMap.clear();
        parMap.put("APPID", WechatAccountInfo.DEERCARE_APP_ID);
        parMap.put("APPSECRET", WechatAccountInfo.DEERCARE_APP_SECRET);
        String result = restTemplate.getForObject(WechatIFSInfo.GET_ACCESS_TOKEN_URL, String.class, parMap);
        String accestoken = JSONObject.parseObject(result).getString("access_token");

        parMap.clear();
        parMap.put("ACCESS_TOKEN", accestoken);
        parMap.put("NEXT_OPENID", "oPBltwLMmQyGXkgP36Q-5lSc1wNI");
        String result2 = restTemplate.getForObject(WechatIFSInfo.GET_USER_LIST, String.class, parMap);
        System.out.println(result2);
        List<String> list = JSONObject.parseObject(result2).getJSONObject("data").getJSONArray("openid").toJavaList(String.class);
        System.out.println(Arrays.toString(list.toArray()));

    }

    public static void send(){
        System.out.println("0---");
        JSONObject json = new JSONObject();
        JSONObject first = new JSONObject();
        first.put("value", "您的项目收益已清算");
        first.put("color", "#173177");
        JSONObject project = new JSONObject();
        project.put("value","滨海湾金沙酒店");
        project.put("color","#173177");
        JSONObject key2 = new JSONObject();
        key2.put("value","1.00元");
        key2.put("color","#173177");
        JSONObject key3 = new JSONObject();
        key3.put("value","10000000000.00元");
        key3.put("color","#173177");
        JSONObject key4 = new JSONObject();
        key4.put("value","10000000000%");
        key4.put("color","#173177");
        JSONObject remark = new JSONObject();
        remark.put("value", "感谢您对项目的支持，您本月的收益已打入个人账户中，请继续支持小鹿健康");
        remark.put("color", "#FF0000");

        json.put("touser", "oPBltwI1mcGmi63mNbeU50sQ6L1E");
        json.put("template_id", "diqIVvf_JRDQbGph9GlOCeMXwxGX2EASlptFL5MOAiU");
        json.put("url","http://www.baidu.com");
        json.put("topcolor","#FF0000");

        JSONObject data = new JSONObject();
        data.put("first", first);
        data.put("keyword1", project);
        data.put("keyword2", key2);
        data.put("keyword3", key3);
        data.put("keyword4", key4);
        data.put("remark", remark);
        json.put("data", data);


        System.out.println("0---");
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> parMap = new HashMap<String, Object>();
        parMap.clear();
        parMap.put("APPID", WechatAccountInfo.DEERCARE_APP_ID);
        parMap.put("APPSECRET",WechatAccountInfo.DEERCARE_APP_SECRET);
        String result = restTemplate.getForObject(WechatIFSInfo.GET_ACCESS_TOKEN_URL, String.class, parMap);
        String accestoken = JSONObject.parseObject(result).getString("access_token");
        parMap.clear();
        parMap.put("ACCESS_TOKEN", accestoken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(WechatIFSInfo.POST_TEMPLATE_MESSAGE_SEND, entity, String.class, parMap);
        String result2 = responseEntity.getBody();


        System.out.println(result2);
    }

    public static void getUserInfo(){
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> parMap = new HashMap<String, Object>();
        parMap.clear();
        parMap.put("APPID", WechatAccountInfo.DEERCARE_APP_ID);
        parMap.put("APPSECRET",WechatAccountInfo.DEERCARE_APP_SECRET);
        String result = restTemplate.getForObject(WechatIFSInfo.GET_ACCESS_TOKEN_URL, String.class, parMap);
        String accestoken = JSONObject.parseObject(result).getString("access_token");
        System.out.println(result);
        parMap.clear();
        parMap.put("ACCESS_TOKEN", accestoken);
        parMap.put("OPENID", "oPBltwI1mcGmi63mNbeU50sQ6L1E");
        String result2 = restTemplate.getForObject(WechatIFSInfo.GET_ACCOUNT_USER_INFO, String.class, parMap);
        System.out.println(result2);
    }

    public static void getUserInfoByProgram(){
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> parMap = new HashMap<String, Object>();
        parMap.clear();
        parMap.put("APPID", WechatAccountInfo.DEERCARE_PROGRAM_APP_ID);
        parMap.put("APPSECRET",WechatAccountInfo.DEERCARE_PROGRAM_APP_SECRET);
        parMap.put("JSCODE", "021SgY6q1rSigi0nya7q1uc37q1SgY6F");
        String result = restTemplate.getForObject(WechatIFSInfo.GET_PROGRAM_USER_AUTH, String.class, parMap);
        System.out.println(result);
    }

    public static void sendmsg(String... key){
        System.out.println(key[0]);
        System.out.println(key[1]);
        System.out.println(key[2]);
    }

    // {"total":4,"count":4,"data":{"openid":["oPBltwI1mcGmi63mNbeU50sQ6L1E","oPBltwLMmQyGXkgP36Q-5lSc1wNI","oPBltwH_gixEag8Ep69bPPqCOAhg","oPBltwF9pYPuA327c7LjghSbEVs0"]},"next_openid":"oPBltwF9pYPuA327c7LjghSbEVs0"}
    public static void main(String[] args) {

        // send();
        // sendmsg("fff","1111","lll");
       // openList();
          // getUserInfo();
       // getUserInfoByProgram();
  //      String k = "{\"errcode\":41001,\"errmsg\":\"access_token missing hint: [JSf.kA03387108!]\"}";
//        System.out.println(JSONObject.parseObject(k).getString("errmsg2").equals("11"));
    }
    // {"errcode":41001,"errmsg":"access_token missing hint: [JSf.kA03387108!]"}
    // {"errcode":40164,"errmsg":"invalid ip 116.231.49.18 ipv6 ::ffff:116.231.49.18, not in whitelist hint: [GiyoAa0338e508]"}

}
