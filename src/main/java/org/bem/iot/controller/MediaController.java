package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.media.model.param.*;
import org.bem.iot.media.model.result.MediaInfoResult;
import org.bem.iot.media.model.result.RtpServerResult;
import org.bem.iot.media.model.result.Statistic;
import org.bem.iot.media.model.result.StreamUrlResult;
import org.bem.iot.media.service.*;
import org.bem.iot.util.ResponseUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 媒体接口
 * @author jaky
 */
@RestController
@RequestMapping("/media")
public class MediaController {
    @Resource
    ApiService iApiService;

    @Resource
    SnapService iSnapService;

    @Resource
    TranscodeService transcodeService;

    @Resource
    VideoStackService iVideoStackService;

    @Resource
    TestVideoService iTestVideoService;

    @Resource
    HttpServletResponse response;

    /**
     * 【拉流代理】添加拉流代理
     * 已支持webrtc代理 url格式 webrtc://127.0.0.1:8899/live/test
     * @param param 拉流代理参数
     */
    @PostMapping(value = "/addStreamProxy")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addStreamProxy(@Validated @Valid StreamProxyParam param) {
        JSONObject jsonObject;
        try {
            StreamUrlResult result = iApiService.addStreamProxy(param);
            jsonObject = ResponseUtil.getSuccessJson(result);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【拉流代理】关闭拉流代理
     * 流注册成功后，也可以使用close_streams接口替代
     * @param key 关联值的键
     */
    @PostMapping(value = "/delStreamProxy")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delStreamProxy(@RequestParam(name="key", defaultValue="") String key) {
        Boolean flag = iApiService.delStreamProxy(key);
        JSONObject jsonObject = ResponseUtil.getSuccessJson(flag);
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【推流代理】添加推流代理
     * 已支持webrtc代理 url格式 webrtc://127.0.0.1:8899/live/test
     * @param param 推流代理参数
     */
    @PostMapping(value = "/addStreamPusherProxy")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addStreamPusherProxy(@Validated @Valid StreamPushProxyParam param) {
        String error = iApiService.addStreamPusherProxy(param);
        JSONObject jsonObject = ResponseUtil.getSuccessJson(error);
        ResponseUtil.responseData(jsonObject, response);
    }


    /**
     * 【推流代理】删除推流代理
     * @param key 推流代理Key
     */
    @PostMapping(value = "/delStreamPusherProxy")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delStreamPusherProxy(@RequestParam(name="key", defaultValue="") String key) {
        Boolean flag = iApiService.delStreamPusherProxy(key);
        JSONObject jsonObject = ResponseUtil.getSuccessJson(flag);
        ResponseUtil.responseData(jsonObject, response);
    }


    /**
     * 【流操作】关闭流
     * @param param 关闭流请求参数
     */
    @PostMapping(value = "/closeStream")
    @PublicHeadLimit("user")
    @ResponseBody
    public void closeStream(@Validated @Valid CloseStreamParam param) {
        Integer status = iApiService.closeStream(param);
        JSONObject jsonObject = ResponseUtil.getSuccessJson(status);
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【流操作】关闭流(批量关)
     * @param param 关闭流请求参数
     */
    @PostMapping(value = "/closeStreams")
    @PublicHeadLimit("user")
    @ResponseBody
    public void closeStreams(CloseStreamsParam param) {
        Integer status = iApiService.closeStreams(param);
        JSONObject jsonObject = ResponseUtil.getSuccessJson(status);
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【流操作】获取流列表
     * @param param 请求参数
     */
    @GetMapping(value = "/getMediaList")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getMediaList(GetMediaListParam param) {
        List<MediaInfoResult> list = iApiService.getMediaList(param);
        JSONObject jsonObject = ResponseUtil.getSuccessJson(list);
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【流操作】获取流信息
     * @param param 请求参数
     */
    @GetMapping(value = "/getMediaInfo")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getMediaInfo(@Validated @Valid MediaQueryParam param) {
        MediaInfoResult info = iApiService.getMediaInfo(param);
        JSONObject jsonObject = ResponseUtil.getSuccessJson(info);
        ResponseUtil.responseData(jsonObject, response);
    }


    /**
     * 【流操作】流是否在线
     * @param param 请求参数
     */
    @GetMapping(value = "/isMediaOnline")
    @PublicHeadLimit("user")
    @ResponseBody
    public void isMediaOnline(@Validated @Valid MediaQueryParam param) {
        Boolean online = iApiService.isMediaOnline(param);
        JSONObject jsonObject = ResponseUtil.getSuccessJson(online);
        ResponseUtil.responseData(jsonObject, response);
    }


    /**
     * 【录像】开始录像
     * @param param 开始录像参数
     */
    @PostMapping(value = "/startRecord")
    @PublicHeadLimit("user")
    @ResponseBody
    public void startRecord(@Validated @Valid StartRecordParam param) {
        Boolean flag = iApiService.startRecord(param);
        JSONObject jsonObject = ResponseUtil.getSuccessJson(flag);
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【录像】停止录像
     * @param param 开始录像参数
     */
    @PostMapping(value = "/stopRecord")
    @PublicHeadLimit("user")
    @ResponseBody
    public void stopRecord(@Validated @Valid StopRecordParam param) {
        Boolean flag = iApiService.stopRecord(param);
        JSONObject jsonObject = ResponseUtil.getSuccessJson(flag);
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【录像】是否录像中
     * @param param 开始录像参数
     */
    @GetMapping(value = "/isRecording")
    @PublicHeadLimit("user")
    @ResponseBody
    public void isRecording(@Validated @Valid RecordStatusParam param) {
        Boolean flag = iApiService.isRecording(param);
        JSONObject jsonObject = ResponseUtil.getSuccessJson(flag);
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【系统】获取内存资源信息
     */
    @GetMapping(value = "/getStatistic")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getStatistic() {
        Statistic statistic = iApiService.getStatistic();
        JSONObject jsonObject = ResponseUtil.getSuccessJson(statistic);
        ResponseUtil.responseData(jsonObject, response);
    }


    /**
     * 【系统】获取服务器配置
     */
    @GetMapping(value = "/getServerConfig")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getServerConfig() {
        String confStr = iApiService.getServerConfig();
        JSONObject jsonObject = ResponseUtil.getSuccessJson(confStr);
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【系统】重启流媒体服务
     */
    @PostMapping(value = "/restartServer")
    @PublicHeadLimit("user")
    @ResponseBody
    public void restartServer() {
        Boolean status = iApiService.restartServer();
        JSONObject jsonObject = ResponseUtil.getSuccessJson(status);
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【系统】设置服务器配置
     * @param request 请求
     */
    @PostMapping(value = "/setServerConfig")
    @PublicHeadLimit("user")
    @ResponseBody
    public void setServerConfig(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Integer size = iApiService.setServerConfig(parameterMap);
        JSONObject jsonObject = ResponseUtil.getSuccessJson(size);
        ResponseUtil.responseData(jsonObject, response);
    }


    /**
     * 【系统】开启rtp服务
     * @param param 开始录像参数
     */
    @PostMapping(value = "/openRtpServer")
    @PublicHeadLimit("user")
    @ResponseBody
    public void openRtpServer(@Validated @Valid OpenRtpServerParam param) {
        Integer port = iApiService.openRtpServer(param);
        JSONObject jsonObject = ResponseUtil.getSuccessJson(port);
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【系统】关闭rtp服务
     * @param stream 流id
     */
    @PostMapping(value = "/closeRtpServer")
    @PublicHeadLimit("user")
    @ResponseBody
    public void closeRtpServer(@RequestParam(name="stream", defaultValue="") String stream) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(stream)) {
            jsonObject = ResponseUtil.getErrorJson("流id不能为空");
        } else {
            Integer status = iApiService.closeRtpServer(stream);
            jsonObject = ResponseUtil.getSuccessJson(status);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【系统】获取所有RTP服务器
     */
    @GetMapping(value = "/listRtpServer")
    @PublicHeadLimit("user")
    @ResponseBody
    public void listRtpServer() {
        List<RtpServerResult> list = iApiService.listRtpServer();
        JSONObject jsonObject = ResponseUtil.getSuccessJson(list);
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【截图】获取截图
     * rtsp地址支持H264、H265，rtmp/http-flv只支持H264
     * @param url 截图流地址
     */
    @GetMapping(value = "/getSnap")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getSnap(String url) {
        iSnapService.getSnap(url, response);

    }

    /**
     * 【转码】拉流代理转码(beta)
     * 默认H265转H264 支持分辨率调整 暂时只支持视频转码
     * @param param 转码参数
     */
    @PostMapping(value = "/transcode")
    @PublicHeadLimit("user")
    @ResponseBody
    public void transcode(@Validated @Valid TranscodeParam param) {
        JSONObject jsonObject;
        try {
            String pushUrl = transcodeService.transcode(param);
            jsonObject = ResponseUtil.getSuccessJson(pushUrl);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【转码】停止转码
     * @param stream 流id
     */
    @PostMapping(value = "/stopTranscode")
    @PublicHeadLimit("user")
    @ResponseBody
    public void stopTranscode(@RequestParam(name="stream", defaultValue="") String stream) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(stream)) {
            jsonObject = ResponseUtil.getErrorJson("流id不能为空");
        } else {
            transcodeService.stopTranscode(stream);
            jsonObject = ResponseUtil.getSuccessJson();
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【拼接屏】开启拼接屏(beta)
     * @param param 拼接屏幕参
     */
    @PostMapping(value = "/stackStart")
    @PublicHeadLimit("user")
    @ResponseBody
    public void startStack(@Validated @Valid VideoStackParamFrm param) {
        List<VideoStackWindowParam> windowList = new ArrayList<>();
        if(StrUtil.isNotEmpty(param.getWindowListStr())) {
            windowList = JSON.parseArray(param.getWindowListStr(), VideoStackWindowParam.class);
        }

        VideoStackParam stackParam = new VideoStackParam();
        stackParam.setId(param.getId());
        stackParam.setApp(param.getApp());
        stackParam.setPushUrl(param.getPushUrl());
        stackParam.setRow(param.getRow());
        stackParam.setCol(param.getCol());
        stackParam.setWidth(param.getWidth());
        stackParam.setHeight(param.getHeight());
        stackParam.setFillImgUrl(param.getFillImgUrl());
        stackParam.setFillColor(param.getFillColor());
        stackParam.setGridLineEnable(param.getGridLineEnable());
        stackParam.setGridLineColor(param.getGridLineColor());
        stackParam.setGridLineWidth(param.getGridLineWidth());
        stackParam.setWindowList(windowList);
        iVideoStackService.startStack(stackParam);

        JSONObject jsonObject = ResponseUtil.getSuccessJson();
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【拼接屏】重新设置拼接屏(beta)
     * @param param 拼接屏幕参
     */
    @PostMapping(value = "/stackReset")
    @PublicHeadLimit("user")
    @ResponseBody
    public void stackReset(@Validated @Valid VideoStackParamFrm param) {
        List<VideoStackWindowParam> windowList = new ArrayList<>();
        if(StrUtil.isNotEmpty(param.getWindowListStr())) {
            windowList = JSON.parseArray(param.getWindowListStr(), VideoStackWindowParam.class);
        }

        VideoStackParam stackParam = new VideoStackParam();
        stackParam.setId(param.getId());
        stackParam.setApp(param.getApp());
        stackParam.setPushUrl(param.getPushUrl());
        stackParam.setRow(param.getRow());
        stackParam.setCol(param.getCol());
        stackParam.setWidth(param.getWidth());
        stackParam.setHeight(param.getHeight());
        stackParam.setFillImgUrl(param.getFillImgUrl());
        stackParam.setFillColor(param.getFillColor());
        stackParam.setGridLineEnable(param.getGridLineEnable());
        stackParam.setGridLineColor(param.getGridLineColor());
        stackParam.setGridLineWidth(param.getGridLineWidth());
        stackParam.setWindowList(windowList);

        iVideoStackService.resetStack(stackParam);
        JSONObject jsonObject = ResponseUtil.getSuccessJson();
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【拼接屏】关闭拼接屏(beta)
     * @param id 拼接屏任务id
     */
    @PostMapping(value = "/stackStop")
    @PublicHeadLimit("user")
    @ResponseBody
    public void stackStop(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(id)) {
            jsonObject = ResponseUtil.getErrorJson("拼接屏任务id不能为空");
        } else {
            iVideoStackService.stopStack(id);
            jsonObject = ResponseUtil.getSuccessJson();
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【测试视频流】生成一路测试视频流
     * @param param 测试流参数
     */
    @PostMapping(value = "/createTestVideo")
    @PublicHeadLimit("user")
    @ResponseBody
    public void createTestVideo(@Validated @Valid TestVideoParam param) {
        StreamUrlResult result = iTestVideoService.createTestVideo(param);
        JSONObject jsonObject = ResponseUtil.getSuccessJson(result);
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 【测试视频流】停止一路测试视频流
     * @param param 关闭测试流参数
     */
    @PostMapping(value = "/stopTestVideo")
    @PublicHeadLimit("user")
    @ResponseBody
    public void stopTestVideo(@Validated @RequestBody CloseTestVideoParam param) {
        Boolean result = iTestVideoService.stopTestVideo(param.getApp(), param.getStream());
        JSONObject jsonObject = ResponseUtil.getSuccessJson(result);
        ResponseUtil.responseData(jsonObject, response);
    }
}
