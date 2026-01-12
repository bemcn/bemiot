package org.bem.iot.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.bem.iot.validate.validator.UserNameValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用户名验证接口
 * @author jakybland
 */
@Documented
@Constraint(validatedBy = UserNameValidator.class)
@Target({ FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface UserNameVerify {
    String message() default "用户账号已经被注册";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
