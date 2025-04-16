package com.thanhdw.identify_service.service;

import com.thanhdw.identify_service.dto.request.PermissionRequest;
import com.thanhdw.identify_service.dto.response.PermissionResponse;
import com.thanhdw.identify_service.entity.Permission;
import com.thanhdw.identify_service.mapper.PermissionMapper;
import com.thanhdw.identify_service.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionService {
    PermissionRepository permissionRepository;
     PermissionMapper permissionMapper;
    public PermissionResponse create(PermissionRequest request){
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }
    public List<PermissionResponse> getAll(){
        var permissions = permissionRepository.findAll();
        return permissions.stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }
    public void delete(String id){
        permissionRepository.deleteById(id);
    }
    
}
