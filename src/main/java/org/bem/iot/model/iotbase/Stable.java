package org.bem.iot.model.iotbase;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Stable implements Serializable {
    @Serial
    private static final long serialVersionUID = 2662374721251810884L;

    /**
     * 字段名称
     */
    private String field;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 字段长度
     */
    private Integer length;

    /**
     * 字段描述
     */
    private String note;

    /**
     * 字段编码
     */
    private String encode;

    /**
     * 压缩方式
     */
    private String compress;

    /**
     * 等级
     */
    private String level;
}
