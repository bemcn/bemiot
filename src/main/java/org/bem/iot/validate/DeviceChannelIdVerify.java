package org.bem.iot.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.bem.iot.validate.validator.DeviceChannelIdValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 设备视频通道ID验证接口
 * @author jakybland
 */
@Documented
@Constraint(validatedBy = DeviceChannelIdValidator.class)
@Target({ FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface DeviceChannelIdVerify {
    String message() default "设备视频通道不存在";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
