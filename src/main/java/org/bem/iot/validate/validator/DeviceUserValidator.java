package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.model.device.DeviceUser;
import org.bem.iot.service.DeviceUserService;
import org.bem.iot.validate.DeviceUserVerify;

public class DeviceUserValidator implements ConstraintValidator<DeviceUserVerify, DeviceUser> {
    @Resource
    DeviceUserService deviceUserService;

    @Override
    public void initialize(DeviceUserVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(DeviceUser deviceUser, ConstraintValidatorContext constraintValidatorContext) {
        try {
            String deviceId = deviceUser.getDeviceId();
            int userId = deviceUser.getUserId();
            return deviceUserService.existsDeviceUser(deviceId, userId);
        } catch (Exception e) {
            return false;
        }
    }
}
