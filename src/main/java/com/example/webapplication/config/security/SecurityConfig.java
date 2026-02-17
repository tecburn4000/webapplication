package com.example.webapplication.config.security;

import com.example.webapplication.config.security.properties.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
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

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		if (securityProperties.isPermitsAll()) {
			// debug: all allowed
			http.
					authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
					.csrf(AbstractHttpConfigurer::disable);
		} else {
			http
					.csrf(AbstractHttpConfigurer::disable) // Do not use in production
					.formLogin(httpForm ->{
						httpForm.loginPage("/login").permitAll();
						httpForm.defaultSuccessUrl("/index");
					})

					.authorizeHttpRequests(register ->{
						register.requestMatchers("/css/**","/js/**").permitAll(); // Allow access to static resources
						register.requestMatchers("/login").permitAll(); // Allow access to login page
						register.requestMatchers("/register").permitAll(); // Allow access to registration page
						register.requestMatchers("/actuator/**").permitAll(); // oder nur /actuator/mappings
						register.requestMatchers("/h2-console/**").permitAll(); // Allow access to H2 Console - do not use in production!
						register.anyRequest().authenticated();
					})
					.logout(logout -> logout
							.logoutUrl("/logout")
							.logoutSuccessUrl("/login?logout")
							.permitAll()
					)
					//.httpBasic(Customizer.withDefaults())
					//h2 console config
					.headers(headers -> headers
							.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));


		}
		return http.build();
	}
}