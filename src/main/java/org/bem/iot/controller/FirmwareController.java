package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.general.Firmware;
import org.bem.iot.service.FirmwareService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 固件管理
 * @author jakybland
 */
@RestController
@RequestMapping("/firmware")
public class FirmwareController {
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
     * @param installType 安装类型 1：http 2：分包拉取
     * @param key 关键字
     */
    @GetMapping("/getFirmwareList")
    @PublicHeadLimit
    @ResponseBody
    public void getFirmwareList(@RequestParam(name="installType", defaultValue="0") int installType,
                                @RequestParam(name="key", defaultValue="") String key) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Firmware> example = createExample(installType, key);
            List<Firmware> list = firmwareService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取固件分页列表
     * @param installType 安装类型 1：http 2：分包拉取
     * @param key 关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getFirmwarePageList")
    @PublicHeadLimit
    @ResponseBody
    public void getFirmwarePageList(@RequestParam(name="installType", defaultValue="0") int installType,
                                    @RequestParam(name="key", defaultValue="") String key,
                                    @RequestParam(name="index", defaultValue = "1") Integer index,
                                    @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Firmware> example = createExample(installType, key);
            IPage<Firmware> page = firmwareService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    private QueryWrapper<Firmware> createExample(int installType, String key) {
        QueryWrapper<Firmware> example = new QueryWrapper<>();
        if(installType > 0) {
            example.eq("install_type", installType);
        }
        if(!StrUtil.isEmpty(key)) {
            example.like("firmware_name", key);
        }
        example.orderByDesc("firmware_id");
        return example;
    }

    /**
     * 获取固件
     * @param id id
     */
    @GetMapping("/getFirmware")
    @PublicHeadLimit
    @ResponseBody
    public void getFirmware(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(firmwareService.existsNotFirmwareId(id)) {
            jsonObject = ResponseUtil.getErrorJson("固件信息不存在");
        } else {
            Firmware data = firmwareService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增固件
     * @param record 固件
     */
    @PostMapping("/addFirmware")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addFirmware(@Validated(Add.class) @Valid Firmware record) {
        JSONObject jsonObject;
        try {String regex = "^(v\\d+\\.\\d+(\\.\\d+)*?)$";
            if (!Pattern.matches(regex, record.getVersion())) {
                jsonObject = ResponseUtil.getErrorJson("版本格式错误");
            } else {
                firmwareService.insert(record);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "固件信息", "新增", "新增固件，【名称】" + record.getFirmwareName());

                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑固件
     * @param record 固件
     */
    @PostMapping("/editFirmware")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editFirmware(@Validated(Edit.class) @Valid Firmware record) {
        JSONObject jsonObject;
        try {
            String regex = "^(v\\d+\\.\\d+(\\.\\d+)*?)$";
            if (!Pattern.matches(regex, record.getVersion())) {
                jsonObject = ResponseUtil.getErrorJson("版本格式错误");
            } else {
                firmwareService.update(record);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "固件信息", "修改", "修改固件，【名称】" + record.getFirmwareName());

                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除固件
     * @param id id
     */
    @GetMapping("/delFirmware")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delFirmware(@RequestParam(name="id", defaultValue="") int id) {
        JSONObject jsonObject;
        try {
            if(!firmwareService.existsNotFirmwareId(id)) {
                Firmware firmware = firmwareService.find(id);
                String firmwareName = firmware.getFirmwareName();
                int count = firmwareService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "固件信息", "删除", "删除固件，【名称】" + firmwareName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个固件
     * @param ids id集合
     */
    @GetMapping("/delFirmwares")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delFirmwares(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
                int count = firmwareService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "固件信息", "删除", "批量删除" + count + "条固件信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
