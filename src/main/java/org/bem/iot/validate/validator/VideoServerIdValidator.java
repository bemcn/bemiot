package org.bem.iot.validate.validator;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.VideoServerService;
import org.bem.iot.validate.VideoServerIdVerify;

/**
 * 视频服务ID验证
 * @author jakybland
 */
public class VideoServerIdValidator implements ConstraintValidator<VideoServerIdVerify, String> {
    @Resource
    VideoServerService videoServerService;

    @Override
    public void initialize(VideoServerIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isEmpty(value)) {
            return false;
        }
        return !videoServerService.existsNotServerId(value);
    }
}