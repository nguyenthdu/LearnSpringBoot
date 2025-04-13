package com.thanhdw.identify_service.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
     @Builder.Default // Dùng để gán giá trị mặc định cho biến khi dung @Builder
     int code=1000;
     String message;
     T result;
}
