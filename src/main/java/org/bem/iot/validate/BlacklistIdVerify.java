package org.bem.iot.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.bem.iot.validate.validator.BlacklistIdValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 黑名单ID验证接口
 * @author jakybland
 */
@Documented
@Constraint(validatedBy = BlacklistIdValidator.class)
@Target({ FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface BlacklistIdVerify {
    String message() default "黑名单信息不存在";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
