package com.thanhdw.identify_service.dto.request;

import com.thanhdw.identify_service.exception.ErrorCode;
import com.thanhdw.identify_service.validator.DobConstrainst;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3, message = "USERNAME_INVALID")
    String username;
    @Size(
            min = 2, max = 50, message = "PASSWORD_INVALID"
    )
    String password;
    String firstName;
    String lastName;
    @DobConstrainst(
            min = 11, message = "INVALID_DOB"
    )
    LocalDate dob;
}
