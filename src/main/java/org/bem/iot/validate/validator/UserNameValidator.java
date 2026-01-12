package org.bem.iot.validate.validator;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.UserInfoService;
import org.bem.iot.validate.UserNameVerify;

/**
 * 用户名验证
 * @author jakybland
 */
public class UserNameValidator implements ConstraintValidator<UserNameVerify, String> {
    @Resource
    UserInfoService userInfoService;

    @Override
    public void initialize(UserNameVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StrUtil.isEmpty(value)) {
            return false;
        }
        return userInfoService.existsNotUserName(value.toLowerCase());
    }
}
