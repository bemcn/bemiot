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
import org.bem.iot.entity.AddProductAuthCode;
import org.bem.iot.entity.BindProductAuthCode;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.product.ProductAuthCode;
import org.bem.iot.service.ProductAuthCodeService;
import org.bem.iot.service.ProductService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 产品授权码
 * @author jakybland
 */
@RestController
@RequestMapping("/productAuthCode")
public class ProductAuthCodeController {
    @Resource
    ProductAuthCodeService productAuthCodeService;

    @Resource
    ProductService productService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取产品授权码分页列表
     * @param status 状态 1：未使用 2：使用中
     * @param productId 产品ID
     * @param deviceId 设备ID
     * @param userId 用户ID
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getProductAuthCodePageList")
    @PublicHeadLimit
    @ResponseBody
    public void getProductAuthCodePageList(@RequestParam(name="status", defaultValue="") int status,
                                           @RequestParam(name="productId", defaultValue="") String productId,
                                           @RequestParam(name="deviceId", defaultValue="") String deviceId,
                                           @RequestParam(name="userId", defaultValue="0") int userId,
                                           @RequestParam(name="index", defaultValue = "1") Integer index,
                                           @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<ProductAuthCode> example = new QueryWrapper<>();
            if(status > 0) {
                example.eq("status", status);
            }
            if(StrUtil.isNotEmpty(productId)) {
                example.eq("product_id", productId);
            }
            if(StrUtil.isNotEmpty(deviceId)) {
                example.eq("device_id", deviceId);
            }
            if(userId > 0) {
                example.eq("user_id", userId);
            }
            IPage<ProductAuthCode> page = productAuthCodeService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取未绑定授权码设备分页列表
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getNotBindDevicePageList")
    @PublicHeadLimit
    @ResponseBody
    public void getNotBindDevicePageList(@RequestParam(name="index", defaultValue = "1") Integer index,
                                         @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            IPage<Device> page = productAuthCodeService.selectNotBindDevicePageList(index, size);
            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取产品授权码
     * @param code 授权码
     */
    @GetMapping("/getProductAuthCode")
    @PublicHeadLimit
    @ResponseBody
    public void getProductAuthCode(@RequestParam(name="code", defaultValue="0") String code) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(code)) {
            jsonObject = ResponseUtil.getErrorJson("授权码不能为空");
        } else if(productAuthCodeService.existsNotAuthCode(code)) {
            jsonObject = ResponseUtil.getErrorJson("产品授权码信息不存在");
        } else {
            ProductAuthCode data = productAuthCodeService.find(code);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取产品授权码
     * @param deviceId 设备编号
     */
    @GetMapping("/getProductAuthCodeByDev")
    @PublicHeadLimit
    @ResponseBody
    public void getProductAuthCodeByDev(@RequestParam(name="deviceId", defaultValue="") String deviceId) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(deviceId)) {
            jsonObject = ResponseUtil.getErrorJson("设备编号不能为空");
        } else if(productAuthCodeService.existsNotAuthCodeByDev(deviceId)) {
            jsonObject = ResponseUtil.getErrorJson("产品授权码信息不存在");
        } else {
            ProductAuthCode data = productAuthCodeService.findByDev(deviceId);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 生成产品授权码
     * @param data 提交参数
     */
    @PostMapping("/createAuthCodes")
    @PublicHeadLimit("user")
    @ResponseBody
    public void createAuthCodes(@Validated @Valid AddProductAuthCode data) {
        JSONObject jsonObject;
        try {
            String productId = data.getProductId();
            int number = data.getNumber();
            Product product = productService.findMeta(productId);
            String productName = product.getProductName();
            productAuthCodeService.createNumber(productId, number);
            jsonObject = ResponseUtil.getSuccessJson();

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "产品授权码", "新增", "批量生成" + number + "个授权码，【产品名称】" + productName);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson("提交参数存在错误");
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 授权码绑定用户
     * @param data 绑定信息
     */
    @PostMapping("/bindUser")
    @PublicHeadLimit("user")
    @ResponseBody
    public void bindUser(@Validated @Valid BindProductAuthCode data) {
        JSONObject jsonObject;
        try {
            String authCode = data.getAuthCode();
            String deviceId = data.getDeviceId();
            int userId = data.getUserId();
            String remark = data.getRemark();

            productAuthCodeService.bindAuthCode(authCode, deviceId, userId, remark);
            jsonObject = ResponseUtil.getSuccessJson();

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "产品授权码", "编辑", "绑定设备和用户，【授权码】" + authCode);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson("提交参数存在错误");
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 移除授权码绑定用户
     * @param code 授权码
     */
    @GetMapping("/cancelBindUser")
    @PublicHeadLimit("user")
    @ResponseBody
    public void cancelBindUser(@RequestParam(name="code", defaultValue="") String code) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(code)) {
            jsonObject = ResponseUtil.getErrorJson("授权码不能为空");
        } else if(productAuthCodeService.existsNotAuthCode(code)) {
            jsonObject = ResponseUtil.getErrorJson("产品授权码信息不存在");
        } else {
            try {
                productAuthCodeService.cancelBindUser(code);
                jsonObject = ResponseUtil.getSuccessJson();

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "产品授权码", "编辑", "移除当前绑定的用户，【授权码】" + code);
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson("提交参数存在错误");
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 解除授权码绑定
     * @param code 授权码
     */
    @GetMapping("/cancelBindUse")
    @PublicHeadLimit("user")
    @ResponseBody
    public void cancelBind(@RequestParam(name="code", defaultValue="") String code) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(code)) {
            jsonObject = ResponseUtil.getErrorJson("授权码不能为空");
        } else if(productAuthCodeService.existsNotAuthCode(code)) {
            jsonObject = ResponseUtil.getErrorJson("产品授权码信息不存在");
        } else {
            try {
                productAuthCodeService.cancelBind(code);
                jsonObject = ResponseUtil.getSuccessJson();

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "产品授权码", "编辑", "解除授权码绑定，【授权码】" + code);
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson("提交参数存在错误");
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除产品授权码
     * @param code 授权码
     */
    @GetMapping("/delProductAuthCode")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delProductAuthCode(@RequestParam(name="code", defaultValue="") String code) {
        JSONObject jsonObject;
        try {
            if(!productAuthCodeService.existsNotAuthCode(code)) {
                int count = productAuthCodeService.del(code);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "产品授权码", "删除", "删除产品授权码，【授权码】" + code);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个产品授权码
     * @param codes 授权码集合
     */
    @GetMapping("/delProductAuthCodes")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delProductAuthCodes(@RequestParam(name="codes", defaultValue="") String codes) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(codes)) {
                jsonObject = ResponseUtil.getErrorJson("授权码集合不能为空");
            } else {
                List<String> codeList = Arrays.stream(codes.split(",")).toList();
                int count = productAuthCodeService.delArray(codeList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "产品授权码", "删除", "批量删除" + count + "条产品授权码信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
