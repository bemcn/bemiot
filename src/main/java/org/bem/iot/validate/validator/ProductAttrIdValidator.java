package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.ProductAttrService;
import org.bem.iot.validate.ProductAttrIdVerify;

/**
 * 产品属性ID验证
 */
public class ProductAttrIdValidator implements ConstraintValidator<ProductAttrIdVerify, Long> {
    @Resource
    ProductAttrService productAttrService;

    @Override
    public boolean isValid(Long attrId, ConstraintValidatorContext constraintValidatorContext) {
        if(attrId == null || attrId <= 0) {
            return false;
        }
        return !productAttrService.existsNotAttrId(attrId);
    }
}