package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.entity.UserExp;
import org.bem.iot.model.log.LogSystem;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.service.LoginService;
import org.bem.iot.util.ExcelUtil;
import org.bem.iot.util.ResponseUtil;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 系统日志
 * @author jakybland
 */
@RestController
@RequestMapping("/log")
public class LogSystemController {
    @Resource
    LogSystemService logSystemService;

    @Resource
    LoginService loginService;

    @Resource
    HttpServletResponse response;

    /**
     * 获取日志分页列表
     * @param source 客户端来源
     * @param filed 查询字段 ipAddress/userName/nickName/modelName
     * @param key 关键字
     * @param operation 操作模式 新增/修改/删除/导出/导入/审核/登录/登出/操作
     * @param startDate 查询开始日期
     * @param endDate 查询截止日期
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getLogPageList")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getLogPageList(@RequestParam(name="source", defaultValue="") String source,
                               @RequestParam(name="filed", defaultValue="") String filed,
                               @RequestParam(name="key", defaultValue="") String key,
                               @RequestParam(name="operation", defaultValue="") String operation,
                               @RequestParam(name="startDate", defaultValue="") String startDate,
                               @RequestParam(name="endDate", defaultValue="") String endDate,
                               @RequestParam(name="index", defaultValue = "1") Integer index,
                               @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<LogSystem> example = new QueryWrapper<>();
            if(!StrUtil.isEmpty(source)) {
                example.eq("client_source", source);
            }
            if(!StrUtil.isEmpty(filed) && !StrUtil.isEmpty(key)) {
                switch(filed) {
                    case "ipAddress":
                        example.like("client_ip", key);
                        break;
                    case "userName":
                        example.like("user_name", key);
                        break;
                    case "nickName":
                        example.like("nick_name", key);
                        break;
                    case "modelName":
                        example.like("model_name", key);
                        break;
                }
            }
            if(!StrUtil.isEmpty(operation)) {
                example.eq("operation", operation);
            }
            if(!StrUtil.isEmpty(startDate) && !StrUtil.isEmpty(endDate)) {
                Date starTime;
                Date endTime;
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    starTime = format.parse(startDate);
                    endTime = format.parse(endDate);
                    example.between("ts", starTime.getTime(), endTime.getTime());
                } catch (ParseException ignored) {
                }
            }
            example.orderByDesc("ts");
            IPage<LogSystem> page = logSystemService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除日志
     * @param id id
     */
    @GetMapping("/delLog")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delLog(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        try {
            logSystemService.del(id);
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个日志
     * @param ids id集合
     */
    @GetMapping("/delLogs")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delLogs(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<String> idList = Arrays.stream(ids.split(",")).toList();
                logSystemService.delArray(idList);
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 导出Excel
     * @param key 关键字
     * @param token 用户令牌
     */
    @GetMapping("/exportExcel")
    public void exportExcel(@RequestParam(name="source", defaultValue="") String source,
                            @RequestParam(name="filed", defaultValue="") String filed,
                            @RequestParam(name="key", defaultValue="") String key,
                            @RequestParam(name="operation", defaultValue="") String operation,
                            @RequestParam(name="startDate", defaultValue="") String startDate,
                            @RequestParam(name="endDate", defaultValue="") String endDate,
                            @RequestParam(name="token", defaultValue="") String token) throws IOException {
        if(StrUtil.isNotEmpty(token)) {
            if(loginService.verifyAccessToken(token)) {
                QueryWrapper<LogSystem> example = new QueryWrapper<>();
                if(!StrUtil.isEmpty(source)) {
                    example.eq("client_source", source);
                }
                if(!StrUtil.isEmpty(filed) && !StrUtil.isEmpty(key)) {
                    switch(filed) {
                        case "ipAddress":
                            example.like("client_ip", key);
                            break;
                        case "userName":
                            example.like("user_name", key);
                            break;
                        case "nickName":
                            example.like("nick_name", key);
                            break;
                        case "modelName":
                            example.like("model_name", key);
                            break;
                    }
                }
                if(!StrUtil.isEmpty(operation)) {
                    example.eq("operation", operation);
                }
                if(!StrUtil.isEmpty(startDate) && !StrUtil.isEmpty(endDate)) {
                    Date starTime;
                    Date endTime;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        starTime = format.parse(startDate);
                        endTime = format.parse(endDate);
                        example.between("ts", starTime.getTime(), endTime.getTime());
                    } catch (ParseException ignored) {
                    }
                }
                example.orderByDesc("ts");
                List<LogSystem> list = logSystemService.selectLimit(example, 1000);

                // 设置响应头
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-disposition", "attachment;filename=" + ExcelUtil.getExcelFileName("系统日志") + ".xlsx");

                // 写入Excel
                EasyExcel.write(response.getOutputStream(), UserExp.class)
                        .useDefaultStyle(false)
                        .sheet("Sheet1")
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .doWrite(list);
            }
        }
    }
}
