package com.thanhdw.identify_service.service;

import com.thanhdw.identify_service.dto.request.UserCreationRequest;
import com.thanhdw.identify_service.dto.request.UserUpdateRequest;
import com.thanhdw.identify_service.dto.response.UserResponse;
import com.thanhdw.identify_service.entity.User;
import com.thanhdw.identify_service.enums.Role;
import com.thanhdw.identify_service.exception.AppException;
import com.thanhdw.identify_service.exception.ErrorCode;
import com.thanhdw.identify_service.mapper.UserMapper;
import com.thanhdw.identify_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
/*
 * Khi sử dụng @RequiredArgsConstructor, bạn không cần phải sử dụng @Autowired cho các trường final
 * mà bạn đã khai báo trong constructor. Spring sẽ tự động tiêm các phụ thuộc này vào.
 * */
public class UserService {
    //no se goi xuong repository
    //    @Autowired - 1 cach inject
    UserRepository userRepository;
    //    @Autowired
    UserMapper userMapper;
    
    public UserResponse createUser(UserCreationRequest request) {
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTS);
        }
        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        //        user.setUsername(request.getUsername());
        //        user.setPassword(request.getPassword());
        //        user.setFirstName(request.getFirstName());
        //        user.setLastName(request.getLastName());
        //        user.setDob(request.getDob());
        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
//        user.setRoles(roles);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("Get all users");
        return userRepository.findAll().stream().map(userMapper::toUserResponse).collect(Collectors.toList());
    }
    
    //    @PostAuthorize("hasRole('ADMIN') or returnObject.username == authentication.name")
    //    @PostAuthorize("hasRole('ADMIN')")
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUserById(String userId) {
        log.info("Get user by id: {}", userId);
        return userMapper.toUserResponse(userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }
    
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userMapper.updateUser(user, request);
        //        user.setPassword(request.getPassword());
        //        user.setFirstName(request.getFirstName());
        //        user.setLastName(request.getLastName());
        //        user.setDob(request.getDob());
        return userMapper.toUserResponse(userRepository.save(user));
    }
    
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }
    
    public UserResponse getMyInfo() {
        log.info("Get my info");
        //khi request thanh cong thi thong tin se duoc luu trong SecurityContextHolder
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }
}
