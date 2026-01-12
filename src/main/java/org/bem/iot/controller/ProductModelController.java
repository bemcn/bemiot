package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.product.ProductModel;
import org.bem.iot.service.ProductModelService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 产品物模型
 * @author jakybland
 */
@RestController
@RequestMapping("/productModel")
public class ProductModelController {
    @Resource
    ProductModelService productModelService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取产品物模型列表
     * @param identity 物模型标识
     * @param name 物模型名称
     * @param productId 产品ID
     * @param modelClass 模型类别 1：属性 2：服务 3：事件 4：标签
     */
    @GetMapping("/getProductModelList")
    @PublicHeadLimit
    @ResponseBody
    public void getProductModelList(@RequestParam(name="productId", defaultValue="") String productId,
                                    @RequestParam(name="identity", defaultValue="") String identity,
                                    @RequestParam(name="name", defaultValue="") String name,
                                    @RequestParam(name="modelClass", defaultValue="0") int modelClass) {
        JSONObject jsonObject;
        if(StrUtil.isNotEmpty(productId)) {
            try {
                QueryWrapper<ProductModel> example = new QueryWrapper<>();
                example.eq("product_id", productId);
                if (modelClass > 0) {
                    example.eq("model_class", modelClass);
                }
                if (!StrUtil.isEmpty(identity)) {
                    example.like("model_identity", identity);
                }
                if (!StrUtil.isEmpty(name)) {
                    example.like("model_name", name);
                }
                example.orderByDesc("create_time");
                List<ProductModel> list = productModelService.select(example);

                jsonObject = ResponseUtil.getSuccessJson(list);
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        } else {
            jsonObject = ResponseUtil.getErrorJson("产品ID不能为空或小于1");
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取物模型
     * @param modelId 物模型ID
     */
    @GetMapping("/getProductModels")
    @PublicHeadLimit
    @ResponseBody
    public void getProductModels(@RequestParam(name="modelId", defaultValue="") String modelId) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(modelId)) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空");
        } else if(productModelService.existsNotModelId(modelId)) {
            jsonObject = ResponseUtil.getErrorJson("物模型不存在");
        } else {
            ProductModel data = productModelService.find(modelId);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增物模型
     * @param record 物模型
     */
    @PostMapping("/addProductModels")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addProductModels(@Validated(Add.class) @Valid ProductModel record) {
        JSONObject jsonObject;
        try {
            if(productModelService.existsModelIdentity(record.getProductId(), record.getModelIdentity())) {
                jsonObject = ResponseUtil.getErrorJson("物模型标识[" + record.getModelIdentity() + "]在产品中已存在");
            } else {
                String dataDefinition = record.getDataDefinition();
                dataDefinition = URLDecoder.decode(dataDefinition, StandardCharsets.UTF_8);
                record.setDataDefinition(dataDefinition);
                productModelService.insert(record);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "物模型", "新增", "新增物模型，【名称】" + record.getModelName());

                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑物模型
     * @param record 物模型
     */
    @PostMapping("/editProductModels")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editProductModels(@Validated(Edit.class) @Valid ProductModel record) {
        JSONObject jsonObject;
        try {
            String dataDefinition = record.getDataDefinition();
            dataDefinition = URLDecoder.decode(dataDefinition, StandardCharsets.UTF_8);
            record.setDataDefinition(dataDefinition);
            productModelService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "物模型", "编辑", "编辑物模型，【名称】" + record.getModelName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除产品物模型
     * @param id id
     */
    @GetMapping("/delProductModel")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delProductModel(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        try {
            ProductModel record = productModelService.find(id);
            String modelName = record.getModelName();
            int count = productModelService.del(id);
            if(count > 0) {
                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "物模型", "删除", "删除物模型，【名称】" + modelName);
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个产品物模型
     * @param ids id集合
     */
    @GetMapping("/delProductModels")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delProductModels(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<String> idList = Arrays.stream(ids.split(",")).toList();
                int count = productModelService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "物模型", "删除", "批量删除" + count + "条物模型信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
