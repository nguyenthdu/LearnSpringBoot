package com.thanhdw.identify_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.thanhdw.identify_service.dto.request.UserCreationRequest;
import com.thanhdw.identify_service.dto.response.UserResponse;
import com.thanhdw.identify_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc //tao request toi controller
//khai bao config voi test
@TestPropertySource("/test.properties")// de cho test chay voi config test
public class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private UserService userService;
    
    private UserCreationRequest userCreationRequest;
    
    private UserResponse userResponse;
    
    private LocalDate dob;
    
    @BeforeEach
        // method nay se chay truoc moi test
    void initData() {
        dob = LocalDate.of(1990, 1, 1);
        //khi goi create voiw request sau
        userCreationRequest = UserCreationRequest.builder().username("thanhdw").password("123456").firstName("Thanh")
                                                 .lastName("Duong").dob(dob).build();
        //thi se nhan duoc respone sau
        userResponse =
                UserResponse.builder().id("ccccc").username("thanhdw").firstName("Thanh").lastName("Duong").dob(dob)
                            .build();
    }
    
    @Test
    void createUser_validRequest_success() throws Exception {
        log.info("Hello test");
        //xay dung test
        // GIVEN - tao du lieu
        //chuyen request object thanh string
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());// để cho jackson ho tro localdate
        String content = objectMapper.writeValueAsString(userCreationRequest);
        //khi goi createUser voi tham so la userCreationRequest thi se tra ve userResponse, tra ve truc tiep ma khong can goi xuong userService
        Mockito.when(userService.createUser(userCreationRequest)).thenReturn(userResponse);
        // WHEN - thuc hien thao tac. tao request
        mockMvc.perform(
                       MockMvcRequestBuilders.post("/user").contentType(MediaType.APPLICATION_JSON_VALUE).content(content))
               //yeu cau string
               //Then - kiem tra ket qua
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
               .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("ccccc"))
        //expect username, password, ....
        ;
        
        // THEN - kiem tra ket qua
    }
    @Test
    void createUser_usernameInvalid_fail() throws Exception {
        log.info("Hello test");
        //xay dung test
        // GIVEN - tao du lieu
        userCreationRequest.setUsername("th");
        //chuyen request object thanh string
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());// để cho jackson ho tro localdate
        String content = objectMapper.writeValueAsString(userCreationRequest);
        //khi goi createUser voi tham so la userCreationRequest thi se tra ve userResponse, tra ve truc tiep ma khong can goi xuong userService
        //        Mockito.when(userService.createUser(userCreationRequest)).thenReturn(userResponse);
        // WHEN - thuc hien thao tac. tao request
        mockMvc.perform(
                       MockMvcRequestBuilders.post("/user")
                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                             .content(content))
               //yeu cau string
               //Then - kiem tra ket qua
               .andExpect(MockMvcResultMatchers.status().isBadRequest())
               .andExpect(MockMvcResultMatchers.jsonPath("code").value(1003))
               .andExpect(MockMvcResultMatchers.jsonPath("message").value("Username must not be empty 3"))
        //expect username, password, ....
        ;
        
        // THEN - kiem tra ket qua
    }
    
}
/*
 * jackson khong ho tro localdate can phai them jackson-datatype-jsr310
 * */


/*TODO:
* Khi chạy test với H2 database sẽ gặp lỗi  ở đoạn:
* ================================================================
* public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
            // Initialize the application here
            // For example, you can load initial data or perform any setup tasks
            if(userRepository.findByUsername("admin").isEmpty()) {
  =============================================================
  * Lý do: H2 database không có dữ liệu admin, các câu lệnh trong H2 không hoàn toàn giống với MySQL
  * Cách khắc phục:
  * @ConditionalOnProperty(
            prefix = "spring", value = "datasource.driverClassName", havingValue = "com.mysql.cj.jdbc.Driver"
    )
    => Isolate đoạn code này với H2 database
    * Mang lại tính linh hoạt cho ứng dụng, có thể chạy trên nhiều môi trường khác nhau mà không cần thay đổi mã nguồn.
    *
* */