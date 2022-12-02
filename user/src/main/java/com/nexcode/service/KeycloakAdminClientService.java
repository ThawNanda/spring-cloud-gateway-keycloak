package com.nexcode.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nexcode.config.keycloak.KeycloakProvider;
import com.nexcode.models.dto.UserDto;
import com.nexcode.models.entity.Role;
import com.nexcode.models.entity.RoleName;
import com.nexcode.models.request.UserSignupRequest;
import com.nexcode.repository.RoleRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class KeycloakAdminClientService {

	private final KeycloakProvider kcProvider;

	private final UserService userService;

	private final RoleRepository roleRepository;

	private final BCryptPasswordEncoder encoder;

	public Response createKeycloakUser(UserSignupRequest user) {

		Keycloak keycloak = kcProvider.getInstance();

		UsersResource usersResource = kcProvider.getInstance().realm("bigbet-realm").users();

		CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());

		UserRepresentation kcUser = new UserRepresentation();
		kcUser.setUsername(user.getName());
		kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
		kcUser.setFirstName(null);
		kcUser.setLastName(null);
		kcUser.setEmail(null);
		kcUser.setEnabled(true);
		kcUser.setEmailVerified(true);

		RealmResource realmResource = keycloak.realm("bigbet-realm");
		UsersResource usersRessource = realmResource.users();

		Response response = usersResource.create(kcUser);

		String userId = CreatedResponseUtil.getCreatedId(response);
		UserResource userResource = usersRessource.get(userId);

//		RoleRepresentation realmRole = realmResource.roles().get("user").toRepresentation();

		List<RoleRepresentation> roles = realmResource.roles().list();

		Optional<RoleRepresentation> role = roles.stream().filter(r -> r.getName().equals("user")).findAny();

		userResource.roles().realmLevel().add(Arrays.asList(role.get()));
//
//		ClientRepresentation app1Client = realmResource.clients().findByClientId("bigbet-client").get(0);
//		RoleRepresentation userClientRole = realmResource.clients().get(app1Client.getId()).roles().get("user")
//				.toRepresentation();
//
//		userResource.roles().clientLevel(app1Client.getId()).add(Arrays.asList(userClientRole));

		if (response.getStatus() == HttpStatus.SC_CREATED) {

			UserDto localUser = new UserDto();
			localUser.setMobileNumber(user.getMobileNumber());
			localUser.setName(user.getName());
			Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
					.orElseThrow(() -> new BadRequestException("User Role not set."));
			localUser.setPassword(encoder.encode(user.getPassword()));
			localUser.setRoles(Collections.singleton(userRole));
			userService.signupUser(localUser);
		} else if (response.getStatus() == HttpStatus.SC_CONFLICT) {
			throw new BadRequestException("Username has been existed!");
		}

		return response;

	}

	private static CredentialRepresentation createPasswordCredentials(String password) {
		CredentialRepresentation passwordCredentials = new CredentialRepresentation();
		passwordCredentials.setTemporary(false);
		passwordCredentials.setType(CredentialRepresentation.PASSWORD);
		passwordCredentials.setValue(password);
		return passwordCredentials;
	}

}
