package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.product.ProductModel;
import org.bem.iot.service.DeviceService;
import org.bem.iot.service.ProductModelService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备物模型
 * @author jakybland
 */
@RestController
@RequestMapping("/deviceModel")
public class DeviceModelController {
    @Resource
    ProductModelService productModelService;

    @Resource
    DeviceService deviceService;

    @Resource
    HttpServletResponse response;

    /**
     * 获取设备物模型列表
     * @param identity 物模型标识
     * @param name 物模型名称
     * @param deviceId 设备ID
     * @param modelClass 模型类别 1：属性 2：服务 3：事件
     * @param neModelClass 不等于模型类别 1：属性 2：服务 3：事件
     * @param history 是否数据存储 0：不检查 1：否 1：是
     */
    @GetMapping("/getDeviceModelList")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceModelList(@RequestParam(name="identity", defaultValue="") String identity,
                                   @RequestParam(name="name", defaultValue="") String name,
                                   @RequestParam(name="deviceId", defaultValue="") String deviceId,
                                   @RequestParam(name="modelClass", defaultValue="0") int modelClass,
                                   @RequestParam(name="neModelClass", defaultValue="0") int neModelClass,
                                   @RequestParam(name="history", defaultValue="0") int history) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(deviceId)) {
                jsonObject = ResponseUtil.getErrorJson("设备ID不能为空");
            } else if(deviceService.existsNotDeviceId(deviceId)) {
                jsonObject = ResponseUtil.getErrorJson("设备信息不存在");
            } else {
                Device device = deviceService.findMeta(deviceId);
                String productId = device.getProductId();

                QueryWrapper<ProductModel> example = new QueryWrapper<>();
                example.eq("product_id", productId);
                if(modelClass > 0) {
                    example.eq("model_class", modelClass);
                }
                if(neModelClass > 0) {
                    example.ne("model_class", neModelClass);
                }
                if(history > 0) {
                    if(history == 1) {
                        example.ne("history", 0);
                    } else {
                        example.eq("history", 1);
                    }
                }
                if(!StrUtil.isEmpty(identity)) {
                    example.like("mode_identity", identity);
                }
                if(!StrUtil.isEmpty(name)) {
                    example.like("model_name", name);
                }
                example.orderByAsc("create_time");
                List<ProductModel> list = productModelService.selectDeviceModels(example, deviceId);

                jsonObject = ResponseUtil.getSuccessJson(list);
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取设备物模型分页列表
     * @param identity 物模型标识
     * @param name 物模型名称
     * @param deviceId 设备ID
     * @param modelClass 模型类别 1：属性 2：服务 3：事件
     * @param neModelClass 不等于模型类别 1：属性 2：服务 3：事件
     * @param history 是否数据存储 0：不检查 1：否 1：是
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getDeviceModelPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceModelPageList(@RequestParam(name="identity", defaultValue="") String identity,
                                       @RequestParam(name="name", defaultValue="") String name,
                                       @RequestParam(name="deviceId", defaultValue="") String deviceId,
                                       @RequestParam(name="modelClass", defaultValue="0") int modelClass,
                                       @RequestParam(name="neModelClass", defaultValue="0") int neModelClass,
                                       @RequestParam(name="history", defaultValue="0") int history,
                                       @RequestParam(name="index", defaultValue = "1") Integer index,
                                       @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(deviceId)) {
                jsonObject = ResponseUtil.getErrorJson("设备ID不能为空");
            } else if(deviceService.existsNotDeviceId(deviceId)) {
                jsonObject = ResponseUtil.getErrorJson("设备信息不存在");
            } else {
                Device device = deviceService.findMeta(deviceId);
                String productId = device.getProductId();

                QueryWrapper<ProductModel> example = new QueryWrapper<>();
                example.eq("product_id", productId);
                if (modelClass > 0) {
                    example.eq("model_class", modelClass);
                }
                if (neModelClass > 0) {
                    example.ne("model_class", neModelClass);
                }
                if (history > 0) {
                    if (history == 1) {
                        example.ne("history", 0);
                    } else {
                        example.eq("history", 1);
                    }
                }
                if (!StrUtil.isEmpty(identity)) {
                    example.like("mode_identity", identity);
                }
                if (!StrUtil.isEmpty(name)) {
                    example.like("model_name", name);
                }
                example.orderByAsc("create_time");
                IPage<ProductModel> page = productModelService.selectDeviceModelsPage(example, deviceId, index, size);

                jsonObject = ResponseUtil.getSuccessJson(page);
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取设备物模型
     * @param deviceId 设备ID
     * @param identity 物模型标识
     */
    @GetMapping("/getDeviceModel")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceModel(@RequestParam(name="deviceId", defaultValue="") String deviceId, @RequestParam(name="identity", defaultValue="") String identity) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(deviceId)) {
            jsonObject = ResponseUtil.getErrorJson("设备ID不能为空");
        } else if(StrUtil.isEmpty(identity)) {
            jsonObject = ResponseUtil.getErrorJson("物模型标识不能为空");
        } else if(deviceService.existsNotDeviceId(deviceId)) {
            jsonObject = ResponseUtil.getErrorJson("设备信息不存在");
        } else {
            ProductModel data = productModelService.findDeviceModel(deviceId, identity);
            if(data == null) {
                jsonObject = ResponseUtil.getErrorJson("设备物模型信息不存在");
            } else {
                Device device = deviceService.findMeta(deviceId);

                JSONObject obj = new JSONObject();
                obj.put("device", device);
                obj.put("model", data);
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
