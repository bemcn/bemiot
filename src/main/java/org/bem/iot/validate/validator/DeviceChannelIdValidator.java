package org.bem.iot.validate.validator;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.DeviceChannelService;
import org.bem.iot.validate.DeviceChannelIdVerify;

/**
 * 设备视频通道ID验证
 * @author jakybland
 */
public class DeviceChannelIdValidator implements ConstraintValidator<DeviceChannelIdVerify, String> {
    @Resource
    DeviceChannelService deviceChannelService;

    @Override
    public void initialize(DeviceChannelIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isEmpty(value)) {
            return false;
        }
        return !deviceChannelService.existsNotChannelId(value);
    }
}
