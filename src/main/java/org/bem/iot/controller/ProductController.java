package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.product.ProductClass;
import org.bem.iot.service.DeviceService;
import org.bem.iot.service.ProductClassService;
import org.bem.iot.service.ProductService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 产品管理
 * @author jakybland
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    @Resource
    ProductService productService;

    @Resource
    ProductClassService productClassService;

    @Resource
    DeviceService deviceService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取产品列表
     * @param classId 分类ID
     * @param types 产品类型 0:不区分 1：直连设备 2：网关设备 3：监控设备 4：视频存储设备 5：网关子设备 6：虚拟设备
     * @param netMethod 联网方式 1：以太网 2：Wifi 3：蜂窝 4：NB-IOT 5：串口通讯 6：其他
     * @param status 状态 1：未发布，2：已发布
     * @param driveCode 驱动编号
     * @param filed 查询字段 name/models/manufacturer/supplier
     * @param key 关键字
     * @param startDate 开始日期
     * @param endDate 截止日期
     */
    @GetMapping("/getProductList")
    @PublicHeadLimit
    @ResponseBody
    public void getProductList(@RequestParam(name="classId", defaultValue="0") int classId,
                               @RequestParam(name="types", defaultValue="0") int types,
                               @RequestParam(name="netMethod", defaultValue="0") int netMethod,
                               @RequestParam(name="status", defaultValue="0") int status,
                               @RequestParam(name="drive", defaultValue="") String driveCode,
                               @RequestParam(name="filed", defaultValue="") String filed,
                               @RequestParam(name="key", defaultValue="") String key,
                               @RequestParam(name="startDate", defaultValue="") String startDate,
                               @RequestParam(name="endDate", defaultValue="") String endDate) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Product> example = createExample(classId, types, netMethod, status, driveCode, filed, key, startDate, endDate);
            List<Product> list = productService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取产品分页列表
     * @param classId 分类ID
     * @param types 产品类型 0:不区分 1：直连设备 2：网关设备 3：监控设备 4：视频存储设备 5：网关子设备 6：虚拟设备
     * @param netMethod 联网方式 1：以太网 2：Wifi 3：蜂窝 4：NB-IOT 5：串口通讯 6：其他
     * @param status 状态 1：未发布，2：已发布
     * @param driveCode 驱动编号
     * @param filed 查询字段 name/models/manufacturer/supplier
     * @param key 关键字
     * @param startDate 开始日期
     * @param endDate 截止日期
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getProductPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getProductPageList(@RequestParam(name="classId", defaultValue="0") int classId,
                                   @RequestParam(name="types", defaultValue="0") int types,
                                   @RequestParam(name="netMethod", defaultValue="0") int netMethod,
                                   @RequestParam(name="status", defaultValue="0") int status,
                                   @RequestParam(name="drive", defaultValue="") String driveCode,
                                   @RequestParam(name="filed", defaultValue="") String filed,
                                   @RequestParam(name="key", defaultValue="") String key,
                                   @RequestParam(name="startDate", defaultValue="") String startDate,
                                   @RequestParam(name="endDate", defaultValue="") String endDate,
                                   @RequestParam(name="index", defaultValue = "1") Integer index,
                                   @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Product> example = createExample(classId, types, netMethod, status, driveCode, filed, key, startDate, endDate);
            IPage<Product> page = productService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    private QueryWrapper<Product> createExample(int classId, int types, int netMethod, int status, String driveCode, String filed,
                                                String key, String startDate, String endDate) {
        QueryWrapper<Product> example = new QueryWrapper<>();
        if(classId > 0) {
            example.eq("class_id", classId);
        }
        if(types > 0) {
            example.eq("types", types);
        }
        if(netMethod > 0) {
            example.eq("net_method", netMethod);
        }
        if(status > 0) {
            example.eq("status", status);
        }
        if(!StrUtil.isEmpty(driveCode)) {
            example.like("drive_code", driveCode);
        }
        if(!StrUtil.isEmpty(filed) && !StrUtil.isEmpty(key)) {
            switch (filed) {
                case "name":
                    example.like("product_name", key);
                    break;
                case "models":
                    example.like("models", key);
                    break;
                case "manufacturer":
                    example.like("manufacturer", key);
                    break;
                case "supplier":
                    example.like("supplier", key);
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
                example.between("create_time", starTime, endTime);
            } catch (ParseException ignored) {
            }
        }
        example.orderByDesc("create_time");
        return example;
    }

    /**
     * 获取产品树
     */
    @GetMapping("/getProductTree")
    @PublicHeadLimit
    @ResponseBody
    public void getProductTree() {
        JSONObject jsonObject;
        try {
            JSONArray array = productService.selectProductTree();

            jsonObject = ResponseUtil.getSuccessJson(array);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取产品
     * @param id id
     */
    @GetMapping("/getProduct")
    @PublicHeadLimit
    @ResponseBody
    public void getProduct(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(id)) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空");
        } else if(productService.existsNotProductId(id)) {
            jsonObject = ResponseUtil.getErrorJson("产品信息不存在");
        } else {
            Product data = productService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增产品
     * @param record 产品
     */
    @PostMapping("/addProduct")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addProduct(@Validated(Add.class) @Valid Product record) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(record.getProductId())) {
                jsonObject = ResponseUtil.getErrorJson("id不能为空");
            } else if(!productService.existsNotProductId(record.getProductId().toLowerCase())) {
                jsonObject = ResponseUtil.getErrorJson("产品id已被使用");
            } else {
                String productId = record.getProductId().toLowerCase();
                record.setProductId(productId);
                productService.insert(record);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "产品信息", "新增", "新增产品，【名称】" + record.getProductName());
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑产品
     * @param record 产品
     */
    @PostMapping("/editProduct")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editProduct(@Validated(Edit.class) @Valid Product record) {
        JSONObject jsonObject;
        try {
            productService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "产品信息", "修改", "修改产品，【名称】" + record.getProductName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑产品状态
     * @param id 产品ID
     * @param status 状态 1-待发布 2-已发布
     */
    @GetMapping("/editProductStatus")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editProductStatus(@RequestParam(name="id", defaultValue="") String id, @RequestParam(name="status", defaultValue="0") Integer status) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(id)) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if (status < 1 || status > 2) {
            jsonObject = ResponseUtil.getErrorJson("状态值错误");
        } else if (productService.existsNotProductId(id)) {
            jsonObject = ResponseUtil.getErrorJson("产品信息不存在");
        } else {


            try {
                Product product = productService.findMeta(id);
                product.setStatus(status);
                productService.update(product);

                String statusName = status == 2 ? "已发布" : "待发布";

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "产品信息", "修改", "修改产品为" + statusName + "状态，【名称】" + product.getProductName());
                jsonObject = ResponseUtil.getSuccessJson();
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 转移产品分类
     * @param oldClassId 待转移分类ID
     * @param newClassId 目标分类ID
     */
    @PostMapping("/transProductClass")
    @PublicHeadLimit("user")
    @ResponseBody
    public void transProductClass(@RequestParam(name="oldClassId", defaultValue="0") int oldClassId,
                                  @RequestParam(name="newClassId", defaultValue="0") int newClassId) {
        JSONObject jsonObject;
        try {
            if(productClassService.existsNotClassId(oldClassId)) {
                jsonObject = ResponseUtil.getErrorJson("待转移分类信息不存在");
            } else if(productClassService.existsNotClassId(newClassId)) {
                jsonObject = ResponseUtil.getErrorJson("目标分类信息不存在");
            } else {
                ProductClass oldClass = productClassService.find(oldClassId);
                ProductClass newClass = productClassService.find(newClassId);
                String oldClassName = oldClass.getClassName();
                String newClassName = newClass.getClassName();
                productService.transferClass(oldClassId, newClassId);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "产品信息", "修改", "批量将分类[" + oldClassName + "]下的产品转移到分类[" + newClassName + "]");

                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除产品
     * @param id id
     */
    @GetMapping("/delProduct")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delProduct(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        try {
            if(!productService.existsNotProductId(id)) {
                Product product = productService.findMeta(id);
                String productName = product.getProductName();
                int count = productService.del(id);
                if (count > 0) {
                    deviceService.delByProductId(id);

                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "产品信息", "删除", "删除产品，【名称】" + productName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个产品
     * @param ids id集合
     */
    @GetMapping("/delProducts")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delProducts(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<String> idList = Arrays.stream(ids.split(",")).toList();
                int count = productService.delArray(idList);
                if(count > 0) {
                    deviceService.delByProductIdArray(idList);

                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "产品信息", "删除", "批量删除" + count + "条产品信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
