package org.bem.iot.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.bem.iot.validate.validator.DeviceUserValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 设备用户权限验证接口
 * @author jakybland
 */
@Documented
@Constraint(validatedBy = DeviceUserValidator.class)
@Target({ FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface DeviceUserVerify {
    String message() default "设备用户权限已存在";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
