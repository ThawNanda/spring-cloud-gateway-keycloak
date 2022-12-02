package com.nexcode.service;

import org.springframework.stereotype.Service;

import com.nexcode.models.dto.UserDto;
import com.nexcode.models.entity.User;
import com.nexcode.models.mapper.UserMapper;
import com.nexcode.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	@Override
	public void signupUser(UserDto userDto) {
		User user = userMapper.toEntity(userDto);
		userRepository.save(user);
	}

}
