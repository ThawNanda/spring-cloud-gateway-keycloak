package com.nexcode.models.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.nexcode.models.dto.UserDto;
import com.nexcode.models.entity.User;
import com.nexcode.models.request.SignUpRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

	UserDto toDto(SignUpRequest request);

	User toEntity(UserDto localUser);

}
