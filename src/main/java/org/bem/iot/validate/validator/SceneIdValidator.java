package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.SceneLinkageService;
import org.bem.iot.validate.SceneIdVerify;

/**
 * 场景联动ID验证
 * @author jakybland
 */
public class SceneIdValidator implements ConstraintValidator<SceneIdVerify, Long> {
    @Resource
    SceneLinkageService sceneLinkageService;

    @Override
    public void initialize(SceneIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !sceneLinkageService.existsNotSceneId(value);
    }
}
