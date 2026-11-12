package com.poppe.poppetechhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Com o Spring Security no classpath, todas as rotas ficam protegidas por padrão.
 * O health-check e a documentação precisam ser públicos. A API usa Basic Auth
 * sem sessão; por isso o CSRF é desabilitado enquanto não houver autenticação
 * baseada em cookie/sessão.
 */
@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(HttpMethod.GET, "/health").permitAll()
				.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
				.anyRequest().authenticated())
			.httpBasic(httpBasic -> {
			});

		return http.build();
	}

}
