package org.bem.iot.validate.validator;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bem.iot.service.DriveService;
import org.bem.iot.validate.DriveCodeVerify;

/**
 * 驱动编号验证
 * @author jakybland
 */
public class DriveCodeValidator implements ConstraintValidator<DriveCodeVerify, String> {
    @Resource
    DriveService driveService;


    @Override
    public void initialize(DriveCodeVerify constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isEmpty(value)) {
            return false;
        }
        return !driveService.existsNotDriveCode(value);
    }
}
