package org.bem.iot.validate.validator;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.PlatformService;
import org.bem.iot.validate.PlatformNoIdVerify;

/**
 * 第3方平台接入ID验证
 * @author jakybland
 */
public class PlatformNoIdValidator implements ConstraintValidator<PlatformNoIdVerify, String> {
    @Resource
    PlatformService platformService;

    @Override
    public void initialize(PlatformNoIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isEmpty(value)) {
            return false;
        }
        return platformService.existsNotPlatformId(value);
    }
}
