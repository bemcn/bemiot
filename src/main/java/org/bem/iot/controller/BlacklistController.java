package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.entity.BlacklistImp;
import org.bem.iot.listener.BlacklistListener;
import org.bem.iot.model.system.Blacklist;
import org.bem.iot.service.BlacklistService;
import org.bem.iot.service.LoginService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ExcelUtil;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 黑名单
 * @author jakybland
 */
@RestController
@RequestMapping("/blacklist")
public class BlacklistController {
    @Resource
    BlacklistService blacklistService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    LoginService loginService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取黑名单列表
     * @param key 关键字
     */
    @GetMapping("/getBlacklistList")
    @PublicHeadLimit
    @ResponseBody
    public void getBlacklistList(@RequestParam(name="key", defaultValue="") String key) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Blacklist> example = createExample(key);
            List<Blacklist> list = blacklistService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取黑名单分页列表
     * @param key 关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getBlacklistPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getBlacklistPageList(@RequestParam(name="key", defaultValue="") String key,
                                @RequestParam(name="index", defaultValue = "1") Integer index,
                                @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Blacklist> example = createExample(key);
            IPage<Blacklist> page = blacklistService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    private QueryWrapper<Blacklist> createExample(String key) {
        QueryWrapper<Blacklist> example = new QueryWrapper<>();
        if(!StrUtil.isEmpty(key)) {
            example.like("ip_start", key).or().like("ip_end", key);
        }
        example.orderByDesc("create_time");
        return example;
    }

    /**
     * 获取黑名单
     * @param id id
     */
    @GetMapping("/getBlacklist")
    @PublicHeadLimit
    @ResponseBody
    public void getBlacklist(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(blacklistService.existsNotBlackId(id)) {
            jsonObject = ResponseUtil.getErrorJson("黑名单信息不存在");
        } else {
            Blacklist data = blacklistService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增黑名单
     * @param record 黑名单
     */
    @PostMapping("/addBlacklist")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addBlacklist(@Validated(Add.class) @Valid Blacklist record) {
        JSONObject jsonObject;
        try {
            Date createTime = new Date();
            String ipAddress = record.getIpStart();
            if(StrUtil.isEmpty(record.getIpEnd())) {
                ipAddress += "-" + record.getIpEnd();
            }
            record.setCreateTime(createTime);
            blacklistService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "黑名单管理", "新增", "新增黑名单信息，【IP地址】" + ipAddress);

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑黑名单
     * @param record 黑名单
     */
    @PostMapping("/editBlacklist")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editBlacklist(@Validated(Edit.class) @Valid Blacklist record) {
        JSONObject jsonObject;
        try {
            String ipAddress = record.getIpStart();
            if(StrUtil.isEmpty(record.getIpEnd())) {
                ipAddress += "-" + record.getIpEnd();
            }
            blacklistService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "黑名单管理", "修改", "修改黑名单信息，【IP地址】" + ipAddress);

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除黑名单
     * @param id id
     */
    @GetMapping("/delBlacklist")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delBlacklist(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        try {
            if(!blacklistService.existsNotBlackId(id)) {
                Blacklist record = blacklistService.find(id);
                String ipAddress = record.getIpStart();
                if(StrUtil.isEmpty(record.getIpEnd())) {
                    ipAddress += "-" + record.getIpEnd();
                }
                int count = blacklistService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "黑名单管理", "删除", "删除黑名单信息，【IP地址】" + ipAddress);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个黑名单
     * @param ids id集合
     */
    @GetMapping("/delBlacklists")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delBlacklists(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
                int count = blacklistService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "黑名单管理", "删除", "批量删除" + count + "条黑名单信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 导入模板下载
     * @param token 黑名单令牌
     */
    @GetMapping("/modelExcel")
    public void modelExcel(@RequestParam(name="token", defaultValue="") String token) throws IOException {
        if(StrUtil.isNotEmpty(token)) {
            if(loginService.verifyAccessToken(token)) {
                String[] headArray = {"开始IP", "截止IP", "备注"};
                ExcelUtil.downloadModelExcel(headArray, "黑名单", response);
            }
        }
    }

    /**
     * excel导入数据
     * @param file 导入文件
     * @param token 黑名单令牌
     */
    @PostMapping("/importExcel")
    @ResponseBody
    public void importExcel(@RequestParam("file") MultipartFile file, @RequestParam(name="token", defaultValue="") String token) {
        JSONObject jsonObject;
        if(StrUtil.isNotEmpty(token)) {
            if(loginService.verifyAccessToken(token)) {
                JSONObject verifyObj = ExcelUtil.verifyImportExcel(file);
                boolean verify = verifyObj.getBoolean("state");
                if(!verify) {
                    jsonObject = ResponseUtil.getErrorJson(2000, verifyObj.getString("message"));
                } else {
                    String suffix = verifyObj.getString("suffix");

                    try {
                        BlacklistListener userListener = new BlacklistListener();
                        EasyExcel.read(file.getInputStream(), BlacklistImp.class, userListener).sheet().doRead();
                        List<BlacklistImp> list = userListener.list;

                        if(!list.isEmpty()) {
                            //验证是否有带必填参数的数据，如果有，表示格式正确
                            boolean isFormat = false;
                            for (BlacklistImp blacklist : list) {
                                if (StrUtil.isNotEmpty(blacklist.getIpStart())) {
                                    isFormat = true;
                                    break;
                                }
                            }

                            if (isFormat) {
                                List<BlacklistImp> errorList = new ArrayList<>();  // 错误列表
                                boolean status = true;  // 状态
                                int successCount = 0;  // 成功数量
                                int errorCount = 0;  // 错误数量
                                for (BlacklistImp blacklist : list) {
                                    if (StrUtil.isEmpty(blacklist.getIpStart())) {
                                        status = false;
                                        errorCount++;
                                        errorList.add(blacklist);
                                    } else {
                                        boolean isInsert = blacklistService.insertImport(blacklist);
                                        if (isInsert) {
                                            successCount++;
                                        } else {
                                            status = false;
                                            errorCount++;
                                            errorList.add(blacklist);
                                        }
                                    }
                                }
                                JSONObject resultJson = new JSONObject();
                                if (status) {
                                    if(successCount > 0) {
                                        resultJson.put("successCount", successCount);
                                        jsonObject = ResponseUtil.getSuccessJson(resultJson);
                                    } else {
                                        jsonObject = ResponseUtil.getErrorJson(2000, "Excel中没有数据内容");
                                    }
                                } else {
                                    resultJson = ExcelUtil.importInitExcel("黑名单", suffix, successCount, errorCount, BlacklistImp.class, errorList);
                                    jsonObject = ResponseUtil.getErrorJson(3000, "导入数据失败", resultJson);
                                }
                            } else {
                                jsonObject = ResponseUtil.getErrorJson(2000, "待导入数据的格式错误，缺少必填项");
                            }
                        } else {
                            jsonObject = ResponseUtil.getErrorJson(2000, "Excel中没有数据内容");
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        jsonObject = ResponseUtil.getErrorJson(2000, "导入数据文件错误");
                    }
                }
            } else {
                jsonObject = ResponseUtil.getErrorJson(2000, "无权限的请求");
            }
        } else {
            jsonObject = ResponseUtil.getErrorJson(2000, "无权限的请求");
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 导出Excel
     * @param key 关键字
     * @param token 黑名单令牌
     */
    @GetMapping("/exportExcel")
    public void exportExcel(@RequestParam(name="key", defaultValue="") String key,
                            @RequestParam(name="token", defaultValue="") String token) throws IOException {
        if(StrUtil.isNotEmpty(token)) {
            if(loginService.verifyAccessToken(token)) {
                QueryWrapper<Blacklist> example = createExample(key);
                List<Blacklist> list = blacklistService.select(example);

                // 设置响应头
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-disposition", "attachment;filename=" + ExcelUtil.getExcelFileName("黑名单管理") + ".xlsx");

                // 写入Excel
                EasyExcel.write(response.getOutputStream(), Blacklist.class)
                        .useDefaultStyle(false)
                        .sheet("Sheet1")
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .doWrite(list);
            }
        }
    }
}
