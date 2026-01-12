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
import org.bem.iot.model.general.Drive;
import org.bem.iot.service.DriveService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 驱动管理
 * @author jakybland
 */
@RestController
@RequestMapping("/drive")
public class DriveController {
    @Resource
    DriveService driveService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取驱动列表
     * @param status 驱动状态 1：停止 2：启动
     * @param filed 查询字段 code/name/protocol/source/author
     * @param key 关键字
     * @param startDate 查询开始日期
     * @param endDate 查询截止日期
     */
    @GetMapping("/getDriveList")
    @PublicHeadLimit
    @ResponseBody
    public void getDriveList(@RequestParam(name="status", defaultValue="0") int status,
                            @RequestParam(name="filed", defaultValue="") String filed,
                            @RequestParam(name="key", defaultValue="") String key,
                            @RequestParam(name="startDate", defaultValue="") String startDate,
                            @RequestParam(name="endDate", defaultValue="") String endDate) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Drive> example = createExample(status, filed, key, startDate, endDate);
            List<Drive> list = driveService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取驱动分页列表
     * @param status 驱动状态 1：停止 2：启动
     * @param filed 查询字段 code/name/protocol/source/author
     * @param key 关键字
     * @param startDate 查询开始日期
     * @param endDate 查询截止日期
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getDrivePageList")
    @PublicHeadLimit
    @ResponseBody
    public void getDrivePageList(@RequestParam(name="status", defaultValue="0") int status,
                                @RequestParam(name="filed", defaultValue="") String filed,
                                @RequestParam(name="key", defaultValue="") String key,
                                @RequestParam(name="startDate", defaultValue="") String startDate,
                                @RequestParam(name="endDate", defaultValue="") String endDate,
                                @RequestParam(name="index", defaultValue = "1") Integer index,
                                @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Drive> example = createExample(status, filed, key, startDate, endDate);
            IPage<Drive> page = driveService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    private QueryWrapper<Drive> createExample(int status, String filed, String key, String startDate, String endDate) {
        QueryWrapper<Drive> example = new QueryWrapper<>();
        if(status > 0) {
            example.eq("status", status);
        }
        if(!StrUtil.isEmpty(filed) && !StrUtil.isEmpty(key)) {
            switch(filed) {
                case "code":
                    example.like("drive_code", key);
                    break;
                case "name":
                    example.like("drive_name", key);
                    break;
                case "source":
                    example.like("drive_source", key);
                    break;
            }
        }
        if(!StrUtil.isEmpty(startDate) && !StrUtil.isEmpty(endDate)) {
            Date starTime;
            Date endTime;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                starTime = format.parse(startDate);
                endTime = format.parse(endDate);
                example.between("release_time", starTime, endTime);
            } catch (ParseException ignored) {
            }
        }
        example.orderByDesc("release_time");
        return example;
    }

    /**
     * 获取驱动
     * @param driveCode 驱动编号
     */
    @GetMapping("/getDrive")
    @PublicHeadLimit
    @ResponseBody
    public void getDrive(@RequestParam(name="driveCode", defaultValue="") String driveCode) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(driveCode)) {
            jsonObject = ResponseUtil.getErrorJson("驱动编号不能为空");
        } else if(driveService.existsNotDriveCode(driveCode)) {
            jsonObject = ResponseUtil.getErrorJson("驱动信息不存在");
        } else {
            Drive data = driveService.find(driveCode);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增驱动
     * @param record 驱动
     */
    @PostMapping("/addDrive")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addDrive(@Validated @Valid Drive record) {
        JSONObject jsonObject;
        try {
            String regex = "^(v\\d+\\.\\d+(\\.\\d+)*?)$";
            if (!Pattern.matches(regex, record.getVersion())) {
                jsonObject = ResponseUtil.getErrorJson("版本格式错误");
            } else {
                driveService.insert(record);
                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "驱动信息", "新增", "新增驱动，【名称】" + record.getDriveName());

                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑驱动
     * @param record 驱动
     */
    @PostMapping("/editDrive")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editDrive(@Validated @Valid Drive record) {
        JSONObject jsonObject;
        try {
            String regex = "^(v\\d+\\.\\d+(\\.\\d+)*?)$";
            if (!Pattern.matches(regex, record.getVersion())) {
                jsonObject = ResponseUtil.getErrorJson("版本格式错误");
            } else {
                driveService.update(record);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "驱动信息", "修改", "修改驱动，【名称】" + record.getDriveName());

                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 启动
     * @param driveCode 驱动编号
     */
    @GetMapping("/startDrive")
    @PublicHeadLimit("user")
    @ResponseBody
    public void startDrive(@RequestParam(name="driveCode", defaultValue="") String driveCode) {
        JSONObject jsonObject;
        try {
            if(!driveService.existsNotDriveCode(driveCode)) {
                Drive drive = driveService.find(driveCode);
                String driveName = drive.getDriveName();
                driveService.starting(driveCode);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "驱动信息", "操作", "启动驱动，【名称】" + driveName);
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 停止
     * @param driveCode 驱动编号
     */
    @GetMapping("/stopDrive")
    @PublicHeadLimit("user")
    @ResponseBody
    public void stopDrive(@RequestParam(name="driveCode", defaultValue="") String driveCode) {
        JSONObject jsonObject;
        try {
            if(!driveService.existsNotDriveCode(driveCode)) {
                Drive drive = driveService.find(driveCode);
                String driveName = drive.getDriveName();
                driveService.stoping(driveCode);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "驱动信息", "操作", "停止驱动，【名称】" + driveName);
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除驱动
     * @param driveCode 驱动编号
     */
    @GetMapping("/delDrive")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDrive(@RequestParam(name="driveCode", defaultValue="") String driveCode) {
        JSONObject jsonObject;
        try {
            if(!driveService.existsNotDriveCode(driveCode)) {
                Drive drive = driveService.find(driveCode);
                String driveName = drive.getDriveName();
                int count = driveService.del(driveCode);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "驱动信息", "删除", "删除驱动，【名称】" + driveName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个驱动
     * @param cedes 驱动编号集合
     */
    @GetMapping("/delDrives")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDrives(@RequestParam(name="cedes", defaultValue="") String cedes) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(cedes)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<String> codeList = Arrays.stream(cedes.split(",")).toList();
                int count = driveService.delArray(codeList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "驱动信息", "删除", "批量删除" + count + "条驱动信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
