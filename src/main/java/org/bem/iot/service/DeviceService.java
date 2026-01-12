package org.bem.iot.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.bem.iot.entity.DeviceMap;
import org.bem.iot.entity.DeviceMapPoints;
import org.bem.iot.entity.amap.Location;
import org.bem.iot.entity.params.DeviceAddParams;
import org.bem.iot.entity.params.DeviceEditParams;
import org.bem.iot.entity.params.DeviceFirmwareVersionParams;
import org.bem.iot.entity.params.DeviceLocationParams;
import org.bem.iot.mapper.postgresql.*;
import org.bem.iot.mapper.tdengine.IotStableMapper;
import org.bem.iot.model.device.*;
import org.bem.iot.model.general.*;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.product.ProductModel;
import org.bem.iot.model.user.UserInfo;
import org.bem.iot.model.video.VideoServer;
import org.bem.iot.util.AmapUitl;
import org.bem.iot.util.ErCodeUtil;
import org.bem.iot.util.InternalIdUtil;
import org.bem.iot.util.UploadFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 设备信息
 * @author jakybland
 */
@Service
public class DeviceService {
    @Resource
    DeviceMapper deviceMapper;

    @Resource
    DeviceGroupMapper deviceGroupMapper;

    @Resource
    DeviceUserMapper deviceUserMapper;

    @Resource
    DeviceParamsMapper deviceParamsMapper;

    @Resource
    DeviceChannelMapper deviceChannelMapper;

    @Resource
    DeviceAttrMapper deviceAttrMapper;

    @Resource
    DeviceControlsMapper deviceControlsMapper;

    @Resource
    ProductService productService;

    @Resource
    DriveMapper driveMapper;

    @Resource
    SpacePositionMapper spacePositionMapper;

    @Resource
    UserInfoMapper userInfoMapper;

    @Resource
    DriveParamsMapper driveParamsMapper;

    @Resource
    SystemConfigService systemConfigService;

    @Resource
    PlatformService platformService;

    @Resource
    ProductMapper productMapper;

    @Resource
    ProductModelMapper productModelMapper;

    @Resource
    FirmwareUpdateTaskMapper firmwareUpdateTaskMapper;

    @Resource
    FirmwareUpdateLogMapper firmwareUpdateLogMapper;

    @Resource
    VideoServerMapper videoServerMapper;

    @Resource
    DeviceMonitoringMapper deviceMonitoringMapper;

    @Value("${map-key}")
    String mapKey;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    IotStableMapper iotStableMapper;

    private static final Logger logger = LoggerFactory.getLogger(ProductModelService.class);

    /**
     * 统计设备数量
     * @param example 查询条件
     * @return 运用授权信息数量
     */
    public long count(QueryWrapper<Device> example) {
        return deviceMapper.selectCount(example);
    }

    /**
     * 统计设备在线数量
     * @return 运用授权信息数量
     */
    public long onlineCount() {
        QueryWrapper<Device> example = new QueryWrapper<>();
        example.eq("status", 3);
        List<Device> list = deviceMapper.selectList(example);
        long count = 0L;
        for (Device device : list) {
            int online = 0;
            String strOnline = stringRedisTemplate.opsForValue().get("device_online:" + device.getDeviceId());
            if (strOnline != null) {
                online = Integer.parseInt(strOnline);
            }
            if (online == 1) {
                count++;
            }
        }
        return count;
    }

    /**
     * 查询设备列表
     * @param example 查询条件
     * @return 设备列表
     */
    public List<Device> select(QueryWrapper<Device> example) {
        List<Device> list = deviceMapper.selectList(example);
        relevancyList(list);
        return list;
    }

    /**
     * 查询设备列表
     * @param example 查询条件
     * @return 设备列表
     */
    public List<Device> selectTop(QueryWrapper<Device> example, long size) {
        Page<Device> page = new Page<>(1L, size);
        IPage<Device> pageData = deviceMapper.selectPage(page, example);
        List<Device> list = pageData.getRecords();
        relevancyList(list);
        return list;
    }

    /**
     * 分页查询设备列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 设备列表
     */
    public IPage<Device> selectPage(QueryWrapper<Device> example, long index, long size) {
        Page<Device> page = new Page<>(index, size);
        IPage<Device> pageData = deviceMapper.selectPage(page, example);
        List<Device> list = pageData.getRecords();
        relevancyList(list);
        pageData.setRecords(list);
        return pageData;
    }

    private void relevancyList(List<Device> list) {
        for (int i = 0; i < list.size(); i++) {
            Device device = list.get(i);
            String deviceId = device.getDeviceId();
            int types = device.getTypes();
            int online = getCacheStatus(deviceId, "online");
            int alarm = getCacheStatus(deviceId, "alarm");

            Product product = productService.find(device.getProductId());
            product.setDrive(driveMapper.selectById(product.getDriveCode()));

            DeviceGroup group;
            if(device.getGroupId() > 0) {
                group = deviceGroupMapper.selectById(device.getGroupId());
            } else {
                group = null;
            }
            UserInfo user;
            if(device.getUserId() > 0) {
                user = userInfoMapper.selectById(device.getUserId());
            } else {
                user = null;
            }
            Device gateway;
            if(StrUtil.isNotEmpty(device.getGatewayId())) {
                gateway = deviceMapper.selectById(device.getGatewayId());
            } else {
                gateway = null;
            }

            if(types == 3 || types == 4) {
                DeviceMonitoring monitoring = queryDeviceMonitoring(deviceId);
                device.setParam(monitoring);
            } else {
                List<DeviceParams> params = getDevParams(deviceId);
                device.setParams(params);
            }
            device.setOnline(online);
            device.setAlarm(alarm);
            device.setProduct(product);
            device.setGroup(group);
            device.setUser(user);
            device.setGateway(gateway);
            list.set(i, device);
        }
    }
    private DeviceMonitoring queryDeviceMonitoring(String deviceId) {
        QueryWrapper<DeviceMonitoring> example = new QueryWrapper<>();
        example.eq("device_id", deviceId);
        DeviceMonitoring data = deviceMonitoringMapper.selectOne(example);
        String serverId = data.getServerId();
        VideoServer server = videoServerMapper.selectById(serverId);
        data.setServer(server);
        return data;
    }

    private void relevancyData(Device device) {
        String deviceId = device.getDeviceId();
        int types = device.getTypes();
        int online = getCacheStatus(deviceId, "online");
        int alarm = getCacheStatus(deviceId, "alarm");

        Product product = productService.find(device.getProductId());
        product.setDrive(driveMapper.selectById(product.getDriveCode()));

        DeviceGroup group;
        if(device.getGroupId() > 0) {
            group = deviceGroupMapper.selectById(device.getGroupId());
        } else {
            group = null;
        }
        UserInfo user;
        if(device.getUserId() > 0) {
            user = userInfoMapper.selectById(device.getUserId());
        } else {
            user = null;
        }
        Device gateway;
        if(StrUtil.isNotEmpty(device.getGatewayId())) {
            gateway = deviceMapper.selectById(device.getGatewayId());
        } else {
            gateway = null;
        }
        if(types == 3 || types == 4) {
            DeviceMonitoring monitoring = queryDeviceMonitoring(deviceId);
            device.setParam(monitoring);
        } else {
            List<DeviceParams> params = getDevParams(deviceId);
            device.setParams(params);
        }
        device.setOnline(online);
        device.setAlarm(alarm);
        device.setProduct(product);
        device.setGroup(group);
        device.setUser(user);
        device.setGateway(gateway);
    }
    private int getCacheStatus(String deviceId, String type) {
        if(type.equals("online")) {
            String strOnline = stringRedisTemplate.opsForValue().get("device_online:" + deviceId);
            if(strOnline != null) {
                return Integer.parseInt(strOnline);
            } else {
                return 0;
            }
        } else {
            String strAlarm = stringRedisTemplate.opsForValue().get("device_alarm:" + deviceId);
            if(strAlarm != null) {
                return Integer.parseInt(strAlarm);
            } else {
                return 0;
            }
        }
    }
    private List<DeviceParams> getDevParams(String deviceId) {
        QueryWrapper<DeviceParams> example = new QueryWrapper<>();
        example.eq("device_id", deviceId);
        example.orderByAsc("order_num");
        return deviceParamsMapper.selectList(example);
    }

    /**
     * 查询设备地图
     * @return 设备地图分布信息
     */
    public DeviceMap selectDeviceMap(QueryWrapper<Device> example) throws Exception {
        if(platformService.existsNotPlatformId("amap_js_api")) {
            throw new Exception("未配置高德地图外部接口");
        } else {
            Platform platform = platformService.find("amap_js_api");
            Map<String, String> configMap = systemConfigService.selectByGroup("parameter");
            String mapCenter = configMap.get("mapCenter");
            String mapZoom = configMap.get("mapZoom");
            if (StrUtil.isEmpty(mapCenter) && StrUtil.isEmpty(mapZoom)) {
                throw new Exception("地图中心点或缩放级别未配置");
            } else {
                BigDecimal centerLon;
                BigDecimal centerLat;
                try {
                    String[] mapCenterArray = mapCenter.split(",");
                    centerLon = new BigDecimal(mapCenterArray[0]);
                    centerLat = new BigDecimal(mapCenterArray[1]);
                } catch (Exception e) {
                    throw new Exception("地图中心点配置错误");
                }

                int zoom;
                try {
                    zoom = Integer.parseInt(mapZoom);
                } catch (Exception e) {
                    throw new Exception("地图中心点配置错误");
                }

                List<Device> list = deviceMapper.selectList(example);
                relevancyList(list);
                List<DeviceMapPoints> devices = new ArrayList<>();
                for (Device dev : list) {
                    String deviceId = dev.getDeviceId();
                    int atatus = 0;
                    String strOnline = stringRedisTemplate.opsForValue().get("device_online:" + deviceId);
                    String strAlarm = stringRedisTemplate.opsForValue().get("device_alarm:" + deviceId);

                    if (strOnline != null) {
                        int online = Integer.parseInt(strOnline);
                        if (strAlarm != null) {
                            int alarm = Integer.parseInt(strAlarm);
                            if (alarm > 0) {
                                atatus = 2;
                            } else {
                                if (online > 0) {
                                    atatus = 1;
                                }
                            }
                        } else {
                            if (online > 0) {
                                atatus = 1;
                            }
                        }
                    }

                    String[] installImgArray = dev.getInstallImg().split(",");

                    DeviceMapPoints point = new DeviceMapPoints();
                    point.setDeviceId(deviceId);
                    point.setDeviceName(dev.getDeviceName());
                    point.setTypes(dev.getProduct().getTypes());
                    point.setStatus(atatus);
                    point.setSpaceRouteName(dev.getSpaceRouteName());
                    point.setLocateMethod(dev.getLocateMethod());
                    point.setLongitude(dev.getLongitude());
                    point.setLatitude(dev.getLatitude());
                    point.setInstallImg(installImgArray[0]);
                    point.setFirmwareVersion(dev.getFirmwareVersion());
                    devices.add(point);
                }

                DeviceMap deviceMap = new DeviceMap();
                deviceMap.setKeys(platform.getAppKey());
                deviceMap.setCenterLon(centerLon);
                deviceMap.setCenterLat(centerLat);
                deviceMap.setZoom(zoom);
                deviceMap.setDevices(devices);
                return deviceMap;
            }
        }
    }

    /**
     * 查询设备树
     * @param types 产品类型 0:不区分 1：直连设备 2：网关设备 3：监控设备 4：视频存储设备 5：网关子设备 6：虚拟设备
     * @param lastType 最后一级类型 0：无 1：网关子设备 2：监控通道 3：设备物模型属性 4：设备物模型功能 5：设备物模型事件 6：设备物模型属性+事件
     * @return 返回树结构
     */
    public JSONArray selectDeviceTree(List<Integer> types, int lastType) {
        QueryWrapper<SpacePosition> example = new QueryWrapper<>();
        example.eq("level_id", 0);
        example.orderByAsc("order_num");
        List<SpacePosition> spaceList = spacePositionMapper.selectList(example);
        JSONArray arrayList = new JSONArray();
        for (SpacePosition space : spaceList) {
            int spaceId = space.getSpaceId();
            JSONArray subArray = getSubTree(spaceId, types, lastType);

            JSONObject subItem = new JSONObject();
            subItem.put("key", "S-" + spaceId);
            subItem.put("label", space.getSpaceName());
            subItem.put("value", "S-" + spaceId);
            subItem.put("id", spaceId + "");
            subItem.put("levelId", space.getLevelId());
            subItem.put("code", "");
            subItem.put("type", "position");
            if(!subArray.isEmpty()) {
                subItem.put("children", subArray);
            } else {
                JSONArray devArray = getDevTree(spaceId, types, lastType);
                subItem.put("children", devArray);
            }
            arrayList.add(subItem);
        }
        return arrayList;
    }
    private JSONArray getSubTree(int levelId, List<Integer> types, int lastType) {
        QueryWrapper<SpacePosition> example = new QueryWrapper<>();
        example.eq("level_id", levelId);
        example.orderByAsc("order_num");
        List<SpacePosition> spaceList = spacePositionMapper.selectList(example);
        JSONArray subList = new JSONArray();
        for (SpacePosition space : spaceList) {
            int spaceId = space.getSpaceId();
            JSONArray subArray = getSubTree(spaceId, types, lastType);

            JSONObject subItem = new JSONObject();
            subItem.put("key", "S-" + spaceId);
            subItem.put("label", space.getSpaceName());
            subItem.put("value", "S-" + spaceId);
            subItem.put("id", spaceId + "");
            subItem.put("levelId", space.getLevelId());
            subItem.put("code", "");
            subItem.put("type", "position");
            if(!subArray.isEmpty()) {
                subItem.put("children", subArray);
            } else {
                JSONArray devArray = getDevTree(spaceId, types, lastType);
                subItem.put("children", devArray);
            }
            subList.add(subItem);
        }
        return subList;
    }
    private JSONArray getDevTree(int spaceId, List<Integer> types, int lastType) {
        QueryWrapper<Device> example = new QueryWrapper<>();
        example.eq("space_id", spaceId);
        if(!types.isEmpty()) {
            example.in("types", types);
        }
        example.eq("status", 3);
        example.orderByDesc("create_time");
        List<Device> deviceList = deviceMapper.selectList(example);
        JSONArray subList = new JSONArray();
        for (Device device : deviceList) {
            String deviceId = device.getDeviceId();

            JSONObject subItem = new JSONObject();
            subItem.put("key", "D-" + deviceId);
            subItem.put("label", device.getDeviceName());
            subItem.put("value", "D-" + deviceId);
            subItem.put("id", deviceId);
            subItem.put("levelId", spaceId);
            subItem.put("type", "device");
            if(lastType > 0) {
                JSONArray subArray;
                if(lastType == 1) {
                    subArray =getSubDevTree(deviceId);
                } else {
                    subArray =getChannelTree(deviceId);
                }
                if(!subArray.isEmpty()) {
                    subItem.put("children", subArray);
                }
            }
            subList.add(subItem);
        }
        return subList;
    }
    private JSONArray getSubDevTree(String deviceId) {
        QueryWrapper<Device> example = new QueryWrapper<>();
        example.eq("gateway_id", deviceId);
        example.eq("status", 3);
        example.orderByDesc("create_time");
        List<Device> deviceList = deviceMapper.selectList(example);
        JSONArray subList = new JSONArray();
        for (Device device : deviceList) {
            String subDevId = device.getDeviceId();

            JSONObject subItem = new JSONObject();
            subItem.put("key", "D-" + subDevId);
            subItem.put("label", device.getDeviceName());
            subItem.put("value", "D-" + subDevId);
            subItem.put("id", subDevId);
            subItem.put("levelId", deviceId);
            subItem.put("type", "subDevice");
            subList.add(subItem);
        }
        return subList;
    }
    private JSONArray getChannelTree(String deviceId) {
        QueryWrapper<DeviceChannel> example = new QueryWrapper<>();
        example.eq("device_id", deviceId);
        example.orderByAsc("channel");
        List<DeviceChannel> channelList = deviceChannelMapper.selectList(example);
        JSONArray subList = new JSONArray();
        for (DeviceChannel channel : channelList) {
            String channelId = channel.getChannelId();

            JSONObject subItem = new JSONObject();
            subItem.put("key", "C-" + channelId);
            subItem.put("label", channel.getChannelName());
            subItem.put("value", "C-" + channelId);
            subItem.put("id", channelId);
            subItem.put("levelId", deviceId);
            subItem.put("url", channel.getSmtpUrl());
            subItem.put("type", "channel");
            subList.add(subItem);
        }
        return subList;
    }
    
    /**
     * 判断设备ID不存在
     * @param deviceId 设备ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotDeviceId(String deviceId) {
        QueryWrapper<Device> example = new QueryWrapper<>();
        example.eq("device_id", deviceId);
        return !deviceMapper.exists(example);
    }

    /**
     * 查询设备
     * @param deviceId 设备ID
     * @return 设备信息
     */
    public Device find(String deviceId) {
        Device device = deviceMapper.selectById(deviceId);
        relevancyData(device);
        return device;
    }

    /**
     * 查询设备
     * @param deviceId 设备ID
     * @return 设备信息
     */
    public Device findMeta(String deviceId) {
        return deviceMapper.selectById(deviceId);
    }

    /**
     * 获取可用主通道号
     * @return 通道号
     */
    public int useMainChannel(String videoDomain) {
        QueryWrapper<Device> example = new QueryWrapper<>();
        example.likeRight("channel_id", videoDomain);
        return deviceMapper.selectMaxMainChannel(example) + 1;
    }


    /**
     * 添加设备
     * @param record 设备信息
     */
    public void insert(DeviceAddParams record) throws Exception {
        try {
            String productId = record.getProductId();
            Product product = productMapper.selectById(productId);

            Device device = addDeviceInfo(record, product);
            int count = deviceMapper.insert(device);
            if (count > 0) {
                String deviceId = device.getDeviceId();
                int types = device.getTypes();
                String paramsData = record.getParamsData();
                String gatewayId = device.getGatewayId();
                if(types == 3 || types == 4) {
                    insertDeviceMonitoring(deviceId, paramsData);
                } else {
                    String spaceRoute = device.getSpaceRoute();

                    insertDeviceParams(deviceId, paramsData);

                    createIotSubTable(deviceId, productId, gatewayId, spaceRoute);
                }
            } else {
                throw new Exception("添加设备信息失败");
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
    private Device addDeviceInfo(DeviceAddParams record, Product product) throws Exception {
        String deviceId = record.getDeviceId();
        String devChannelId = record.getChannelId();
        int spaceId = record.getSpaceId();
        SpacePosition position = spacePositionMapper.selectById(spaceId);
        String spaceRoute = position.getSpaceRoute();
        String spaceRouteName = position.getSpaceRouteName();
        Date now = new Date();

        String classRoute = product.getClassRoute();
        int types = product.getTypes();
        int mainChannel = 0;
        if(types == 3 || types == 4) {
            mainChannel = Integer.parseInt(devChannelId.substring(devChannelId.length() - 4));
        }

        // 生成二维码
        Map<String, String> dirMap = UploadFileUtil.getStoragePath();
        String url = dirMap.get("path") + "/" + deviceId + ".png";
        String saveUrl = dirMap.get("savePath") + "/" + deviceId + ".png";
        boolean isCreate = ErCodeUtil.createErCode(deviceId, 500, 500, saveUrl);
        if(isCreate) {
            Device device = new Device();
            device.setDeviceId(deviceId);
            device.setDeviceName(record.getDeviceName());
            device.setProductId(product.getProductId());
            device.setTypes(types);
            device.setClassRoute(classRoute);
            device.setGroupId(record.getGroupId());
            device.setUserId(0);
            device.setSpaceId(spaceId);
            device.setSpaceRoute(spaceRoute);
            device.setSpaceRouteName(spaceRouteName);
            device.setGatewayId(record.getGatewayId());
            device.setLocateMethod(record.getLocateMethod());
            device.setOpenShadow(record.getOpenShadow());
            device.setAddress(record.getAddress());
            device.setIpAddress(record.getIpAddress());
            device.setLongitude(record.getLongitude());
            device.setLatitude(record.getLatitude());
            device.setInstallImg(record.getInstallImg());
            device.setSummary("");
            device.setFirmwareVersion(record.getFirmwareVersion());
            device.setMainChannel(mainChannel);
            device.setChannelId(devChannelId);
            device.setErCode(url);
            device.setRemark(record.getRemark());
            device.setStatus(0);
            device.setActiveTime(now);
            device.setCreateTime(now);
            return device;
        } else {
            throw new Exception("生成二维码失败");
        }
    }
    private void insertDeviceParams(String deviceId, String paramsData) {
        if (StrUtil.isNotEmpty(paramsData)) {
            // 结构 {params_id 和 params_value}
            JSONArray params = JSON.parseArray(paramsData);
            for (int i = 0; i < params.size(); i++) {
                JSONObject item = params.getJSONObject(i);
                long paramsId = item.getLong("paramsId");
                String paramsValue = item.getString("paramsValue");

                QueryWrapper<DriveParams> example = new QueryWrapper<>();
                example.eq("params_id", paramsId);
                if(driveParamsMapper.exists(example)) {
                    DriveParams driveParams = driveParamsMapper.selectById(paramsId);
                    String id = InternalIdUtil.createId();

                    DeviceParams param = new DeviceParams();
                    param.setId(id);
                    param.setDeviceId(deviceId);
                    param.setProductId("");
                    param.setModelIdentity("");
                    param.setParamsId(paramsId);
                    param.setDriveCode(driveParams.getDriveCode());
                    param.setGroupType(1);
                    param.setParamsName(driveParams.getParamsName());
                    param.setParamsKey(driveParams.getParamsKey());
                    param.setParamsType(driveParams.getParamsType());
                    param.setParamsValue(paramsValue);
                    param.setShowData(driveParams.getShowData());
                    param.setOrderNum(driveParams.getOrderNum());
                    deviceParamsMapper.insert(param);
                }
            }
        }
    }
    private void createIotSubTable(String deviceId, String productId, String gatewayId, String spaceRoute) {
        Device device = deviceMapper.selectById(deviceId);
        String deviceName = device.getDeviceName();

        Product product = productMapper.selectById(productId);
        String driveCode = product.getDriveCode();

        QueryWrapper<DriveParams> exampleDriveParams = new QueryWrapper<>();
        exampleDriveParams.eq("drive_id", deviceId);
        exampleDriveParams.eq("group_type", 2);
        exampleDriveParams.orderByAsc("order_num");
        List<DriveParams> driveParamsList = driveParamsMapper.selectList(exampleDriveParams);

        QueryWrapper<ProductModel> examplePtoModel = new QueryWrapper<>();
        examplePtoModel.eq("product_id", productId);
        examplePtoModel.orderByDesc("create_time");
        List<ProductModel> modelList = productModelMapper.selectList(examplePtoModel);
        for (ProductModel proModel : modelList) {
            String Identity = proModel.getModelIdentity();
            String tags = "'" + gatewayId + "', '" + spaceRoute + "'";
            try {
                iotStableMapper.createSubTable(productId, deviceId, tags, deviceName);
            } catch (Exception e) {
                logger.error("创建物模型表子表{}异常：{}", deviceId, e.getMessage());
            }

            int orderNumParam = 1;
            for(DriveParams driveParams : driveParamsList) {
                String id = InternalIdUtil.createUUID();

                DeviceParams deviceParams = new DeviceParams();
                deviceParams.setId(id);
                deviceParams.setDeviceId(deviceId);
                deviceParams.setProductId(productId);
                deviceParams.setModelIdentity(Identity);
                deviceParams.setParamsId(driveParams.getParamsId());
                deviceParams.setDriveCode(driveCode);
                deviceParams.setGroupType(driveParams.getGroupType());
                deviceParams.setParamsName(driveParams.getParamsName());
                deviceParams.setParamsKey(driveParams.getParamsKey());
                deviceParams.setParamsType(driveParams.getParamsType());
                deviceParams.setParamsValue(driveParams.getDefaultValue());
                deviceParams.setShowData(driveParams.getShowData());
                deviceParams.setOrderNum(orderNumParam);
                deviceParamsMapper.insert(deviceParams);
                orderNumParam++;
            }
        }
    }


    private void insertDeviceMonitoring(String deviceId, String paramsData) {
        if (StrUtil.isNotEmpty(paramsData)) {
            JSONObject obj = JSONObject.parseObject(paramsData);
            String serverId = obj.getString("serverId");

            VideoServer videoServer = videoServerMapper.selectById(serverId);
            String serverType = videoServer.getServerType();

            DeviceMonitoring record = new DeviceMonitoring();
            record.setDeviceId(deviceId);
            record.setServerId(serverId);
            record.setServerType(serverType);
            record.setIpAddress(obj.getString("ipAddress"));
            record.setPort(obj.getInteger("port"));
            record.setAccount(obj.getString("account"));
            record.setPassword(obj.getString("password"));
            record.setPtzType(obj.getInteger("ptzType"));
            deviceMonitoringMapper.insert(record);
        }
    }

    /**
     * 修改设备
     * @param record 设备信息
     */
    public void update(DeviceEditParams record) {
        Device device = editDeviceInfo(record);
        int count = deviceMapper.updateById(device);
        if(count >= 0) {
            String deviceId = device.getDeviceId();
            int types = device.getTypes();
            String paramsData = record.getParamsData();

            if(types == 3 || types == 4) {
                updateDeviceMonitoring(deviceId, paramsData);
            } else {
                deleteDeviceParams(deviceId);
                insertDeviceParams(deviceId, paramsData);
            }
        }
    }
    private Device editDeviceInfo(DeviceEditParams record) {
        String deviceId = record.getDeviceId();
        Device device = deviceMapper.selectById(deviceId);

        int spaceId = record.getSpaceId();
        SpacePosition position = spacePositionMapper.selectById(spaceId);
        String spaceRoute = position.getSpaceRoute();
        String spaceRouteName = position.getSpaceRouteName();

        device.setDeviceName(record.getDeviceName());
        device.setGroupId(record.getGroupId());
        device.setSpaceId(spaceId);
        device.setSpaceRoute(spaceRoute);
        device.setSpaceRouteName(spaceRouteName);
        device.setGatewayId(record.getGatewayId());
        device.setLocateMethod(record.getLocateMethod());
        device.setOpenShadow(record.getOpenShadow());
        device.setAddress(record.getAddress());
        device.setIpAddress(record.getIpAddress());
        device.setLongitude(record.getLongitude());
        device.setLatitude(record.getLatitude());
        device.setInstallImg(record.getInstallImg());
        device.setFirmwareVersion(record.getFirmwareVersion());
        device.setRemark(record.getRemark());
        return device;
    }
    private void deleteDeviceParams(String deviceId) {
        QueryWrapper<DeviceParams> example = new QueryWrapper<>();
        example.eq("device_id", deviceId);
        example.eq("group_type", 1);
        deviceParamsMapper.delete(example);
    }
    private void updateDeviceMonitoring(String deviceId, String paramsData) {
        if (StrUtil.isNotEmpty(paramsData)) {
            JSONObject obj = JSONObject.parseObject(paramsData);
            String serverId = obj.getString("serverId");

            VideoServer videoServer = videoServerMapper.selectById(serverId);
            String serverType = videoServer.getServerType();

            QueryWrapper<DeviceMonitoring> example = new QueryWrapper<>();
            example.eq("device_id", deviceId);
            DeviceMonitoring record = deviceMonitoringMapper.selectOne(example);
            record.setServerId(serverId);
            record.setServerType(serverType);
            record.setIpAddress(obj.getString("ipAddress"));
            record.setPort(obj.getInteger("port"));
            record.setAccount(obj.getString("account"));
            record.setPassword(obj.getString("password"));
            record.setPtzType(obj.getInteger("ptzType"));
            deviceMonitoringMapper.updateById(record);
        }
    }

    /**
     * 刷新二维码
     * @param deviceId 设备ID
     */
    public void refreshErCode(String deviceId) throws Exception {
        Device record = deviceMapper.selectById(deviceId);
        Map<String, String> dirMap = UploadFileUtil.getStoragePath();
        String url = dirMap.get("path") + "/" + deviceId + ".png";
        String saveUrl = dirMap.get("savePath") + "/" + deviceId + ".png";
        boolean isCreate = ErCodeUtil.createErCode(deviceId, 500, 500, saveUrl);
        if(isCreate) {
            record.setErCode(url);
            deviceMapper.updateById(record);
        } else {
            throw new Exception("生成二维码失败");
        }
    }

    /**
     * 更新设备定位
     * @param record 设备定位
     */
    public void updateLocation(DeviceLocationParams record) {
        String deviceId = record.getDeviceId();
        BigDecimal longitude = record.getLongitude();
        BigDecimal latitude = record.getLatitude();
        String address = record.getAddress();

        if(longitude.compareTo(BigDecimal.ZERO) > 0 && latitude.compareTo(BigDecimal.ZERO) > 0) {
            String location = longitude + "," + latitude;
            try {
                address = AmapUitl.getGeoAddress(location, mapKey);
            } catch (Exception ignored) {
            }
        } else {
            try {
                Location geoInfo = AmapUitl.getGeoLocation(address, null, mapKey);
                longitude = geoInfo.getLongitude();
                latitude = geoInfo.getLatitude();
            } catch (Exception ignored) {
            }
        }
        deviceMapper.updateLocation(deviceId, address, longitude, latitude);
    }

    /**
     * 更新设备固件版本号
     * @param record 固件升级信息
     */
    public void updateFirmwareVersion(DeviceFirmwareVersionParams record) throws Exception {
        String deviceId = record.getDeviceId();
        int taskId = record.getTaskId();

        Device device = deviceMapper.selectById(deviceId);
        FirmwareUpdateTask task = firmwareUpdateTaskMapper.selectById(taskId);
        String version = task.getVersion();

        deviceMapper.updateFirmwareVersion(deviceId, version);


        FirmwareUpdateLog log = new FirmwareUpdateLog();
        log.setFirmwareId(task.getFirmwareId());
        log.setTaskId(taskId);
        log.setTaskName(task.getTaskName());
        log.setDeviceId(deviceId);
        log.setDeviceName(device.getDeviceName());
        log.setVersion(task.getVersion());
        log.setStatus(record.getStatus());
        log.setProgress(record.getProgress());
        log.setUpdateTime(new Date());

        firmwareUpdateLogSave(log, task);
    }
    private void firmwareUpdateLogSave(FirmwareUpdateLog record, FirmwareUpdateTask task) throws Exception {
        int taskId = record.getTaskId();
        String deviceId = record.getDeviceId();

        QueryWrapper<FirmwareUpdateLog> example = new QueryWrapper<>();
        example.eq("task_id", taskId);
        example.eq("device_id", deviceId);
        example.lt("status", 3);

        Date updateTime = new Date();
        try {
            if (firmwareUpdateLogMapper.exists(example)) {
                FirmwareUpdateLog log = firmwareUpdateLogMapper.selectOne(example);
                log.setStatus(record.getStatus());
                log.setProgress(record.getProgress());
                log.setUpdateTime(updateTime);
                firmwareUpdateLogMapper.updateById(log);
            } else {
                int firmwareId = task.getFirmwareId();

                record.setLogId(null);
                record.setFirmwareId(firmwareId);
                record.setUpdateTime(updateTime);
                int count = firmwareUpdateLogMapper.insert(record);
                if (count < 1) {
                    throw new Exception("提交失败");
                }
            }
        } catch (Exception e) {
            throw new Exception("提交失败");
        }
    }

    /**
     * 更新设备摘要
     * @param deviceId 设备ID
     * @param summary 设备摘要
     */
    public void updateSummary(String deviceId, String summary) {
        deviceMapper.updateSummary(deviceId, summary);
    }

    /**
     * 设备激活
     * @param deviceId 设备ID
     */
    public void updateActive(String deviceId) {
        deviceMapper.updateActive(deviceId, new Date());
    }

    /**
     * 修改设备状态
     * @param deviceId 设备ID
     * @param status 设备状态
     */
    public void updateStatus(String deviceId, int status) {
        deviceMapper.updateStatus(deviceId, status);
    }

    /**
     * 绑定用户
     * @param idList 设备ID集合
     * @param userId 用户ID
     */
    public int bindByUsers(List<String> idList, int userId) {
        QueryWrapper<Device> example = new QueryWrapper<>();
        example.in("device_id", idList);
        List<Device> list = deviceMapper.selectList(example);
        for (Device record : list) {
            record.setUserId(userId);
            deviceMapper.updateById(record);
        }
        return idList.size();
    }

    /**
     * 解绑用户
     * @param deviceId 设备ID
     */
    public void liftedByUser(String deviceId) {
        Device record = deviceMapper.selectById(deviceId);
        record.setUserId(0);
        deviceMapper.updateById(record);
    }

    /**
     * 批量解绑用户
     * @param idList 设备ID集合
     */
    public int liftedByUsers(List<String> idList) {
        QueryWrapper<Device> example = new QueryWrapper<>();
        example.in("device_id", idList);
        List<Device> list = deviceMapper.selectList(example);
        for (Device record : list) {
            record.setUserId(0);
            deviceMapper.updateById(record);
        }
        return idList.size();
    }

    /**
     * 删除设备
     * @param deviceId 设备ID
     * @return 删除数量
     */
    public int del(String deviceId) {
        dropIotTable(deviceId);
        delByDeviceParams(deviceId);
        delByDeviceAttr(deviceId);
        delByDeviceChannel(deviceId);
        delByDeviceUser(deviceId);
        delByDeviceControls(deviceId);
        return deviceMapper.deleteById(deviceId);
    }

    /**
     * 批量删除设备
     * @param idList 设备ID列表
     * @return 删除数量
     */
    public int delArray(List<String> idList) {
        dropIotTableArray(idList);
        delByDeviceParamsArray(idList);
        delByDeviceAttrArray(idList);
        delByDeviceChannelArray(idList);
        delByDeviceUserArray(idList);
        delByDeviceControlsArray(idList);
        return deviceMapper.deleteBatchIds(idList);
    }

    public void delByProductId(String productId) {
        QueryWrapper<Device> example = new QueryWrapper<>();
        example.eq("product_id", productId);
        List<Device> devList = deviceMapper.selectList(example);
        for (Device device : devList) {
            String deviceId = device.getDeviceId();
            dropIotTable(deviceId);
            delByDeviceParams(deviceId);
            delByDeviceUser(deviceId);
            deviceMapper.deleteById(deviceId);
        }
        dropIotSTable(productId);
    }

    public void delByProductIdArray(List<String> productIdArray) {
        for (String productId : productIdArray) {
            delByProductId(productId);
        }
    }

    private void delByDeviceUser(String deviceId) {
        QueryWrapper<DeviceUser> example = new QueryWrapper<>();
        example.eq("device_id", deviceId);
        deviceUserMapper.delete(example);
    }

    private void delByDeviceControls(String deviceId) {
        String key = "\"deviceId\":" + deviceId;

        QueryWrapper<DeviceControls> example = new QueryWrapper<>();
        example.like("control_rule", key);
        deviceControlsMapper.delete(example);
    }

    private void delByDeviceUserArray(List<String> idList) {
        QueryWrapper<DeviceUser> example = new QueryWrapper<>();
        example.in("device_id", idList);
        deviceUserMapper.delete(example);
    }

    private void delByDeviceControlsArray(List<String> idList) {
        for (String deviceId : idList) {
            String key = "\"deviceId\":" + deviceId;

            QueryWrapper<DeviceControls> example = new QueryWrapper<>();
            example.like("control_rule", key);
            deviceControlsMapper.delete(example);
        }
    }

    private void delByDeviceParams(String deviceId) {
        QueryWrapper<DeviceParams> example = new QueryWrapper<>();
        example.eq("device_id", deviceId);
        deviceParamsMapper.delete(example);
    }

    private void delByDeviceAttr(String deviceId) {
        QueryWrapper<DeviceAttr> example = new QueryWrapper<>();
        example.eq("device_id", deviceId);
        deviceAttrMapper.delete(example);
    }

    private void delByDeviceChannel(String deviceId) {
        QueryWrapper<DeviceChannel> example = new QueryWrapper<>();
        example.eq("device_id", deviceId);
        deviceChannelMapper.delete(example);
    }

    private void delByDeviceParamsArray(List<String> idList) {
        QueryWrapper<DeviceParams> example = new QueryWrapper<>();
        example.in("device_id", idList);
        deviceParamsMapper.delete(example);
    }

    private void delByDeviceAttrArray(List<String> idList) {
        QueryWrapper<DeviceAttr> example = new QueryWrapper<>();
        example.in("device_id", idList);
        deviceAttrMapper.delete(example);
    }

    private void delByDeviceChannelArray(List<String> idList) {
        QueryWrapper<DeviceChannel> example = new QueryWrapper<>();
        example.in("device_id", idList);
        deviceChannelMapper.delete(example);
    }

    private void dropIotTable(String deviceId) {
        try {
            iotStableMapper.delSubTable(deviceId);
        } catch (Exception e) {
            logger.error("删除物模型子表{}失败：{}", deviceId, e.getMessage());
        }
    }

    private void dropIotTableArray(List<String> idList) {
        int index = 0;
        StringBuilder tables = new StringBuilder();
        for (String deviceId : idList) {
            if(index == 0) {
                tables = new StringBuilder("IF EXISTS bemcn." + deviceId);
            } else {
                tables.append(", IF EXISTS bemcn.").append(deviceId);
            }
            index++;
        }
        try {
            iotStableMapper.delSubTableArray(tables.toString());
        } catch (Exception e) {
            logger.error("批量删除物模型子表{}失败：{}", tables, e.getMessage());
        }
    }

    private void dropIotSTable(String productId) {
        try {
            iotStableMapper.dropStable(productId);
        } catch (Exception e) {
            logger.error("删除物模型主表{}失败：{}", productId, e.getMessage());
        }
    }
}
