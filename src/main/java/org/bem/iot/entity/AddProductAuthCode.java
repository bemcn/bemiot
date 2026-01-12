package org.bem.iot.entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.bem.iot.validate.ProductIdVerify;

import java.io.Serial;
import java.io.Serializable;

/**
 * 生成产品授权码
 * @author jakybland
 */
@Data
public class AddProductAuthCode implements Serializable {
    @Serial
    private static final long serialVersionUID = -3379123413048229788L;

    @NotEmpty(message = "产品ID不能为空")
    @ProductIdVerify()
    private String productId;

    @NotEmpty(message = "生成数量不能为空")
    @Min(value = 1, message = "生成数量不能小于1")
    @Max(value = 100, message = "生成数量不能大于100")
    private Integer number;
}
