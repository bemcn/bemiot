package org.bem.iot.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 提交驱动参数
 * @author JiangShiYi
 */
@Data
public class DriveParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -6106534841762138095L;

    private Integer paramsId;

    private String paramsValue;
}
