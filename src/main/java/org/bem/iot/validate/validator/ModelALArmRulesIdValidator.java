package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.ModelAlarmRulesService;
import org.bem.iot.validate.ModelALArmRulesIdVerify;

/**
 * 物模型告警规则ID验证
 * @author jakybland
 */
public class ModelALArmRulesIdValidator implements ConstraintValidator<ModelALArmRulesIdVerify, Long> {
    @Resource
    ModelAlarmRulesService modelAlarmRulesService;

    @Override
    public void initialize(ModelALArmRulesIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value == 0L) {
            return true;
        }
        return !modelAlarmRulesService.existsNotModelAlarmRulesId(value);
    }
}
