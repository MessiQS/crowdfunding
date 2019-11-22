package cn.deercare.interceptor;

import cn.deercare.utils.TokenUtils;
import com.alibaba.druid.util.StringUtils;
import cn.deercare.annotation.Authorization;
import cn.deercare.model.User;
import cn.deercare.service.UserService;
import cn.deercare.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

	private static Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);
	
	/**
	 * 存放登录用户模型Key的Request Key
	 */
	public final static String REQUEST_CURRENT_KEY = "REQUEST_CURRENT_KEY";
	
	/**
	 * 存放鉴权信息的Header名称，默认是Authorization
	 */
	public final static String HTTP_HEADER_NAME = "Authorization";
	
	/**
	 * 鉴权信息的无用前缀，默认为空
	 */
	// private String httpHeaderPrefix = "Bearer ";
	private String httpHeaderPrefix = "";
	
	/**
	 * 鉴权失败后返回的错误信息，默认为401 unauthorized
	 */
	private String unauthorizedErrorMessage401 = "401 unauthorized";
	
	private String unauthorizedErrorMessage403 = "403 unauthorized";
	
	/**
	 * 鉴权失败后返回的HTTP错误码，默认为401
	 */
	private int unauthorizedErrorCode401 = HttpServletResponse.SC_UNAUTHORIZED;
	/**
	 * 鉴权失败后返回的HTTP错误码，默认为401
	 */
	private int unauthorizedErrorCode403 = HttpServletResponse.SC_FORBIDDEN;
	
	
	// private static final String[] ESPECIAL_TOKEN = {"544CD49431D42E3CFDE7F182B4DEE4B3"};
	
	

	/**
     * 在请求处理之前进行调用（Controller方法调用之前）
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		// 如果不是映射到方法直接通过
		if (!(handler instanceof HandlerMethod)) {
			System.out.println(request.getRequestURI());
			return true;
		}
		
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();
		// 查看方法上是否有注解
		if (method.getAnnotation(Authorization.class) == null
				// 查看方法所在的Controller是否有注解
				&& handlerMethod.getBeanType().getAnnotation(Authorization.class) == null) {
			// tokenError(response, unauthorizedErrorCode401, unauthorizedErrorMessage401);
			return true;
		}
		// 从header中得到token
		String token = request.getHeader(HTTP_HEADER_NAME);
		if(StringUtils.isEmpty(token)) {
			// 解析token错误 返回false
			tokenError(response, unauthorizedErrorCode401, "认证错误");
			return false;
		}
		if (token != null && token.startsWith(httpHeaderPrefix) && token.length() > 0) {
			token = token.substring(httpHeaderPrefix.length());	
			try {
				// 获取用户来源
				String userId = TokenUtils.getUserId(token);
				// 验证token过期
				boolean k = TokenUtils.tokenCheck(token, TokenUtils.URL_AUTHENTICATION);
				if(!k) {
					// 解析token错误 返回false
					tokenError(response, unauthorizedErrorCode401, "认证已过期");
					return false;
				}
				// 根据用户来源获取用户信息
				UserService userService = SpringUtil.getBean(UserService.class);
				User user = (User) userService.getById(userId);
				// User user = (User) userService.login(new User(Long.parseLong(userId), Integer.parseInt(userId)));
				// User user = userService.get(new User(userName));
				// 保存用户信息
				// GlobalScope.user = user;
				// 从库中验证token是否正确
				if(!user.getToken().equals(token)) {
					// token不匹配
					tokenError(response, unauthorizedErrorCode403, unauthorizedErrorMessage403);
					return false;
				}
				// 日志追踪
				MDC.put("logger_trace", user.getMainId().toString());
				return true;
			}catch (Exception e) {
				logger.error(e.toString(), e);
				// 解析token错误 返回false
				tokenError(response, unauthorizedErrorCode401, unauthorizedErrorMessage401);
				return false;
			}
		}
		/*
		 * 如果验证token失败，并且方法注明了Authorization，返回401错误
		 */
		tokenError(response, unauthorizedErrorCode401, unauthorizedErrorMessage401);
		return false;
	}
	
	private void tokenError(HttpServletResponse response, int code, String message) throws IOException {
		response.setStatus(code);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
		writer.write(message);
		writer.close();
	}
	/**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}
	/**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
