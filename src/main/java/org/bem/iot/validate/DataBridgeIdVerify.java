package org.bem.iot.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.bem.iot.validate.validator.DataBridgeIdValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 桥接ID验证接口
 * @author your-name
 */
@Documented
@Constraint(validatedBy = DataBridgeIdValidator.class)
@Target({ FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface DataBridgeIdVerify {
    String message() default "数据桥接信息不存在";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}