package org.bem.iot.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.entity.BaseFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelUtil {
    /**
     * 生成Excel导出文件名
     * @param name 名称
     */
    public static String getExcelFileName(String name) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String strDate = sdf.format(date);
        return URLEncoder.encode(name + "-" + strDate, StandardCharsets.UTF_8);
    }

    /**
     * 下载Excel模板
     * @param headArray 表头名称数组
     * @param excelName Excel文件名
     * @param response 响应
     */
    public static void downloadModelExcel(String[] headArray, String excelName, HttpServletResponse response) throws IOException {
        java.util.List<java.util.List<String>> head = new ArrayList<>();
        for (String headName : headArray) {
            java.util.List<String> itemList = new ArrayList<>();
            itemList.add(headName);
            head.add(itemList);
        }
        List<List<Object>> data = new ArrayList<>();

        // 设置响应头
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(excelName + "-导入模板", StandardCharsets.UTF_8) + ".xlsx");

        // 写入Excel
        EasyExcel.write(response.getOutputStream())
                .useDefaultStyle(false)
                .head(head)
                .sheet("Sheet1")
                .doWrite(data);
    }

    /**
     * 验证导入Excel文件
     * @param file 文件
     */
    public static JSONObject verifyImportExcel(MultipartFile file) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("state", false);
        if (file == null) {
            jsonObject.put("message", "无法获取到上传文件");
        } else {
            String filename = file.getOriginalFilename();
            if (filename == null) {
                jsonObject.put("message", "上传文件信息错误");
            } else {
                String suffix = filename.substring(filename.lastIndexOf(".")).toLowerCase();
                if (!".xlsx".equals(suffix) && !".xls".equals(suffix)){
                    jsonObject.put("message", "只能上传xls或者xlsx格式的文件");
                } else {
                    long maxSize = 2 * 1024 * 1024;
                    long size = file.getSize();
                    if (size > maxSize) {
                        jsonObject.put("message", "上传文件大小不能超过2MB");
                    } else {
                        jsonObject.put("state", true);
                        jsonObject.put("suffix", suffix);
                    }
                }
            }
        }
        return jsonObject;
    }

    /**
     * 输出导入结果
     * @param excelName Excel文件名
     * @param suffix 文件后缀
     * @param successCount 成功数量
     * @param errorCount 错误数量
     * @param classType 模型数组
     * @param errorList 错误列表
     * @return JSONObject
     * @param <T> 泛型
     */
    public static <T> JSONObject importInitExcel(String excelName, String suffix, int successCount, int errorCount, Class<T> classType, List<T> errorList) {
        // 初始化文件存储信息，删除1天前文件
        BaseFile baseFile = UploadFileUtil.initByErrorFile(excelName, suffix);
        String filePath = baseFile.getSaveUrl();
        String url = baseFile.getUrl();

        EasyExcel.write(filePath, classType)
                .useDefaultStyle(false)
                .sheet("Sheet1")
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .doWrite(errorList);

        JSONObject resultJson = new JSONObject();
        resultJson.put("successCount", successCount);
        resultJson.put("errorCount", errorCount);
        resultJson.put("url", url);
        return resultJson;
    }

}
