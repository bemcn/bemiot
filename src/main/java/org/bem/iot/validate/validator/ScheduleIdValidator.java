package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.ScheduleService;
import org.bem.iot.validate.ScheduleIdVerify;

/**
 * 定时任务ID验证
 * @author jakybland
 */
public class ScheduleIdValidator implements ConstraintValidator<ScheduleIdVerify, Integer> {
    @Resource
    ScheduleService scheduleService;

    @Override
    public void initialize(ScheduleIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !scheduleService.existsNotScheduleId(value);
    }
}
