package com.imooc.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import com.imooc.entity.User;
import com.imooc.service.IUserService;

public class AuthProvider implements AuthenticationProvider{

	@Autowired
	private IUserService userService;
	
	private final Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String userName = authentication.getName();
		String inputPassword = (String) authentication.getCredentials();
		
		User user = userService.findUserByName(userName);
		if(user == null) {
			throw new AuthenticationCredentialsNotFoundException("authError");
		}
		
		if(this.passwordEncoder.isPasswordValid(user.getPassword(), inputPassword, user.getId())) {
			return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
		}
		throw new BadCredentialsException("authError");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return true;
	}
	

}
