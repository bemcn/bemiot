package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.BlacklistService;
import org.bem.iot.validate.BlacklistIdVerify;

/**
 * 黑名单ID验证
 * @author jakybland
 */
public class BlacklistIdValidator implements ConstraintValidator<BlacklistIdVerify, Long> {
    @Resource
    BlacklistService blacklistService;

    @Override
    public void initialize(BlacklistIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !blacklistService.existsNotBlackId(value);
    }
}
