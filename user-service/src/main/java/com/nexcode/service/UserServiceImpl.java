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
import org.springframework.stereotype.Service;

import com.nexcode.config.KeycloakProvider;
import com.nexcode.models.dto.UserDto;
import com.nexcode.models.entity.Role;
import com.nexcode.models.entity.RoleName;
import com.nexcode.models.entity.User;
import com.nexcode.models.mapper.UserMapper;
import com.nexcode.repository.RoleRepository;
import com.nexcode.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private final KeycloakService keycloakService;

	private final UserRepository userRepository;

	private final RoleRepository roleRepository;

	private final KeycloakProvider kcProvider;

	private final UserMapper userMapper;

	@Override
	public void signUpUser(UserDto userDto) {

		UserRepresentation userRepresentation = keycloakService.readUserByUsername(userDto.getUsername());
		CredentialRepresentation credentialRepresentation = createPasswordCredentials(userDto.getPassword());
		if (userRepresentation != null) {
			throw new BadRequestException("Username already existed!");
		} else {
			UserRepresentation kcUser = new UserRepresentation();
			kcUser.setUsername(userDto.getUsername());
			kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
			kcUser.setFirstName(null);
			kcUser.setLastName(null);
			kcUser.setEmail(null);
			kcUser.setEnabled(true);
			kcUser.setEmailVerified(true);

			Keycloak keycloak = kcProvider.getInstance();
			
			RealmResource realmResource = keycloak.realm("nexcode-realm");
			UsersResource usersRessource = realmResource.users();

			Response response = usersRessource.create(kcUser);

			System.out.println(response.getStatus());

			String userId = CreatedResponseUtil.getCreatedId(response);
			UserResource userResource = usersRessource.get(userId);

			List<RoleRepresentation> roles = realmResource.roles().list();

			Optional<RoleRepresentation> role = roles.stream().filter(r -> r.getName().equals("admin")).findAny();

			userResource.roles().realmLevel().add(Arrays.asList(role.get()));

			if (response.getStatus() == HttpStatus.SC_CREATED) {

				UserDto localUser = new UserDto();
				localUser.setMobileNumber(userDto.getMobileNumber());
				localUser.setUsername(userDto.getUsername());
				Role userRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
						.orElseThrow(() -> new BadRequestException("User Role not set."));
				localUser.setRoles(Collections.singleton(userRole));
				User user = userMapper.toEntity(localUser);
				userRepository.save(user);
			} else if (response.getStatus() == HttpStatus.SC_CONFLICT) {
				throw new BadRequestException("Username has been already existed!");
			}
		}
	}

	private static CredentialRepresentation createPasswordCredentials(String password) {
		CredentialRepresentation passwordCredentials = new CredentialRepresentation();
		passwordCredentials.setTemporary(false);
		passwordCredentials.setType(CredentialRepresentation.PASSWORD);
		passwordCredentials.setValue(password);
		return passwordCredentials;
	}

}
