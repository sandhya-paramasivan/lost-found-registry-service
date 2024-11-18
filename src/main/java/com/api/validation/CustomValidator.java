package com.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class CustomValidator implements ConstraintValidator<ValidateInputFile, MultipartFile>{

    @Override
    public boolean isValid(MultipartFile lostItemsFile, ConstraintValidatorContext constraintValidatorContext) {
        return lostItemsFile.getOriginalFilename().endsWith(".csv");
    }
}
