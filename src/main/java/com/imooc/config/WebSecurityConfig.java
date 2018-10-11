package com.imooc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.imooc.security.AuthProvider;
import com.imooc.security.LoginAuthFailHandler;
import com.imooc.security.LoginUrlEntryPoint;

@EnableWebSecurity
@EnableGlobalMethodSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
	/**
	 * http权限控制
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http.authorizeRequests()
		.antMatchers("/admin/login").permitAll()//管理员登陆入口
		.antMatchers("/static/**").permitAll()//静态资源
		.antMatchers("/user/login").permitAll()//用户登陆入口
		.antMatchers("/admin/**").hasRole("ADMIN")//管理接口
		.antMatchers("/user/**").hasAnyRole("ADMIN","USER")//用户接口
		.antMatchers("/api/user/**").hasAnyRole("ADMIN","USER")//api接口
		.and()
		.formLogin()
		.loginProcessingUrl("/login")//配置角色登陆入口
		.failureHandler(authFailHandler())
		.and()
		.logout()
		.logoutUrl("/logout")
		.logoutSuccessUrl("/logout/page")
		.deleteCookies("JSESSIONID")
		.invalidateHttpSession(true)
		.and()
		.exceptionHandling()
		.authenticationEntryPoint(urlEntryPonit())
		.accessDeniedPage("/403");
		 
		
		http.csrf().disable();
		http.headers().frameOptions().sameOrigin();
	}
	
	/**
	 * 自定义认证策略
	 * @throws Exception 
	 */
	@Autowired
	public void configGlobal(AuthenticationManagerBuilder auth) throws Exception {
		//内存认证策略
		//auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN").and();
		auth.authenticationProvider(authProvider()).eraseCredentials(true);
	}
	@Bean
	public AuthProvider authProvider() {
		return new AuthProvider();
	}

	@Bean
	public LoginUrlEntryPoint urlEntryPonit() {
		return new LoginUrlEntryPoint("/user/login");
	}
	@Bean
	public LoginAuthFailHandler authFailHandler() {
		return new LoginAuthFailHandler(urlEntryPonit());
	}
}
