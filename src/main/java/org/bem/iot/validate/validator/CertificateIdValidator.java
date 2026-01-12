package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.CertificateService;
import org.bem.iot.validate.CertificateIdVerify;

/**
 * 证书ID验证
 * @author jakybland
 */
public class CertificateIdValidator implements ConstraintValidator<CertificateIdVerify, Integer> {
    @Resource
    CertificateService certificateService;

    @Override
    public void initialize(CertificateIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !certificateService.existsNotCertificateId(value);
    }
}
