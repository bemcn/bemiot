package org.bem.iot.media.model.param;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 拼接屏幕地址参数
 */
@Data
public class VideoStackWindowParam implements Serializable {
    @Serial
    private static final long serialVersionUID = 826863020062148475L;

    /**
     * 拼接视频地址
     */
    private String videoUrl;

    /**
     * 拼接图片地址
     * 和上面二选一
     */
    private String imgUrl;

    /**
     * 默认填充颜色
     */
    private String fillColor = "BFBFBF";

    /**
     * 所占的格子
     */
    private List<Integer> span;
}