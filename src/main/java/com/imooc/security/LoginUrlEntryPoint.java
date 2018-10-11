package com.imooc.security;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * 基于角色的登录入口控制器
 * @author Administrator
 *
 */
public class LoginUrlEntryPoint extends LoginUrlAuthenticationEntryPoint{

	private PathMatcher pathMather = new AntPathMatcher();
	
	private final Map<String ,String> authEntryPointMap;
	
	public LoginUrlEntryPoint(String loginFormUrl) {
		super(loginFormUrl);
		this.authEntryPointMap = new HashMap<String,String>();
		//普通用户登录入口映射
		authEntryPointMap.put("/user/**", "/user/login");
		//管理员登录入口映射
		authEntryPointMap.put("/admin/**", "/admin/login");
	}
	
	@Override
	protected String determineUrlToUseForThisRequest(HttpServletRequest request,HttpServletResponse response, AuthenticationException exception) {
		String uri = request.getRequestURI().replace(request.getContextPath(), "");
		for (Map.Entry<String, String> authEntry:this.authEntryPointMap.entrySet()) {
			if (this.pathMather.match(authEntry.getKey(), uri)) {
				return authEntry.getValue();
			}
		}
		return super.determineUrlToUseForThisRequest(request, response, exception);
	}
	
	
}
