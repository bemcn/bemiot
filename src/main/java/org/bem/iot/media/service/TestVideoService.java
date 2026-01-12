package org.bem.iot.media.service;

import jakarta.annotation.Resource;
import org.bem.iot.media.config.MediaServerConfig;
import org.bem.iot.media.model.param.TestVideoParam;
import org.bem.iot.media.model.result.StreamUrlResult;
import org.bem.iot.media.module.test.TestVideo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TestVideoService {
    @Resource
    private MediaServerConfig config;
    /**
     * 测试视频
     */
    private static final Map<String, TestVideo> TEST_VIDEO_MAP = new HashMap<>();


    public StreamUrlResult createTestVideo(TestVideoParam param) {
        TestVideo testVideo = new TestVideo(param);
        testVideo.initVideo();
        testVideo.startTestVideo();
        TEST_VIDEO_MAP.put(param.getApp() + param.getStream(), testVideo);
        return new StreamUrlResult(config, param);
    }


    public Boolean stopTestVideo(String app, String stream) {
        String key = app + stream;
        TestVideo testVideo = TEST_VIDEO_MAP.get(key);
        if (testVideo != null) {
            testVideo.closeVideo();
            TEST_VIDEO_MAP.remove(key);
            return true;
        }
        return false;
    }
}
