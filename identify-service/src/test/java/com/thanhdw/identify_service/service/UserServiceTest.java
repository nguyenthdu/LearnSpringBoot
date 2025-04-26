package com.thanhdw.identify_service.service;

import com.thanhdw.identify_service.dto.request.UserCreationRequest;
import com.thanhdw.identify_service.dto.response.UserResponse;
import com.thanhdw.identify_service.entity.User;
import com.thanhdw.identify_service.exception.AppException;
import com.thanhdw.identify_service.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @MockitoBean
    private UserRepository userRepository;
    
    private UserCreationRequest userCreationRequest;
    
    private UserResponse userResponse;
    
    private User user;
    
    private LocalDate dob;
    
    @BeforeEach
        // method nay se chay truoc moi test
    void initData() {
        dob = LocalDate.of(1990, 1, 1);
        //khi goi create voiw request sau
        userCreationRequest = UserCreationRequest.builder().username("thanhdwf").password("123456").firstName("Thanh")
                                                 .lastName("Duong").dob(dob).build();
        //thi se nhan duoc respone sau
        userResponse =
                UserResponse.builder().id("ccccc").username("thanhdwf").firstName("Thanh").lastName("Duong").dob(dob)
                            .build();
        user = User.builder().id("ccccc").username("thanhdwf").firstName("Thanh").lastName("Duong").dob(dob).build();
    }
    
    @Test
    void createUser_validRequest_success() {
        //xay dung test
        // GIVEN - tao du lieu
        when(userRepository.existsByUsername(anyString())).thenReturn(
                false);//false thi thuc hien cac buoc kiem tra tiep theo
        when(userRepository.save(any())).thenReturn(user);
        // WHEN - thuc hien
        userService.createUser(userCreationRequest);
        //THEN - kiem tra
        Assertions.assertThat(userResponse.getId()).isEqualTo("ccccc");
        Assertions.assertThat(userResponse.getUsername()).isEqualTo("thanhdwf");
        Assertions.assertThat(userResponse.getFirstName()).isEqualTo("Thanh");
        Assertions.assertThat(userResponse.getLastName()).isEqualTo("Duong");
        
    }
    
    @Test
    void createUser_userExisted_fail() {
        //xay dung test
        // GIVEN - tao du lieu
        when(userRepository.existsByUsername(anyString())).thenReturn(
                true);// true bat ngay vi tri nay nen khong can thuc hien cac buoc kiem tra tiep theo
        // WHEN - thuc hien
        
        var exception = assertThrows(AppException.class, () -> userService.createUser(userCreationRequest));
        //THEN - kiem tra
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1001);
    }
    
    @Test
    //Anotation @WithMockUser la cua spring security de tao ra mot user da dang nhap
    @WithMockUser(
            username = "thanhdwf"
    )
    void getMyInfo_valid_success() {
        //Phuong thuc se gap loi do getMyInfo() lay token tu SecurityContextHolder nen khong can phai set token
        //can them dependency cho phuong thuc spring security test
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        var response = userService.getMyInfo();
        Assertions.assertThat(response.getUsername()).isEqualTo("thanhdwf");
        Assertions.assertThat(response.getId()).isEqualTo("ccccc");
    }
    @Test
    //Anotation @WithMockUser la cua spring security de tao ra mot user da dang nhap
    @WithMockUser(
            username = "thanhdwf"
    )
    void getMyInfo_userNotFound_error() {
        //Phuong thuc se gap loi do getMyInfo() lay token tu SecurityContextHolder nen khong can phai set token
        //can them dependency cho phuong thuc spring security test
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(null));
        var exception = assertThrows(AppException.class, () -> userService.getMyInfo());
        
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1006);
    }
}
