package com.nexcode.config.keycloak;

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
//@ConditionalOnProperty(prefix = "rest.security", value = "enabled", havingValue = "true")
public class KeyCloakSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

//	@Autowired
//	private SecurityProperties securityProperties;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		KeycloakAuthenticationProvider provider = new KeycloakAuthenticationProvider();
		provider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
		auth.authenticationProvider(provider);
	}

	@Bean
	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(buildSessionRegistry());
	}

	@Bean
	protected SessionRegistry buildSessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Override
	public void configure(final HttpSecurity http) throws Exception {
//		http.cors().configurationSource(corsConfigurationSource()).and().headers().frameOptions().disable().and().csrf()
//				.disable().authorizeRequests().antMatchers("/api/users/login").permitAll().antMatchers("/api/roles")
//				.hasAuthority("user").antMatchers(securityProperties.getApiMatcher()).authenticated();
//		super.configure(http);
//		http.authorizeRequests().antMatchers("/api/users/**").permitAll().antMatchers("/api/roles/**")
//				.hasAnyRole("user").antMatchers("/moderator/**").hasAnyRole("moderator").antMatchers("/admin/**")
//				.hasAnyRole("admin").anyRequest().permitAll();
		http.csrf().disable();

//		http.authorizeExchange()
//				// ALLOWING REGISTER API FOR DIRECT ACCESS
//				.pathMatchers("/user/api/v1/register").permitAll()
//				// ALL OTHER APIS ARE AUTHENTICATED
//				.anyExchange().authenticated().and().csrf().disable().oauth2Login().and().oauth2ResourceServer().jwt();
//		return http.build();

	}

//	@Bean
//	public CorsConfigurationSource corsConfigurationSource() {
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		if (null != securityProperties.getCorsConfiguration()) {
//			source.registerCorsConfiguration("/**", securityProperties.getCorsConfiguration());
//		}
//		return source;
//	}
//
//	@Bean
//	public JwtAccessTokenCustomizer jwtAccessTokenCustomizer(ObjectMapper mapper) {
//		return new JwtAccessTokenCustomizer(mapper);
//	}

}
