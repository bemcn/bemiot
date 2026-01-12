package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.DriveParamsService;
import org.bem.iot.validate.DriveParamsIdVerify;

/**
 * 驱动参数id验证
 * @author jakybland
 */
public class DriveParamsIdValidator implements ConstraintValidator<DriveParamsIdVerify, Long> {
    @Resource
    DriveParamsService driveParamsService;

    @Override
    public void initialize(DriveParamsIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }
        return !driveParamsService.existsNotParamsId(value);
    }
}
