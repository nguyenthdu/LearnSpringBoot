package com.thanhdw.identify_service.controller;

import com.thanhdw.identify_service.dto.request.ApiResponse;
import com.thanhdw.identify_service.dto.request.UserCreationRequest;
import com.thanhdw.identify_service.dto.request.UserUpdateRequest;
import com.thanhdw.identify_service.dto.response.UserResponse;
import com.thanhdw.identify_service.entity.User;
import com.thanhdw.identify_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
     UserService userService;
    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid  UserCreationRequest request) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }
    @GetMapping
    ApiResponse<List<UserResponse>> getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info("grantedAuthority: {}", grantedAuthority.getAuthority()));
//       return ApiResponse.<List<UserResponse>>builder().result(userService.getUsers()).build();
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
        
    }
    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUserById(@PathVariable String userId) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getUserById(userId));
        return apiResponse;
    }
    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.updateUser(userId, request));
        apiResponse.setMessage("Successfully updated user");
        return apiResponse;
    }
    @DeleteMapping("/{userId}")
    ApiResponse deleteUser(@PathVariable String userId) {
        ApiResponse apiResponse = new ApiResponse();
        userService.deleteUser(userId);
        apiResponse.setMessage("Successfully deleted user");
        return apiResponse;
    }
    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return  ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .message("Successfully get my info")
                .build();
    }
}
