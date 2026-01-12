package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.SpacePositionService;
import org.bem.iot.validate.SpaceIdVerify;

/**
 * 空间位置ID验证
 * @author jakybland
 */
public class SpaceIdValidator implements ConstraintValidator<SpaceIdVerify, Integer> {
    @Resource
    SpacePositionService spacePositionService;

    @Override
    public void initialize(SpaceIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !spacePositionService.existsNotSpaceId(value);
    }
}
