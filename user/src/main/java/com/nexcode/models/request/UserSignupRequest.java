package com.nexcode.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequest {

	private String name;
	private String mobileNumber;
	private String password;
}
