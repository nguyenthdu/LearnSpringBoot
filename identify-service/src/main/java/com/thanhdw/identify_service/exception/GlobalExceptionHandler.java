package com.thanhdw.identify_service.exception;

import com.thanhdw.identify_service.dto.request.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    //    @ExceptionHandler(value = RuntimeException.class)
    //    ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException e) {
    //       ApiResponse apiResponse = new ApiResponse();
    //         apiResponse.setCode(1001);
    //       apiResponse.setMessage(e.getMessage());
    //       return ResponseEntity.badRequest().body(apiResponse);
    //    }
    private static final String MIN_ATTRIBUTE = "min";
    
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException e) {
        log.error("Exception: ", e);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_ERROR.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_ERROR.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }
    
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException e) {
        log.error("AppException: ", e);
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }
    
    @ExceptionHandler(value = AuthorizationDeniedException.class)
    ResponseEntity<ApiResponse> handleAccessDeniedException(AuthorizationDeniedException e) {
        log.error("AuthorizationDeniedException: ", e);
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(ApiResponse.builder().code(errorCode.getCode()).message(errorCode.getMessage()).build());
    }
    
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: ", e);
        String enumKey = e.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);
            //Lấy thông tin từ annotation
            var constraintViolation = e.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
            //Lấy thuộc tính từ annotation
            attributes = constraintViolation.getConstraintDescriptor().getAttributes();
            log.info(attributes.toString());
        } catch (IllegalArgumentException ex) {
            // Handle the case where the enum value is not found
            //            errorCode = ErrorCode.INVALID_KEY;
        }
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(Objects.nonNull(attributes) ? mapAttribute(errorCode.getMessage(), attributes) : errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }
    
    //map value trong annotation vào message
    /*
    * Ví dụ: message = "Your are must be at least {min}"
    *   @DobConstrainst(
            min = 11, message = "INVALID_DOB"
    )
    * Thì sẽ thay thế {min} = 11
    * */
    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = attributes.get(MIN_ATTRIBUTE).toString();
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
