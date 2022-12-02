package com.nexcode.config;

import javax.ws.rs.BadRequestException;

import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import com.nexcode.security.CustomAuthenticationEntryPoint;

//@KeycloakConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class ResourceServerConfig extends KeycloakWebSecurityConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) {
		try {

			// http.authorizeRequests().antMatchers("/api/users/login").permitAll();
			// .anyRequest().authenticated().and().oauth2ResourceServer().jwt(jwt ->
			// jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()));

			http.httpBasic().disable().formLogin(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable)
					.authorizeRequests(authorize -> authorize
							.mvcMatchers(HttpMethod.POST, "/user-service/api/users/**", "/admin/**").permitAll()
							.mvcMatchers(HttpMethod.PUT, "/app/**", "/admin/**").authenticated()
							.mvcMatchers(HttpMethod.POST, "/app/**", "/admin/**").authenticated())
					.oauth2ResourceServer().authenticationEntryPoint(new CustomAuthenticationEntryPoint()).jwt()
					.jwtAuthenticationConverter(jwtAuthenticationConverter());
		} catch (Exception e) {
			throw new BadRequestException("Error" + e.getMessage() + e.getLocalizedMessage());
		}

	}

	private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
		JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
		jwtConverter.setJwtGrantedAuthoritiesConverter(new RealmRoleConverter());
		return jwtConverter;
	}

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

}
