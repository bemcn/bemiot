package org.bem.iot.media.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.aizuda.zlm4j.structure.MK_MEDIA_SOURCE;
import jakarta.annotation.Resource;
import org.bem.iot.media.config.MediaServerConfig;
import org.bem.iot.media.constants.MediaServerConstants;
import org.bem.iot.media.model.param.TranscodeParam;
import org.bem.iot.media.module.transcode.Transcode;
import org.bem.iot.media.pool.MediaServerThreadPool;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.bem.iot.media.context.MediaServerContext.ZLM_API;

/**
 * 转码
 */
@Service
public class TranscodeService {

    @Resource
    private MediaServerConfig mediaServerConfig;
    private final Map<String, Transcode> TRANSCODE_MAP = new HashMap<>();

    /**
     * 转码
     * @param param 转码参数
     * @return 返回push路径
     */
    public String transcode(TranscodeParam param) throws Exception {
        try {
            MK_MEDIA_SOURCE mkMediaSource = ZLM_API.mk_media_source_find2("rtmp", MediaServerConstants.DEFAULT_VHOST, param.getApp(), param.getStream(), 0);
            Assert.isNull(mkMediaSource, "当前流已在线");
            Assert.isFalse(TRANSCODE_MAP.containsKey(param.getStream()), "转码任务已存在");
            String pushUrl = StrUtil.format("rtmp://127.0.0.1:{}/{}/{}", mediaServerConfig.getRtmp_port(), param.getApp(), param.getStream());
            Transcode transcode = new Transcode(param, pushUrl);
            MediaServerThreadPool.execute(transcode::start);
            TRANSCODE_MAP.put(param.getStream(), transcode);
            return pushUrl;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * 停止转码
     * @param stream 流ID
     */
    public void stopTranscode(String stream) {
        Transcode transcode = TRANSCODE_MAP.get(stream);
        if (transcode!=null){
            transcode.stop();
            TRANSCODE_MAP.remove(stream);
        }
    }
}
