package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.UserInfoService;
import org.bem.iot.validate.UserIdVerify;

/**
 * 用户ID验证
 * @author jakybland
 */
public class UserIdValidator implements ConstraintValidator<UserIdVerify, Integer> {
    @Resource
    UserInfoService userInfoService;

    @Override
    public void initialize(UserIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !userInfoService.existsNotUserId(value);
    }
}
