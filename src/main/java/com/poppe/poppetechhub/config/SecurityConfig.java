package com.poppe.poppetechhub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Com o Spring Security no classpath, todas as rotas ficam protegidas por padrão.
 * O health-check precisa ser público para que monitoramento e orquestradores possam
 * consultá-lo. A documentação (springdoc) também é pública, mas apenas quando está
 * habilitada: em produção ela é desligada por profile e, nesse caso, a permissão nem
 * chega a ser registrada. A API usa Basic Auth sem sessão; por isso o CSRF é
 * desabilitado enquanto não houver autenticação baseada em cookie/sessão.
 */
@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http,
			@Value("${springdoc.api-docs.enabled:true}") boolean docsEnabled) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(authorize -> {
				authorize.requestMatchers(HttpMethod.GET, "/health").permitAll();
				if (docsEnabled) {
					authorize.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();
				}
				authorize.anyRequest().authenticated();
			})
			.httpBasic(httpBasic -> {
			});

		return http.build();
	}

}
