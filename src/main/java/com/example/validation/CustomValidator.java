package com.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class CustomValidator implements ConstraintValidator<ValidateInputFile, MultipartFile>{

    @Override
    public boolean isValid(MultipartFile lostItemsFile, ConstraintValidatorContext constraintValidatorContext) {
        if(!lostItemsFile.getOriginalFilename().endsWith(".csv")){
            return false;
        }
        return true;
    }
}
