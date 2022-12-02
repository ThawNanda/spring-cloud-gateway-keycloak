package com.nexcode.controller;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexcode.config.keycloak.KeycloakProvider;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

	@Value("${keycloak.realm}")
	public String realm;
	@Value("${keycloak.resource}")
	public String clientID;

	@Autowired
	private KeycloakProvider kcProvider;

	@GetMapping
	@RolesAllowed("user")
	public List<RoleRepresentation> getAllRoles() {

//		System.out.println(SecurityContextUtils.getUserName());

		Keycloak keycloak = kcProvider.getInstance();

		RealmResource realmResource = keycloak.realm(realm);

		List<RoleRepresentation> roles = realmResource.roles().list();

		return roles;

	}
}
