package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.SystemDictService;
import org.bem.iot.validate.DictIdVerify;

/**
 * 字典类型ID验证
 * @author jakybland
 */
public class DictIdValidator implements ConstraintValidator<DictIdVerify, Integer> {
    @Resource
    SystemDictService systemDictService;

    @Override
    public void initialize(DictIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !systemDictService.existsNotDictId(value);
    }
}
