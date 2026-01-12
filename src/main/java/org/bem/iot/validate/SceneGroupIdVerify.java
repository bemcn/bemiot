package org.bem.iot.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.bem.iot.validate.validator.SceneGroupIdValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 场景分组ID验证接口
 * @author jakybland
 */
@Documented
@Constraint(validatedBy = SceneGroupIdValidator.class)
@Target({ FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface SceneGroupIdVerify {
    String message() default "场景分组信息不存在";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
