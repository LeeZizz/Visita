package com.vitazi.mapper;

import org.mapstruct.Mapper;

import com.vitazi.dto.request.UserCreateRequest;
import com.vitazi.entities.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

	UserEntity toUserEntity(UserCreateRequest userCreateRequest);
}
