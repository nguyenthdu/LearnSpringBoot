package com.thanhdw.identify_service.dto.response; 

import com.thanhdw.identify_service.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
     String id;
     String username;
     String firstName;
     String lastName;
     LocalDate dob;
     Set<RoleResponse> roles;
}
