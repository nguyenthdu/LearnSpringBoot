package com.thanhdw.identify_service.controller;

import com.thanhdw.identify_service.dto.request.ApiResponse;
import com.thanhdw.identify_service.dto.request.PermissionRequest;
import com.thanhdw.identify_service.dto.request.RoleRequest;
import com.thanhdw.identify_service.dto.response.PermissionResponse;
import com.thanhdw.identify_service.dto.response.RoleResponse;
import com.thanhdw.identify_service.service.PermissionService;
import com.thanhdw.identify_service.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;
    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }
    @GetMapping
    ApiResponse<List<RoleResponse>> findAll(){
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }
    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable String id){
        roleService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Successfully deleted role")
                .build();
    }
    
}
