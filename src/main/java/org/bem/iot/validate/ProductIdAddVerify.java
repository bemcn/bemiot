package org.bem.iot.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.bem.iot.validate.validator.ProductIdAddValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 产品ID验证接口
 * @author jakybland
 */
@Documented
@Constraint(validatedBy = ProductIdAddValidator.class)
@Target({ FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface ProductIdAddVerify {
    String message() default "产品ID被使用";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
