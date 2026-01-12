package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.entity.ProductClassImp;
import org.bem.iot.listener.ProductClassListener;
import org.bem.iot.model.product.ProductClass;
import org.bem.iot.service.LoginService;
import org.bem.iot.service.ProductClassService;
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
import java.util.List;

/**
 * 产品分类
 * @author jakybland
 */
@RestController
@RequestMapping("/productClass")
public class ProductClassController {
    @Resource
    ProductClassService productClassService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    LoginService loginService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取产品分类树
     */
    @GetMapping("/getProductClassTree")
    @PublicHeadLimit
    @ResponseBody
    public void getProductClassTree() {
        JSONObject jsonObject;
        try {
            JSONArray list = productClassService.selectTree();
            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取产品分类树列表
     */
    @GetMapping("/getProductClassTreeTable")
    @PublicHeadLimit
    @ResponseBody
    public void getProductClassTreeTable() {
        JSONObject jsonObject;
        try {
            JSONArray list = productClassService.selectTreeTable();
            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取产品分类
     * @param id id
     */
    @GetMapping("/getProductClass")
    @PublicHeadLimit
    @ResponseBody
    public void getProductClass(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(productClassService.existsNotClassId(id)) {
            jsonObject = ResponseUtil.getErrorJson("产品分类不存在");
        } else {
            ProductClass data = productClassService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增产品分类
     * @param record 产品分类
     */
    @PostMapping("/addProductClass")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addProductClass(@Validated(Add.class) @Valid ProductClass record) {
        JSONObject jsonObject;
        try {
            if(productClassService.existsClassName(record.getLevelId(), record.getClassName())) {
                jsonObject = ResponseUtil.getErrorJson("分类名称在同级中已存在");
            } else {
                productClassService.insert(record);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "产品分类", "新增", "新增产品分类，【名称】" + record.getClassName());

                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑产品分类
     * @param record 产品分类
     */
    @PostMapping("/editProductClass")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editProductClass(@Validated(Edit.class) @Valid ProductClass record) {
        JSONObject jsonObject;
        try {
            if(productClassService.existsClassName(record.getClassId(), record.getLevelId(), record.getClassName())) {
                jsonObject = ResponseUtil.getErrorJson("分类名称在同级中已存在");
            } else {
                productClassService.update(record);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "产品分类", "修改", "修改产品分类，【名称】" + record.getClassName());

                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 修改排序
     * @param id id
     * @param orderNum 排序值
     */
    @GetMapping("/updateOrderNumber")
    @PublicHeadLimit("user")
    @ResponseBody
    public void updateOrderNumber(@RequestParam(name="id", defaultValue="0") int id, @RequestParam(name="orderNum", defaultValue="0") int orderNum) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(productClassService.existsNotClassId(id)) {
            jsonObject = ResponseUtil.getErrorJson("产品分类不存在");
        } else {
            try {
                ProductClass productClass = productClassService.find(id);
                String className = productClass.getClassName();
                productClassService.updateOrder(id, orderNum);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "产品分类", "修改", "修改产品排序为" + orderNum + "，【名称】" + className);

                jsonObject = ResponseUtil.getSuccessJson();
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除产品分类
     * @param id id
     */
    @GetMapping("/delProductClass")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delProductClass(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        try {
            if(!productClassService.existsNotClassId(id)) {
                ProductClass productClass = productClassService.find(id);
                String className = productClass.getClassName();
                int count = productClassService.del(id);
                if (count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "产品分类", "删除", "删除产品分类，【名称】" + className);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 导入模板下载
     * @param token 用户令牌
     */
    @GetMapping("/modelExcel")
    public void modelExcel(@RequestParam(name="token", defaultValue="") String token) throws IOException {
        if(StrUtil.isNotEmpty(token)) {
            if(loginService.verifyAccessToken(token)) {
                String[] headArray = {"分类名称", "上级分类", "备注"};
                ExcelUtil.downloadModelExcel(headArray, "产品分类", response);
            }
        }
    }

    /**
     * excel导入数据
     * @param file 导入文件
     * @param token 用户令牌
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
                        ProductClassListener classListener = new ProductClassListener();
                        EasyExcel.read(file.getInputStream(), ProductClassImp.class, classListener).sheet().doRead();
                        List<ProductClassImp> list = classListener.list;

                        if(!list.isEmpty()) {
                            //验证是否有带必填参数的数据，如果有，表示格式正确
                            boolean isFormat = false;
                            for (ProductClassImp productClass : list) {
                                if (StrUtil.isNotEmpty(productClass.getClassName())) {
                                    isFormat = true;
                                    break;
                                }
                            }

                            if (isFormat) {
                                List<ProductClassImp> errorList = new ArrayList<>();  // 错误列表
                                boolean status = true;  // 状态
                                int successCount = 0;  // 成功数量
                                int errorCount = 0;  // 错误数量
                                for (ProductClassImp productClass : list) {
                                    if (StrUtil.isEmpty(productClass.getClassName())) {
                                        status = false;
                                        errorCount++;
                                        errorList.add(productClass);
                                    } else {
                                        boolean isInsert = productClassService.insertImport(productClass);
                                        if (isInsert) {
                                            successCount++;
                                        } else {
                                            status = false;
                                            errorCount++;
                                            errorList.add(productClass);
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
                                    resultJson = ExcelUtil.importInitExcel("产品分类", suffix, successCount, errorCount, ProductClassImp.class, errorList);
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
     * @param token 用户令牌
     */
    @GetMapping("/exportExcel")
    public void exportExcel(@RequestParam(name="token", defaultValue="") String token) throws IOException {
        if(StrUtil.isNotEmpty(token)) {
            if(loginService.verifyAccessToken(token)) {
                List<ProductClass> list = productClassService.select();

                // 设置响应头
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-disposition", "attachment;filename=" + ExcelUtil.getExcelFileName("产品分类") + ".xlsx");

                // 写入Excel
                EasyExcel.write(response.getOutputStream(), ProductClass.class)
                        .useDefaultStyle(false)
                        .sheet("Sheet1")
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .doWrite(list);
            }
        }
    }
}
