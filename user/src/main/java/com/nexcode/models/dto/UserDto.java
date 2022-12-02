package com.nexcode.models.dto;

import java.util.Set;

import com.nexcode.models.entity.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

	private Long id;
	private String name;
	private String mobileNumber;
	private String password;
	private Set<Role> roles;
}
