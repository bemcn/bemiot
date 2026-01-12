package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.ProductService;
import org.bem.iot.validate.ProductIdVerify;

/**
 * 产品ID验证
 * @author jakybland
 */
public class ProductIdValidator implements ConstraintValidator<ProductIdVerify, String> {
    @Resource
    ProductService productService;


    @Override
    public void initialize(ProductIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }
        return !productService.existsNotProductId(value);
    }
}
