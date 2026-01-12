package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.DeviceUserService;
import org.bem.iot.validate.DeviceUserIdVerify;

/**
 * 设备用户权限ID验证
 * @author jakybland
 */
public class DeviceUserIdValidator implements ConstraintValidator<DeviceUserIdVerify, Long> {
    @Resource
    DeviceUserService deviceUserService;

    @Override
    public void initialize(DeviceUserIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }
        return !deviceUserService.existsNotDeviceUserId(value);
    }
}
