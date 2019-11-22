package cn.deercare.over;


import cn.deercare.utils.SpringUtil;
import cn.deercare.finals.wechat.WechatAccountInfo;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

@Component("restTemplate")
public class RestTemplate extends org.springframework.web.client.RestTemplate{

	private static Logger logger = LoggerFactory.getLogger(RestTemplate.class);

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

	public static org.springframework.web.client.RestTemplate getRestTemplateByWechat(){
		RestTemplate restTemplate = getRestTemplate();
		try {
			KeyStore ks = KeyStore.getInstance("pkcs12");//eg. PKCS12
			ks.load(new FileInputStream(RestTemplate.class.getClassLoader().getResource("wechat_cert/apiclient_cert.p12").getFile()), WechatAccountInfo.PAY_MERCHANT.toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, WechatAccountInfo.PAY_MERCHANT.toCharArray());

			SSLContext ssl = SSLContext.getInstance("TLS");//eg. TLS
			ssl.init(kmf.getKeyManagers(), null, null);

			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ssl, NoopHostnameVerifier.INSTANCE);

			CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
			restTemplate.setRequestFactory(factory);
		}catch (Exception e){
			logger.error(e.toString(), e);
		}
		return restTemplate;
	}
}
