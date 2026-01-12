package org.bem.iot.validate.validator;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.DeviceService;
import org.bem.iot.validate.DeviceIdVerify;

/**
 * 设备信息ID验证
 * @author jakybland
 */
public class DeviceIdValidator implements ConstraintValidator<DeviceIdVerify, String> {
    @Resource
    DeviceService deviceService;

    @Override
    public void initialize(DeviceIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isNotEmpty(value)) {
            return false;
        }
        return !deviceService.existsNotDeviceId(value);
    }
}
