package com.imooc.entity;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.imooc.repository.RoleRepository;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.imooc.ApplicationTests;
import com.imooc.repository.UserRepository;

public class UserRepositoryTests extends ApplicationTests{
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Test
	public void testFindOne() {
		User user = userRepository.findByName("waliwali");
		System.out.println(user.toString());
		Assert.assertEquals("waliwali", user.getName());
	}

	@Test
	public void testFindOneRole() {
		List<Role> roles = roleRepository.findRolesByUserId(1l);
		Assert.assertTrue((roles.get(0).getName().equals("USER")));
		System.out.println("ok");

	}


}
