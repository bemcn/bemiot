package org.bem.iot.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.bem.iot.validate.validator.ProductAttrIdValidator;

import java.lang.annotation.*;

/**
 * 产品属性ID验证
 * @author 
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ProductAttrIdValidator.class)
public @interface ProductAttrIdVerify {
    String message() default "产品属性ID不存在";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}