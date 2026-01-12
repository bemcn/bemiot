package org.bem.iot.model.log;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.bem.iot.model.scene.SceneGroup;
import org.bem.iot.model.scene.SceneLinkage;

import java.io.Serial;
import java.io.Serializable;

/**
 * 场景联动执行日志
 * @author JiangShiYi
 */
@Data
@TableName("bemcn.log_scene_linkage")
public class LogSceneLinkage implements Serializable {
    @Serial
    private static final long serialVersionUID = -8018689653374736485L;

    /**
     * 时间戳
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Long ts;

    /**
     * id
     */
    @TableField("log_id")
    private String logId;

    /**
     * 联动id
     */
    @TableField("scene_id")
    private Long sceneId;

    /**
     * 分组ID
     */
    @TableField("scene_group_id")
    private Integer sceneGroupId;

    /**
     * 状态 1：成功 2：失败
     */
    @TableField("status")
    private Integer status;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 关联场景联动
     */
    @TableField(exist = false)
    private SceneLinkage scene;

    /**
     * 关联场景分组
     */
    @TableField(exist = false)
    private SceneGroup group;
}
