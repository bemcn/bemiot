package org.bem.iot.validate.validator;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.ProductAuthCodeService;
import org.bem.iot.validate.ProductAuthCodeVerify;

/**
 * 产品授权码验证
 * @author jakybland
 */
public class ProductAuthCodeValidator implements ConstraintValidator<ProductAuthCodeVerify, String> {
    @Resource
    ProductAuthCodeService productAuthCodeService;

    @Override
    public void initialize(ProductAuthCodeVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isEmpty(value)) {
            return false;
        }
        return !productAuthCodeService.existsNotAuthCode(value);
    }
}
