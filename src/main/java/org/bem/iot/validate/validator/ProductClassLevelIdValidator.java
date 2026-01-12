package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.ProductClassService;
import org.bem.iot.validate.ProductClassLevelIdVerify;

/**
 * 产品分类上级ID验证
 * @author jakybland
 */
public class ProductClassLevelIdValidator implements ConstraintValidator<ProductClassLevelIdVerify, Integer> {
    @Resource
    ProductClassService productClassService;

    @Override
    public void initialize(ProductClassLevelIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }  else {
            if(value == 0) {
                return true;
            } else {
                return !productClassService.existsNotClassId(value);
            }
        }
    }
}
