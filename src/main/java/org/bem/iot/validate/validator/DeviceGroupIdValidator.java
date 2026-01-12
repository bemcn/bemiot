package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.DeviceGroupService;
import org.bem.iot.validate.DeviceGroupIdVerify;

/**
 * 设备分组ID验证
 * @author jakybland
 */
public class DeviceGroupIdValidator implements ConstraintValidator<DeviceGroupIdVerify, Integer> {
    @Resource
    DeviceGroupService deviceGroupService;

    @Override
    public void initialize(DeviceGroupIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value == 0) {
            return false;
        }
        return !deviceGroupService.existsNotDeviceGroupId(value);
    }
}
