package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.SystemNoticeService;
import org.bem.iot.validate.NoticeIdVerify;

/**
 * 通知公告ID验证
 * @author jakybland
 */
public class NoticeIdValidator implements ConstraintValidator<NoticeIdVerify, Integer> {
    @Resource
    SystemNoticeService systemNoticeService;

    @Override
    public void initialize(NoticeIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !systemNoticeService.existNotNotice(value);
    }
}
