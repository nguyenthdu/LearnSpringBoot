package com.thanhdw.identify_service.mapper;

import com.thanhdw.identify_service.dto.request.RoleRequest;
import com.thanhdw.identify_service.dto.response.RoleResponse;
import com.thanhdw.identify_service.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface RoleMaper
{
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role role);
}
