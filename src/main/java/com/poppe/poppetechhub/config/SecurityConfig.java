package com.poppe.poppetechhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Com o Spring Security no classpath, todas as rotas ficam protegidas por padrão.
 * O health-check precisa ser público para que monitoramento e orquestradores possam consultá-lo.
 */
@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(HttpMethod.GET, "/health").permitAll()
				.anyRequest().authenticated())
			.httpBasic(httpBasic -> {
			});

		return http.build();
	}

}
