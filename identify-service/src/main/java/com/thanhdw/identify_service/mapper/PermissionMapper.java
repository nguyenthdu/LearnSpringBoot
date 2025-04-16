package com.thanhdw.identify_service.mapper;

import com.thanhdw.identify_service.dto.request.PermissionRequest;
import com.thanhdw.identify_service.dto.request.UserCreationRequest;
import com.thanhdw.identify_service.dto.request.UserUpdateRequest;
import com.thanhdw.identify_service.dto.response.PermissionResponse;
import com.thanhdw.identify_service.dto.response.UserResponse;
import com.thanhdw.identify_service.entity.Permission;
import com.thanhdw.identify_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
   Permission toPermission(PermissionRequest request);
   PermissionResponse toPermissionResponse(Permission permission);
}
