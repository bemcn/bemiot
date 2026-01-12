package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.DataBridgeService;
import org.bem.iot.validate.DataBridgeIdVerify;

/**
 * 桥接ID验证
 * @author your-name
 */
public class DataBridgeIdValidator implements ConstraintValidator<DataBridgeIdVerify, String> {
    @Resource
    DataBridgeService dataBridgeService;

    @Override
    public void initialize(DataBridgeIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return !dataBridgeService.existsNotBridgeId(value);
    }
}