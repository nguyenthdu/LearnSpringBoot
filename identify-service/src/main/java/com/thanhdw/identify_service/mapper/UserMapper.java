package com.thanhdw.identify_service.mapper;

import com.thanhdw.identify_service.dto.request.UserCreationRequest;
import com.thanhdw.identify_service.dto.request.UserUpdateRequest;
import com.thanhdw.identify_service.dto.response.UserResponse;
import com.thanhdw.identify_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
    UserResponse toUserResponse(User user);
    //neu 2 file khac nhau
    //@Mapping(target = "userId", source = "id")
    //khong map
    //@Mapping(target = "id", ignore = true)
}
