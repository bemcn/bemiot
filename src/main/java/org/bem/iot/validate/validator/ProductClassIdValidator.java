package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.ProductClassService;
import org.bem.iot.validate.ProductClassIdVerify;

/**
 * 产品分类ID验证
 * @author jakybland
 */
public class ProductClassIdValidator implements ConstraintValidator<ProductClassIdVerify, Integer> {
    @Resource
    ProductClassService productClassService;

    @Override
    public void initialize(ProductClassIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }
        return !productClassService.existsNotClassId(value);
    }
}
