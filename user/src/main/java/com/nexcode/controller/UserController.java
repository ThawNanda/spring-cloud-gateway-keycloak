package com.nexcode.controller;

import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexcode.config.keycloak.KeycloakProvider;
import com.nexcode.models.request.LoginRequest;
import com.nexcode.models.request.UserSignupRequest;
import com.nexcode.service.KeycloakAdminClientService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Slf4j
public class UserController {

	private final KeycloakProvider kcProvider;

	private final KeycloakAdminClientService kcAdminClient;

	@PostMapping
	public ResponseEntity<?> userSignup(@RequestBody UserSignupRequest request) {

		Response createdResponse = kcAdminClient.createKeycloakUser(request);
		return ResponseEntity.status(createdResponse.getStatus()).build();

	}

	@PostMapping("/login")
	public ResponseEntity<AccessTokenResponse> login(@NotNull @RequestBody LoginRequest loginRequest) {
		Keycloak keycloak = kcProvider
				.newKeycloakBuilderWithPasswordCredentials(loginRequest.getName(), loginRequest.getPassword()).build();

		AccessTokenResponse accessTokenResponse = null;

		try {
			accessTokenResponse = keycloak.tokenManager().getAccessToken();
			return ResponseEntity.status(HttpStatus.OK).body(accessTokenResponse);
		} catch (BadRequestException ex) {
			log.warn("invalid account. User probably hasn't verified email.", ex);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(accessTokenResponse);
		}

	}
}
