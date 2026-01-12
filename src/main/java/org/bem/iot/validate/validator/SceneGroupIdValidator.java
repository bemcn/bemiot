package org.bem.iot.validate.validator;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.SceneGroupService;
import org.bem.iot.validate.SceneGroupIdVerify;

/**
 * 场景分组ID验证
 * @author jakybland
 */
public class SceneGroupIdValidator implements ConstraintValidator<SceneGroupIdVerify, Integer> {
    @Resource
    SceneGroupService sceneGroupService;

    @Override
    public void initialize(SceneGroupIdVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !sceneGroupService.existsNotSceneGroupId(value);
    }
}
