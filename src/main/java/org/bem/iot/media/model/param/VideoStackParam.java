package org.bem.iot.media.model.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 拼接屏幕参数
 */
@Data
public class VideoStackParam implements Serializable {
    @Serial
    private static final long serialVersionUID = 7818766988325436821L;

    /**
     * 拼接屏任务id(流id)
     */
    @NotEmpty(message = "拼接屏任务id不为空")
    private String id;

    /**
     * 拼接屏任务流app
     */
    private String app = "live";

    /**
     * 推流地址
     * 如果传了pushUrl将不在本地产生流
     */
    private String pushUrl;

    /**
     * 拼接屏行数
     */
    @NotNull(message = "拼接屏行数不为空")
    private Integer row;

    /**
     * 拼接屏列数
     */
    @NotNull(message = "拼接屏列数不为空")
    private Integer col;

    /**
     * 拼接屏宽度
     */
    @NotNull(message = "拼接屏宽度不为空")
    private Integer width;

    /**
     * 拼接屏高度
     */
    @NotNull(message = "拼接屏高度不为空")
    private Integer height;

    /**
     * 图片链接，为空则填灰色
     */
    private String fillImgUrl;

    /**
     * 默认填充颜色
     */
    private String fillColor = "BFBFBF";

    /**
     * 是否存在分割线
     */
    private Boolean gridLineEnable = false;

    /**
     * 分割线颜色
     */
    private String gridLineColor = "000000";

    /**
     * 分割线宽度
     */
    private Integer gridLineWidth = 1;

    /**
     * 拼接屏内容
     */
    private List<VideoStackWindowParam> windowList;
}