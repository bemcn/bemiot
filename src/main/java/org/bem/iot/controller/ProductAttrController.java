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
import org.bem.iot.model.product.ProductAttr;
import org.bem.iot.service.ProductAttrService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 档案属性管理
 * @author jakybland
 */
@RestController
@RequestMapping("/productAttr")
public class ProductAttrController {
    @Resource
    ProductAttrService productAttrService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取档案属性列表
     * @param classId 分类ID
     * @param productId 产品ID
     * @param key 关键字
     */
    @GetMapping("/getProductAttrList")
    @PublicHeadLimit
    @ResponseBody
    public void getProductAttrList(@RequestParam(name="classId", defaultValue="0") int classId,
                                   @RequestParam(name="productId", defaultValue="") String productId,
                                   @RequestParam(name="key", defaultValue="") String key) {
        JSONObject jsonObject;
        try {
            QueryWrapper<ProductAttr> example = new QueryWrapper<>();
            if(classId > 0) {
                String classKey = ":" + classId + ":";
                example.like("class_route", classKey);
            }
            if(StrUtil.isEmpty(productId)) {
                example.eq("product_id", productId);
            }
            if(!StrUtil.isEmpty(key)) {
                example.like("field_label", key);
            }
            example.orderByAsc("attr_id");
            List<ProductAttr> list = productAttrService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取档案属性分页列表
     * @param classId 分类ID
     * @param productId 产品ID
     * @param key 关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getProductAttrPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getProductAttrPageList(@RequestParam(name="classId", defaultValue="0") int classId,
                                       @RequestParam(name="productId", defaultValue="") String productId,
                                       @RequestParam(name="key", defaultValue="") String key,
                                       @RequestParam(name="index", defaultValue = "1") Integer index,
                                       @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<ProductAttr> example = new QueryWrapper<>();
            if(classId > 0) {
                String classKey = ":" + classId + ":";
                example.like("class_route", classKey);
            }
            if(StrUtil.isNotEmpty(productId)) {
                example.eq("product_id", productId);
            }
            if(!StrUtil.isEmpty(key)) {
                example.like("field_label", key);
            }
            example.orderByAsc("attr_id");
            IPage<ProductAttr> page = productAttrService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取档案属性
     * @param id id
     */
    @GetMapping("/getProductAttr")
    @PublicHeadLimit
    @ResponseBody
    public void getProductAttr(@RequestParam(name="id", defaultValue="0") long id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(productAttrService.existsNotAttrId(id)) {
            jsonObject = ResponseUtil.getErrorJson("档案属性信息不存在");
        } else {
            ProductAttr data = productAttrService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增档案属性
     * @param record 档案属性
     */
    @PostMapping("/addProductAttr")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addProductAttr(@Validated(Add.class) @Valid ProductAttr record) {
        JSONObject jsonObject;
        try {
            productAttrService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "档案属性", "新增", "新增档案属性，【字段名称】" + record.getFieldKey());
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑档案属性
     * @param record 档案属性
     */
    @PostMapping("/editProductAttr")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editProductAttr(@Validated(Edit.class) @Valid ProductAttr record) {
        JSONObject jsonObject;
        try {
            productAttrService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "档案属性", "修改", "修改档案属性，【字段名称】" + record.getFieldKey());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除档案属性
     * @param id id
     */
    @GetMapping("/delProductAttr")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delProductAttr(@RequestParam(name="id", defaultValue="") long id) {
        JSONObject jsonObject;
        try {
            if(!productAttrService.existsNotAttrId(id)) {
                ProductAttr productAttr = productAttrService.find(id);
                String fieldKey = productAttr.getFieldKey();
                int count = productAttrService.del(id);
                if (count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "档案属性", "删除", "删除档案属性，【字段名称】" + fieldKey);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个档案属性
     * @param ids id集合
     */
    @GetMapping("/delProductAttrs")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delProductAttrs(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).toList();
                int count = productAttrService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "档案属性", "删除", "批量删除" + count + "条档案属性信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}