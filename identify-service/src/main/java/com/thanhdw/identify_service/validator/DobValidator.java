package com.thanhdw.identify_service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

//Class này định nghĩa các phương thức kiểm tra tính hợp lệ của trường ngày sinh
//Mỗi anotation chỉ nên chịu trách nhiệm cho một loại kiểm tra
public class DobValidator implements ConstraintValidator<DobConstrainst, LocalDate> {
    private int min;
    
    //Khỏi tạo, lấy thông số từ annotation
    @Override
    public void initialize(DobConstrainst constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }
    
    //Hàm dùng để xử lý dữ liệu này có đúng hay không
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if(Objects.isNull(localDate)) {
            return true;
        }
        long years = ChronoUnit.YEARS.between(localDate, LocalDate.now());
        return years >= min;
    }
}
