package com.imooc;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

public class Test {

	private final static Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
	public static void main(String[] args) {
		String password = passwordEncoder.encodePassword("yzadmin", 6);
		System.out.println(password);
		
	}
}
