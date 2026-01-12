package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.ProtocolsService;
import org.bem.iot.validate.ProtocolIdVerify;

/**
 * 通讯协议ID验证
 * @author jakybland
 */
public class ProtocolIdValidator implements ConstraintValidator<ProtocolIdVerify, Integer> {
    @Resource
    ProtocolsService protocolsService;

    @Override
    public void initialize(ProtocolIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }
        return !protocolsService.existsNotProtocolsId(value);
    }
}
