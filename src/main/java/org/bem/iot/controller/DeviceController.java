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
import org.bem.iot.entity.DeviceMap;
import org.bem.iot.entity.params.DeviceAddParams;
import org.bem.iot.entity.params.DeviceEditParams;
import org.bem.iot.entity.params.DeviceFirmwareVersionParams;
import org.bem.iot.entity.params.DeviceLocationParams;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.user.UserInfo;
import org.bem.iot.service.DeviceService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.service.UserInfoService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 设备管理
 * @author jakybland
 */
@RestController
@RequestMapping("/device")
public class DeviceController {
    @Resource
    DeviceService deviceService;

    @Resource
    UserInfoService userInfoService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取设备列表
     * @param types 产品类型 产品类型 0:不区分 1：直连设备 2：网关设备 3：监控设备 4：视频存储设备 5：网关子设备 6：虚拟设备
     * @param typeArray 产品类型集合
     * @param classId 分类ID
     * @param groupId 分组ID
     * @param gatewayId 父网关ID
     * @param userId 绑定用户ID
     * @param spaceId 空间位置ID
     * @param productId 产品ID
     * @param status 状态 1：未激活 2：禁用 3：在线 4：离线
     * @param filed 查询字段 code/name/models
     * @param key 关键字
     * @param startDate 开始日期
     * @param endDate 截止日期
     */
    @GetMapping("/getDeviceList")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceList(@RequestParam(name="types", defaultValue="0") int types,
                              @RequestParam(name="typeArray", defaultValue="") String typeArray,
                              @RequestParam(name="classId", defaultValue="0") int classId,
                              @RequestParam(name="groupId", defaultValue="0") int groupId,
                              @RequestParam(name="gatewayId", defaultValue="") String gatewayId,
                              @RequestParam(name="userId", defaultValue="0") int userId,
                              @RequestParam(name="spaceId", defaultValue="0") int spaceId,
                              @RequestParam(name="productId", defaultValue="") String productId,
                              @RequestParam(name="status", defaultValue="0") int status,
                              @RequestParam(name="filed", defaultValue="") String filed,
                              @RequestParam(name="key", defaultValue="") String key,
                              @RequestParam(name="startDate", defaultValue="") String startDate,
                              @RequestParam(name="endDate", defaultValue="") String endDate) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Device> example = createExample(types, typeArray, classId, groupId, gatewayId, userId, spaceId, productId, status, filed, key, startDate, endDate);
            List<Device> list = deviceService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取设备未绑定列表
     */
    @GetMapping("/getDeviceNoAssetList")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceNoAssetList(@RequestParam(name="filed", defaultValue="") String filed,
                                     @RequestParam(name="key", defaultValue="") String key) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Device> example = new QueryWrapper<>();
            example.eq("user_id", 0);
            if(!StrUtil.isEmpty(filed) && !StrUtil.isEmpty(key)) {
                if ("code".equals(filed)) {
                    example.like("device_id", key);
                } else {
                    example.like("device_name", key);
                }
            }
            example.orderByDesc("create_time");
            List<Device> list = deviceService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取指定数量设备列表
     * @param types 产品类型 1：直连设备 2：网关设备 3：监控设备 4：视频存储设备 5：网关子设备 6：虚拟设备
     * @param typeArray 产品类型集合
     * @param classId 分类ID
     * @param groupId 分组ID
     * @param gatewayId 父网关ID
     * @param userId 绑定用户ID
     * @param spaceId 空间位置ID
     * @param productId 产品ID
     * @param status 状态 1：未激活 2：禁用 3：在线 4：离线
     * @param filed 查询字段 code/name/models
     * @param key 关键字
     * @param startDate 开始日期
     * @param endDate 截止日期
     * @param size 数量
     */
    @GetMapping("/getDeviceTopList")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceTopList(@RequestParam(name="types", defaultValue="0") int types,
                                 @RequestParam(name="typeArray", defaultValue="") String typeArray,
                                 @RequestParam(name="classId", defaultValue="0") int classId,
                                 @RequestParam(name="groupId", defaultValue="0") int groupId,
                                 @RequestParam(name="gatewayId", defaultValue="") String gatewayId,
                                 @RequestParam(name="userId", defaultValue="0") int userId,
                                 @RequestParam(name="spaceId", defaultValue="0") int spaceId,
                                 @RequestParam(name="productId", defaultValue="") String productId,
                                 @RequestParam(name="status", defaultValue="0") int status,
                                 @RequestParam(name="filed", defaultValue="") String filed,
                                 @RequestParam(name="key", defaultValue="") String key,
                                 @RequestParam(name="startDate", defaultValue="") String startDate,
                                 @RequestParam(name="endDate", defaultValue="") String endDate,
                                 @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Device> example = createExample(types, typeArray, classId, groupId, gatewayId, userId, spaceId, productId, status, filed, key, startDate, endDate);
            List<Device> list = deviceService.selectTop(example, size);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取设备分页列表
     * @param types 产品类型 1：直连设备 2：网关设备 3：监控设备 4：视频存储设备 5：网关子设备 6：虚拟设备
     * @param typeArray 产品类型集合
     * @param classId 分类ID
     * @param groupId 分组ID
     * @param gatewayId 父网关ID
     * @param userId 绑定用户ID
     * @param spaceId 空间位置ID
     * @param productId 产品ID
     * @param status 状态 1：未激活 2：禁用 3：在线 4：离线
     * @param filed 查询字段 code/name/models
     * @param key 关键字
     * @param startDate 开始日期
     * @param endDate 截止日期
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getDevicePageList")
    @PublicHeadLimit
    @ResponseBody
    public void getDevicePageList(@RequestParam(name="types", defaultValue="0") int types,
                                  @RequestParam(name="typeArray", defaultValue="") String typeArray,
                                  @RequestParam(name="classId", defaultValue="0") int classId,
                                  @RequestParam(name="groupId", defaultValue="0") int groupId,
                                  @RequestParam(name="gatewayId", defaultValue="") String gatewayId,
                                  @RequestParam(name="userId", defaultValue="0") int userId,
                                  @RequestParam(name="spaceId", defaultValue="0") int spaceId,
                                  @RequestParam(name="productId", defaultValue="") String productId,
                                  @RequestParam(name="status", defaultValue="0") int status,
                                  @RequestParam(name="filed", defaultValue="") String filed,
                                  @RequestParam(name="key", defaultValue="") String key,
                                  @RequestParam(name="startDate", defaultValue="") String startDate,
                                  @RequestParam(name="endDate", defaultValue="") String endDate,
                                  @RequestParam(name="index", defaultValue = "1") Integer index,
                                  @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Device> example = createExample(types, typeArray, classId, groupId, gatewayId, userId, spaceId, productId, status, filed, key, startDate, endDate);
            IPage<Device> page = deviceService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取设备分页列表
     * @param userId 绑定用户ID
     * @param filed 查询字段 code/name/models
     * @param key 关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getDeviceAssetPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceAssetPageList(@RequestParam(name="userId", defaultValue="0") int userId,
                                       @RequestParam(name="filed", defaultValue="") String filed,
                                       @RequestParam(name="key", defaultValue="") String key,
                                       @RequestParam(name="index", defaultValue = "1") Integer index,
                                       @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Device> example = new QueryWrapper<>();
            if(userId > 0) {
                example.eq("user_id", userId);
            } else {
                example.gt("user_id", 0);
            }
            if(!StrUtil.isEmpty(filed) && !StrUtil.isEmpty(key)) {
                if ("code".equals(filed)) {
                    example.like("device_id", key);
                } else {
                    example.like("device_name", key);
                }
            }
            example.orderByDesc("create_time");
            IPage<Device> page = deviceService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取设备分布地图
     * @param types 产品类型 1：直连设备 2：网关设备 3：监控设备 4：视频存储设备 5：网关子设备 6：虚拟设备
     * @param typeArray 产品类型集合
     * @param classId 分类ID
     * @param groupId 分组ID
     * @param userId 绑定用户ID
     * @param spaceId 空间位置ID
     * @param productId 产品ID
     */
    @GetMapping("/getDevicesMap")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getDevicesMap(@RequestParam(name="types", defaultValue="0") int types,
                              @RequestParam(name="typeArray", defaultValue="") String typeArray,
                              @RequestParam(name="classId", defaultValue="0") int classId,
                              @RequestParam(name="groupId", defaultValue="0") int groupId,
                              @RequestParam(name="userId", defaultValue="0") int userId,
                              @RequestParam(name="spaceId", defaultValue="0") int spaceId,
                              @RequestParam(name="productId", defaultValue="") String productId) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Device> example = createExample(types, typeArray, classId, groupId, "", userId, spaceId, productId, 0, "", "", "", "");
            example.gt("locate_method", 0);
            example.ne("longitude", 0);
            example.ne("latitude", 0);
            DeviceMap deviceMap = deviceService.selectDeviceMap(example);
            jsonObject = ResponseUtil.getSuccessJson(deviceMap);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    private QueryWrapper<Device> createExample(int types, String typeArray, int classId, int groupId, String gatewayId, int userId, int spaceId, String productId,
                                               int status, String filed, String key, String startDate, String endDate) {
        QueryWrapper<Device> example = new QueryWrapper<>();
        if(types > 0) {
            example.eq("types", types);
        } else {
            if(!StrUtil.isEmpty(typeArray)) {
                List<Integer> typeArrayList = Arrays.stream(typeArray.split(",")).map(Integer::parseInt).toList();
                example.in("types", typeArrayList);
            }
        }
        if(classId > 0) {
            String classKey = ":" + classId + ":";
            example.like("class_route", classKey);
        }
        if(groupId > 0) {
            example.eq("group_id", groupId);
        }
        if(StrUtil.isNotEmpty(gatewayId)) {
            example.eq("gateway_id", gatewayId);
        }
        if(userId > 0) {
            example.eq("user_id", userId);
        }
        if(spaceId > 0) {
            String spaceKey = ":" + spaceId + ":";
            example.like("space_route", spaceKey);
        }
        if(StrUtil.isNotEmpty(productId)) {
            example.eq("product_id", productId);
        }
        if(status > 0) {
            example.eq("status", status);
        }
        if(!StrUtil.isEmpty(filed) && !StrUtil.isEmpty(key)) {
            if ("code".equals(filed)) {
                example.like("device_id", key);
            } else {
                example.like("device_name", key);
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
     * 获取设备分布地图
     * @param types 产品类型 0:不区分 1：直连设备 2：网关设备 3：监控设备 4：视频存储设备 5：网关子设备 6：虚拟设备
     * @param lastType 最后一级类型 0：无 1：网关子设备 2：监控通道 3：设备物模型属性 4：设备物模型功能 5：设备物模型事件 6：设备物模型属性+事件
     */
    @GetMapping("/getDevicesTree")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getDevicesTree(@RequestParam(name="types", defaultValue="") String types,
                              @RequestParam(name="lastType", defaultValue="0") int lastType) {
        JSONObject jsonObject;
        JSONArray tree;
        try {
            if(StrUtil.isEmpty(types)) {
                tree = deviceService.selectDeviceTree(new ArrayList<>(), lastType);
            } else {
                List<Integer> typeList = Arrays.stream(types.split(",")).map(Integer::parseInt).toList();
                tree = deviceService.selectDeviceTree(typeList, lastType);
            }

            jsonObject = ResponseUtil.getSuccessJson(tree);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }


    /**
     * 获取设备
     * @param id id
     */
    @GetMapping("/getDevice")
    @PublicHeadLimit
    @ResponseBody
    public void getDevice(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(id)) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空");
        } else if(deviceService.existsNotDeviceId(id)) {
            jsonObject = ResponseUtil.getErrorJson("设备信息不存在");
        } else {
            Device data = deviceService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取可用主通道号
     * @param videoDomain 设备域
     */
    @GetMapping("/useMainChannel")
    @PublicHeadLimit
    @ResponseBody
    public void useMainChannel(@RequestParam(name="videoDomain", defaultValue="") String videoDomain) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(videoDomain)) {
            jsonObject = ResponseUtil.getErrorJson("请选择视频服务器");
        } else {
            int mainCode = deviceService.useMainChannel(videoDomain);
            jsonObject = ResponseUtil.getSuccessJson(mainCode);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增设备
     * @param record 设备
     */
    @PostMapping("/addDevice")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addDevice(@Validated @Valid DeviceAddParams record) {
        JSONObject jsonObject;
        try {
            deviceService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "设备信息", "新增", "新增设备，【名称】" + record.getDeviceName());
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑设备
     * @param record 设备
     */
    @PostMapping("/editDevice")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editDevice(@Validated @Valid DeviceEditParams record) {
        JSONObject jsonObject;
        try {
            deviceService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "设备信息", "修改", "修改设备，【名称】" + record.getDeviceName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 刷新二维码
     * @param id id
     */
    @GetMapping("/refreshErCode")
    @PublicHeadLimit("user")
    @ResponseBody
    public void refreshErCode(@RequestParam(name="id", defaultValue="0") String id) {
        JSONObject jsonObject;
        try {
            if(!deviceService.existsNotDeviceId(id)) {
                deviceService.refreshErCode(id);
                jsonObject = ResponseUtil.getSuccessJson();
            } else {
                jsonObject = ResponseUtil.getErrorJson("设备信息不存在");
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 禁用设备
     * @param id id
     */
    @GetMapping("/disableDevice")
    @PublicHeadLimit("user")
    @ResponseBody
    public void disableDevice(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        try {
            if(!deviceService.existsNotDeviceId(id)) {
                Device device = deviceService.findMeta(id);
                if(device.getStatus() > 2) {
                    String deviceName = device.getDeviceName();
                    deviceService.updateStatus(id, 2);
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "设备信息", "修改", "禁用设备，【名称】" + deviceName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 启用设备
     * @param id id
     */
    @GetMapping("/enableDevice")
    @PublicHeadLimit("user")
    @ResponseBody
    public void enableDevice(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        try {
            if(!deviceService.existsNotDeviceId(id)) {
                Device device = deviceService.findMeta(id);
                if(device.getStatus() == 2) {
                    String deviceName = device.getDeviceName();
                    deviceService.updateStatus(id, 4);
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "设备信息", "修改", "启用设备，【名称】" + deviceName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 更新设备定位
     * @param deviceLocation 设备定位
     */
    @GetMapping("/updateLocation")
    @PublicHeadLimit("user")
    @ResponseBody
    public void updateLocation(@Validated @Valid DeviceLocationParams deviceLocation) {
        JSONObject jsonObject;
        try {
            deviceService.updateLocation(deviceLocation);
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 更新设备固件版本号
     * @param firmwareVersionParams  升级信息
     */
    @GetMapping("/updateFirmwareVersion")
    @PublicHeadLimit("user")
    @ResponseBody
    public void updateFirmwareVersion(@Validated @Valid DeviceFirmwareVersionParams firmwareVersionParams) {
        JSONObject jsonObject;
        try {
            String deviceId = firmwareVersionParams.getDeviceId();
            firmwareVersionParams.setDeviceId(deviceId);
            deviceService.updateFirmwareVersion(firmwareVersionParams);
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 更新设备摘要
     * @param id 设备ID
     * @param summary  设备摘要
     */
    @GetMapping("/updateSummary")
    @PublicHeadLimit("user")
    @ResponseBody
    public void updateSummary(@RequestParam(name="driveId", defaultValue="") String id,
                              @RequestParam(name="address", defaultValue="") String summary) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(id)) {
                jsonObject = ResponseUtil.getErrorJson("设备Id不能为空");
            } else if(deviceService.existsNotDeviceId(id)) {
                jsonObject = ResponseUtil.getErrorJson("设备信息不存在");
            } else if(StrUtil.isEmpty(summary)) {
                jsonObject = ResponseUtil.getErrorJson("设备摘要不能为空");
            } else {
                deviceService.updateSummary(id, summary);
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 更新设备激活
     * @param id 设备ID
     * @param status 状态 2：禁用 3：启用
     */
    @GetMapping("/updateStatus")
    @PublicHeadLimit("user")
    @ResponseBody
    public void updateStatus(@RequestParam(name="id", defaultValue="") String id,
                             @RequestParam(name="status", defaultValue="0") Integer status) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(id)) {
            jsonObject = ResponseUtil.getErrorJson("设备ID不能为空");
        } else if(status < 2 || status > 3) {
            jsonObject = ResponseUtil.getErrorJson("状态标识错误");
        } else if(deviceService.existsNotDeviceId(id)) {
            jsonObject = ResponseUtil.getErrorJson("设备信息不存在");
        } else {
            try {
                Device device = deviceService.findMeta(id);
                String deviceName = device.getDeviceName();
                deviceService.updateStatus(id, status);

                String statusName = status == 2 ? "禁用" : "启用";

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "设备信息", "修改", "修改设备为" + statusName + "状态，【名称】" + deviceName);

                jsonObject = ResponseUtil.getSuccessJson();
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 绑定用户
     * @param ids id集合
     * @param userId 用户ID
     */
    @GetMapping("/bindUserArray")
    @PublicHeadLimit("user")
    @ResponseBody
    public void bindUserArray(@RequestParam(name="ids", defaultValue="") String ids,
                         @RequestParam(name="userId", defaultValue="0") Integer userId) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("请选择设备");
            } else if(userInfoService.existsNotUserId(userId)) {
                jsonObject = ResponseUtil.getErrorJson("用户信息不存在");
            } else {
                List<String> idList = StrUtil.split(ids, ",");
                int count = deviceService.bindByUsers(idList, userId);

                UserInfo user = userInfoService.findMeta(userId);
                String userName = user.getUserName();

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "资产分配", "新增", "【用户】" + userName + "批量解绑" + count + "个设备");
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 解绑用户
     * @param id id
     */
    @GetMapping("/liftedUser")
    @PublicHeadLimit("user")
    @ResponseBody
    public void liftedUser(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(id)) {
                jsonObject = ResponseUtil.getErrorJson("设备ID不能为空");
            } else if(deviceService.existsNotDeviceId(id)) {
                jsonObject = ResponseUtil.getErrorJson("设备信息不存在");
            } else {
                Device device = deviceService.findMeta(id);
                int userId = device.getUserId();
                UserInfo user = userInfoService.findMeta(userId);
                String userName = user.getUserName();
                String deviceName = device.getDeviceName();

                deviceService.liftedByUser(id);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "资产分配", "删除", "【用户】" + userName + "解绑【设备】" + deviceName);
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 解绑多个设备
     * @param ids id集合
     */
    @GetMapping("/liftedUsers")
    @PublicHeadLimit("user")
    @ResponseBody
    public void liftedUsers(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("请选择设备");
            } else {
                List<String> idList = StrUtil.split(ids, ",");
                int count = deviceService.liftedByUsers(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "资产分配", "删除", "批量解绑" + count + "个设备");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }


    /**
     * 删除设备
     * @param id id
     */
    @GetMapping("/delDevice")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDevice(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        try {
            if(!deviceService.existsNotDeviceId(id)) {
                Device device = deviceService.findMeta(id);
                String deviceName = device.getDeviceName();
                int count = deviceService.del(id);
                if (count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "设备信息", "删除", "删除设备，【名称】" + deviceName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个设备
     * @param ids id集合
     */
    @GetMapping("/delDevices")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDevices(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<String> idList = Arrays.stream(ids.split(",")).toList();
                int count = deviceService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "设备信息", "删除", "批量删除" + count + "条设备信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
