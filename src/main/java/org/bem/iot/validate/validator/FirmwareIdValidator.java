package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.FirmwareService;
import org.bem.iot.validate.FirmwareIdVerify;

/**
 * 固件ID验证
 * @author jakybland
 */
public class FirmwareIdValidator implements ConstraintValidator<FirmwareIdVerify, Integer> {
    @Resource
    FirmwareService firmwareService;

    @Override
    public void initialize(FirmwareIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value == 0) {
            return true;
        }
        return !firmwareService.existsNotFirmwareId(value);
    }
}
