package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.DeviceControlsService;
import org.bem.iot.validate.DeviceControlIdVerify;

/**
 * 设备群控ID验证
 * @author jakybland
 */
public class DeviceControlIdValidator implements ConstraintValidator<DeviceControlIdVerify, Long> {
    @Resource
    DeviceControlsService deviceControlsService;

    @Override
    public void initialize(DeviceControlIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }
        return !deviceControlsService.existsNotControlId(value);
    }
}
