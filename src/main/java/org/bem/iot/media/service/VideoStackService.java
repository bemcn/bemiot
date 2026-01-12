package org.bem.iot.media.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.aizuda.zlm4j.structure.MK_MEDIA_SOURCE;
import jakarta.annotation.Resource;
import org.bem.iot.media.config.MediaServerConfig;
import org.bem.iot.media.constants.MediaServerConstants;
import org.bem.iot.media.model.param.VideoStackParam;
import org.bem.iot.media.module.stack.VideoStack;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.bem.iot.media.context.MediaServerContext.ZLM_API;

@Service
public class VideoStackService {
    @Resource
    private MediaServerConfig mediaServerConfig;

    private final static Map<String, VideoStack> VIDEO_STACK_MAP = new HashMap<>();


    /**
     * 开启拼接屏任务
     * @param param 拼接屏幕参数
     */
    public void startStack(VideoStackParam param) {
        if (StrUtil.isBlank(param.getPushUrl())) {
            MK_MEDIA_SOURCE mkMediaSource = ZLM_API.mk_media_source_find2("rtmp", MediaServerConstants.DEFAULT_VHOST, param.getApp(), param.getId(), 0);
            Assert.isNull(mkMediaSource, "当前流已在线");
        }
        Assert.isFalse(VIDEO_STACK_MAP.containsKey(param.getId()), "拼接屏任务已存在");
        //String pushUrl = StrUtil.format("rtmp://127.0.0.1:{}/{}/{}", mediaServerConfig.getRtmp_port(), param.getApp(), param.getId());
        VideoStack videoStack = new VideoStack(param);
        videoStack.init();
        VIDEO_STACK_MAP.put(param.getId(), videoStack);
    }


    /**
     * 重设拼接屏任务
     * @param param 拼接屏幕参数
     */
    public void resetStack(VideoStackParam param) {
        VideoStack videoStack = VIDEO_STACK_MAP.get(param.getId());
        Assert.isTrue(VIDEO_STACK_MAP.containsKey(param.getId()), "拼接屏任务不存在");
        videoStack.reset(param);
    }

    /**
     * 停止拼接屏任务
     * @param id 任务ID
     */
    public void stopStack(String id) {
        VideoStack videoStack = VIDEO_STACK_MAP.get(id);
        Assert.isTrue(VIDEO_STACK_MAP.containsKey(id), "拼接屏任务不存在");
        videoStack.stop();
        VIDEO_STACK_MAP.remove(id);
    }
}
