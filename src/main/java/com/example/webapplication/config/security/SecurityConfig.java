package com.example.webapplication.config.security;

import com.example.webapplication.config.security.properties.SecurityProperties;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

	private final SecurityProperties securityProperties;

	public SecurityConfig(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	// API Security (OAuth2/JWT)
	@Bean
	@Order(1)
	public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher("/api/**")  // Nur für API-Requests
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/auth/**").permitAll()
						.anyRequest().authenticated()
				)
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.oauth2ResourceServer(oauth -> oauth
						.jwt(Customizer.withDefaults())
				)
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint((request, response, authException) -> {
							response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							response.setContentType("application/json");
							response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"" +
									authException.getMessage() + "\"}");
						})
				);

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain webSecurityFilterChain(
			HttpSecurity http,
			CustomAuthenticationEntryPoint authEntryPoint) throws Exception {

		if (securityProperties.isPermitsAll()) {
			// debug: all allowed
			http.
					authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
					.csrf(AbstractHttpConfigurer::disable);
		} else {
			http
					.securityMatcher("/**")
					.csrf(AbstractHttpConfigurer::disable)
					.authorizeHttpRequests(auth -> auth
							.requestMatchers("/", "/login", "/register", "/css/**", "/js/**").permitAll()
							.anyRequest().authenticated()
					)
					.formLogin(form -> form
							.loginPage("/login")
							.defaultSuccessUrl("/index", true)
							.permitAll()
					)
					.logout(logout -> logout
							.logoutSuccessUrl("/login?logout=true")
							.permitAll()
					)
					.exceptionHandling(ex -> ex
							.authenticationEntryPoint(authEntryPoint)
					);
		}
		return http.build();
	}
}
