package cn.deercare.over;


import cn.deercare.utils.SpringUtil;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component("restTemplate")
public class RestTemplate extends org.springframework.web.client.RestTemplate{
	public static RestTemplate getRestTemplate() {
		RestTemplate restTemplate = (RestTemplate) SpringUtil.getBean("restTemplate");
		restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		return restTemplate;
		/*
		// 非生成环境，不做特别设置
		if (!prof.equals(PROD)){
			// System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");
			return restTemplate;
		}
		// 生成环境设置代理
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		SocketAddress address = new InetSocketAddress(IP, PORT);
		Properties props = System.getProperties();
		Proxy proxy = new Proxy(SOCKS, address);
		requestFactory.setProxy(proxy);
		restTemplate.setRequestFactory(requestFactory);
		return restTemplate;
		 */
	}

	public static RestTemplate getRestTemplateByWechatPay(){
		return null;
	}
}
