package cn.deercare.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class StringUtil {

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址。
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if("127.0.0.1".equals(ip)||"0:0:0:0:0:0:0:1".equals(ip)){
                //根据网卡取本机配置的IP
                InetAddress inet=null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip= inet.getHostAddress();
            }
        }
        return ip;
    }

    /**
     * 作用：产生随机字符串，不长于32位
     */
    public static String createNoncestr() {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        String str = "";
        int current_str_length = 0;
        Random random = new Random();
        for (int i = 0; i < 32; i++) {
            current_str_length = random.nextInt(chars.length() - 1);
            str += chars.substring(current_str_length, current_str_length + 1);
        }
        return str;
    }

    /**
     * 作用：map转xml
     */
    public static String arrayToXml(SortedMap<String, Object> map_value) {
        String xml = "<xml>";
        for (Map.Entry<String, Object> entry : map_value.entrySet()) {
            if (entry.getValue() instanceof Integer) {
                xml += "<" + entry.getKey() + ">" + entry.getValue() + "</"
                        + entry.getKey() + ">";
            } else {
                xml += "<" + entry.getKey() + "><![CDATA[" + entry.getValue()
                        + "]]></" + entry.getKey() + ">";
                // xml+="<"+entry.getKey()+">"+entry.getValue()+"</"+entry.getKey()+">";
            }
        }
        xml += "</xml>";
        return xml;
    }

    /**
     * 作用：将xml转为map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> xmlToArray(String xml) {
        Map<String, Object> map_value = new HashMap<String, Object>();
        Document document;
        try {
            document = DocumentHelper.parseText(xml);
            Element root_element = document.getRootElement();
            List<Element> child_element = root_element.elements();
            for (Element ele : child_element) {
                map_value.put(ele.getName(), ele.getText());
            }
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return map_value;
    }
}
