//package com.nexcode.security;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Component
//@Slf4j
//public class SecurityContextUtils {
//
//	private static final String ANONYMOUS = "anonymous";
//
//	private SecurityContextUtils() {
//	}
//
//	public static String getUserName() {
//		SecurityContext securityContext = SecurityContextHolder.getContext();
//		Authentication authentication = securityContext.getAuthentication();
//		String username = ANONYMOUS;
//
//		if (null != authentication) {
//
//			if (authentication.getPrincipal() instanceof UserDetails) {
//				UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
//				username = springSecurityUser.getUsername();
//				log.info(username);
//
//			} else if (authentication.getPrincipal() instanceof String) {
//				username = (String) authentication.getPrincipal();
//
//			} else {
//				log.error("User details not found in Security Context");
//
//			}
//		} else {
//			log.error("Request not authenticated, hence no user name available");
//		}
//
//		return username;
//	}
//
//	public static Set<String> getUserRoles() {
//		SecurityContext securityContext = SecurityContextHolder.getContext();
//		Authentication authentication = securityContext.getAuthentication();
//		Set<String> roles = new HashSet<>();
//
//		if (null != authentication) {
//			authentication.getAuthorities().forEach(e -> roles.add(e.getAuthority()));
//		}
//		return roles;
//	}
//}