package com.nexcode.service;

import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import com.nexcode.config.KeycloakProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeycloakService {

	private final KeycloakProvider kcProvider;

	public UserRepresentation readUserByUsername(String username) {
		Keycloak keycloak = kcProvider.getInstance();
		RealmResource realmResource = keycloak.realm("nexcode-realm");
		UsersResource usersResource = realmResource.users();
		List<UserRepresentation> userRepresentation = usersResource.search(username);
		UserRepresentation user = null;
		if (userRepresentation.size() != 0) {
			user = userRepresentation.get(0);
		}
		return user;
	}
}
