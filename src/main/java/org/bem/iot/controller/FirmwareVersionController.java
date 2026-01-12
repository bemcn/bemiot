package org.bem.iot.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.general.FirmwareVersion;
import org.bem.iot.service.FirmwareService;
import org.bem.iot.service.FirmwareVersionService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 固件版本
 * @author jakybland
 */
@RestController
@RequestMapping("/firmwareVersion")
public class FirmwareVersionController {
    @Resource
    FirmwareVersionService firmwareVersionService;

    @Resource
    FirmwareService firmwareService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取固件列表
     * @param firmwareId 固件id
     */
    @GetMapping("/getFirmwareVersionList")
    @PublicHeadLimit
    @ResponseBody
    public void getFirmwareVersionList(@RequestParam(name="firmwareId", defaultValue="0") int firmwareId) {
        JSONObject jsonObject;
        try {
            QueryWrapper<FirmwareVersion> example = createExample(firmwareId);
            List<FirmwareVersion> list = firmwareVersionService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取固件分页列表
     * @param firmwareId 固件id
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getFirmwareVersionPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getFirmwareVersionPageList(@RequestParam(name="firmwareId", defaultValue="0") int firmwareId,
                                    @RequestParam(name="index", defaultValue = "1") Integer index,
                                    @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<FirmwareVersion> example = createExample(firmwareId);
            IPage<FirmwareVersion> page = firmwareVersionService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    private QueryWrapper<FirmwareVersion> createExample(int firmwareId) {
        if(firmwareService.existsNotFirmwareId(firmwareId)) {
            throw new RuntimeException("固件版本不存在");
        }

        QueryWrapper<FirmwareVersion> example = new QueryWrapper<>();
        example.eq("firmware_id", firmwareId);
        example.orderByDesc("release_time");
        return example;
    }

    /**
     * 获取固件
     * @param id id
     */
    @GetMapping("/getFirmwareVersion")
    @PublicHeadLimit
    @ResponseBody
    public void getFirmwareVersion(@RequestParam(name="id", defaultValue="0") long id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(firmwareVersionService.existsNotVersionId(id)) {
            jsonObject = ResponseUtil.getErrorJson("固件版本不存在");
        } else {
            FirmwareVersion data = firmwareVersionService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除固件
     * @param id id
     */
    @GetMapping("/delFirmwareVersion")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delFirmwareVersion(@RequestParam(name="id", defaultValue="") long id) {
        JSONObject jsonObject;
        try {
            if(!firmwareVersionService.existsNotVersionId(id)) {
                FirmwareVersion firVersion = firmwareVersionService.find(id);
                String version = firVersion.getVersion();
                int count = firmwareVersionService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "固件版本", "删除", "删除固件版本，【版本号】" + version);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
