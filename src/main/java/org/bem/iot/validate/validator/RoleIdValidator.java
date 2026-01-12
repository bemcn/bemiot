package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.RoleService;
import org.bem.iot.validate.RoleIdVerify;

/**
 * 角色ID验证
 * @author jakybland
 */
public class RoleIdValidator implements ConstraintValidator<RoleIdVerify, Integer> {
    @Resource
    RoleService roleService;

    @Override
    public void initialize(RoleIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !roleService.existsNotRoleId(value);
    }
}
