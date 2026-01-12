package org.bem.iot.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 刷新Token
 * @author JiangShiYi
 */
@Data
public class ReturnToken implements Serializable {
    @Serial
    private static final long serialVersionUID = 6199925804475353761L;

    private Boolean status;

    private String accessToken;

    private String refreshToken;
}
