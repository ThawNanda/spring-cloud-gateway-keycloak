package com.nexcode.controller;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexcode.config.KeycloakProvider;
import com.nexcode.exception.BadRequestException;
import com.nexcode.models.dto.UserDto;
import com.nexcode.models.mapper.UserMapper;
import com.nexcode.models.request.LoginRequest;
import com.nexcode.models.request.SignUpRequest;
import com.nexcode.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Slf4j
public class UserController {

	private final UserMapper userMapper;

	private final UserService userService;

	private final KeycloakProvider kcProvider;

	@PostMapping("/admin/signUp")
	public ResponseEntity<?> signUpAdmin(@RequestBody SignUpRequest request) {
		UserDto userDto = userMapper.toDto(request);
		userService.signUpUser(userDto);
		return new ResponseEntity<>("SuccessFully Created", HttpStatus.OK);
	}

	@PostMapping("/login")
	@RolesAllowed("admin")
	public ResponseEntity<AccessTokenResponse> login(@NotNull @RequestBody LoginRequest loginRequest) {
		Keycloak keycloak = kcProvider
				.newKeycloakBuilderWithPasswordCredentials(loginRequest.getUsername(), loginRequest.getPassword())
				.build();

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
