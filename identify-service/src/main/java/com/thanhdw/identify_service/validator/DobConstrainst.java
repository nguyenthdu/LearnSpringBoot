package com.thanhdw.identify_service.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

//Đánh dấu phương thức
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
//Đánh dấu thời điểm chạy chương trình
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {DobValidator.class}
)
public @interface DobConstrainst {
    String message() default "Invalid date of birth";
    
    int min();
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
