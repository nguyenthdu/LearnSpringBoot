package com.thanhdw.identify_service.service;

import com.thanhdw.identify_service.dto.request.RoleRequest;
import com.thanhdw.identify_service.dto.response.RoleResponse;
import com.thanhdw.identify_service.mapper.RoleMaper;
import com.thanhdw.identify_service.repository.RoleRepository;
import com.thanhdw.identify_service.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleService {
    RoleRepository roleRepository;
    RoleMaper roleMaper;
    PermissionRepository permissionRepository;
    public RoleResponse create(RoleRequest request){
        var role =  roleMaper.toRole(request);
        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);
        return roleMaper.toRoleResponse(role);
    }
    public List<RoleResponse> getAll(){
        var roles = roleRepository.findAll();
        return roles.stream()
                .map(roleMaper::toRoleResponse)
                .toList();
    }
    public void delete(String id){
        roleRepository.deleteById(id);
    }
}
