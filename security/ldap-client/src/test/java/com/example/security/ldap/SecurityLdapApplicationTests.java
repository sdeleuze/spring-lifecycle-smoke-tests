package com.example.security.ldap;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityLdapApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Test
	void rootWhenAuthenticatedThenSaysHelloUser() throws Exception {
		// @formatter:off
		this.mvc.perform(get("/users")
						.with(httpBasic("user", "password")))
				.andExpect(content().string(containsString("Rod Johnson")));
		// @formatter:on
	}

	@Test
	void rootWhenUnauthenticatedThen401() throws Exception {
		// @formatter:off
		this.mvc.perform(get("/users"))
				.andExpect(status().isUnauthorized());
		// @formatter:on
	}

	@Test
	void tokenWhenBadCredentialsThen401() throws Exception {
		// @formatter:off
		this.mvc.perform(get("/users")
						.with(httpBasic("user", "passwerd")))
				.andExpect(status().isUnauthorized());
		// @formatter:on
	}

}
