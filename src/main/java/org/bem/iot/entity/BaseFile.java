package org.bem.iot.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录提交参数
 * @author jakybland
 */
@Data
public class BaseFile implements Serializable {
    @Serial
    private static final long serialVersionUID = -3046953457477872185L;

    /**
     * Base64文件数据
     */
    private String fileData;

    /**
     * 访问路径
     */
    private String url;

    /**
     * 存储磁盘目录
     */
    private String saveDir;

    /**
     * 存储文件名称
     */
    private String saveName;

    /**
     * 存储磁盘路径
     */
    private String saveUrl;

    public BaseFile(String fileData, String url, String saveDir, String saveName, String saveUrl) {
        this.fileData = fileData;
        this.url = url;
        this.saveDir = saveDir;
        this.saveName = saveName;
        this.saveUrl = saveUrl;
    }
}
