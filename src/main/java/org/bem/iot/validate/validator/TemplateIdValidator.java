package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.MsgTemplateService;
import org.bem.iot.validate.TemplateIdVerify;

/**
 * 消息模板ID验证
 * @author jakybland
 */
public class TemplateIdValidator implements ConstraintValidator<TemplateIdVerify, Long> {
    @Resource
    MsgTemplateService msgTemplateService;

    @Override
    public void initialize(TemplateIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !msgTemplateService.existsNotTemplateId(value);
    }
}
