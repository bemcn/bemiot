package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.SystemDictTypeService;
import org.bem.iot.validate.DictTypeIdVerify;

/**
 * 字典类型ID验证
 * @author jakybland
 */
public class DictTypeIdValidator implements ConstraintValidator<DictTypeIdVerify, Integer> {
    @Resource
    SystemDictTypeService systemDictTypeService;

    @Override
    public void initialize(DictTypeIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !systemDictTypeService.existsNotTypeId(value);
    }
}
