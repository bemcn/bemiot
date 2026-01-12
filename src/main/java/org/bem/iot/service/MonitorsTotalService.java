package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.bem.iot.mapper.postgresql.*;
import org.bem.iot.mapper.tdengine.*;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.log.LogSceneLinkage;
import org.bem.iot.model.log.LogScheduleTask;
import org.bem.iot.model.monitor.StatisticsBaseData;
import org.bem.iot.model.monitor.StatisticsBusinessData;
import org.bem.iot.model.monitor.StatisticsSceneLinkage;
import org.bem.iot.model.monitor.StatisticsScheduleTask;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.product.ProductModel;
import org.bem.iot.model.user.MessageUser;
import org.bem.iot.util.InternalIdUtil;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 监控数据汇总存储
 * @author jakybland
 */
@Service
public class MonitorsTotalService {
    @Resource
    UserInfoMapper userInfoMapper;

    @Resource
    ProductMapper productMapper;

    @Resource
    DeviceMapper deviceMapper;

    @Resource
    ProductModelMapper productModelMapper;

    @Resource
    DeviceChannelMapper deviceChannelMapper;

    @Resource
    DataBridgeMapper dataBridgeMapper;

    @Resource
    SceneLinkageMapper sceneLinkageMapper;

    @Resource
    IotStableMapper iotStableMapper;

    @Resource
    IotDataMapper iotDataMapper;

    @Resource
    StatisticsBaseDataMapper statisticsBaseDataMapper;

    @Resource
    StatisticsBusinessDataMapper statisticsBusinessDataMapper;

    @Resource
    StatisticsSceneLinkageMapper statisticsSceneLinkageMapper;

    @Resource
    StatisticsScheduleTaskMapper statisticsScheduleTaskMapper;

    @Resource
    MessageUserMapper messageUserMapper;

    @Resource
    LogSceneLinkageMapper logSceneLinkageMapper;

    @Resource
    LogScheduleTaskMapper logScheduleTaskMapper;

    /**
     * 运行基础数据统计
     */
    public void runStatisticsBaseData() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        long userNumber = userInfoMapper.selectCount(new QueryWrapper<>());
        long productNumber = productMapper.selectCount(new QueryWrapper<>());
        long deviceNumber = deviceMapper.selectCount(new QueryWrapper<>());
        long modelNumber = getDeviceModelNumber();
        long channelNumber = deviceChannelMapper.selectCount(new QueryWrapper<>());
        long bridgingNumber = dataBridgeMapper.selectCount(new QueryWrapper<>());
        long linkageNumber = sceneLinkageMapper.selectCount(new QueryWrapper<>());
        String id = InternalIdUtil.createId();

        StatisticsBaseData data = new StatisticsBaseData();
        data.setTs(System.currentTimeMillis());
        data.setLogId(id);
        data.setUserNumber(userNumber);
        data.setProductNumber(productNumber);
        data.setDeviceNumber(deviceNumber);
        data.setModelNumber(modelNumber);
        data.setChannelNumber(channelNumber);
        data.setBridgingNumber(bridgingNumber);
        data.setLinkageNumber(linkageNumber);
        data.setYear(year);
        data.setMonth(month);
        data.setDay(day);
        data.setHour(hour);
        statisticsBaseDataMapper.insert(data);
    }
    private long getDeviceModelNumber() {
        List<Product> productList = productMapper.selectList(new QueryWrapper<>());
        long modelNumber = 0L;
        for (Product product : productList) {
            String productId = product.getProductId();

            QueryWrapper<ProductModel> exampleModel = new QueryWrapper<>();
            exampleModel.eq("product_id", productId);
            long modelCount = productModelMapper.selectCount(exampleModel);

            QueryWrapper<Device> exampleDevice = new QueryWrapper<>();
            exampleDevice.eq("product_id", productId);
            long deviceCount = deviceMapper.selectCount(exampleDevice);

            long productModelCount = modelCount * deviceCount;
            modelNumber += productModelCount;
        }
        return modelNumber;
    }

    /**
     * 运行业务数据统计
     */
    public void runStatisticsBusinessData() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        long endTimer = calendar.getTimeInMillis();

        calendar.add(Calendar.HOUR_OF_DAY, -1);
        long startTimer = calendar.getTimeInMillis();

        long modelNumber = getModelNumber(startTimer, endTimer);
        long logNumber = getLogNumber(startTimer, endTimer);
        long statisticsNumber = getStatisticsNumber(startTimer, endTimer);
        long messageNumber = getMessageNumber(startTimer, endTimer);
        String id = InternalIdUtil.createId();

        StatisticsBusinessData data = new StatisticsBusinessData();
        data.setTs(System.currentTimeMillis());
        data.setLogId(id);
        data.setModelNumber(modelNumber);
        data.setLogNumber(logNumber);
        data.setStatisticsNumber(statisticsNumber);
        data.setMessageNumber(messageNumber);
        data.setYear(year);
        data.setMonth(month);
        data.setDay(day);
        data.setHour(hour);
        statisticsBusinessDataMapper.insert(data);
    }
    private long getModelNumber(long startTimer, long endTimer) {
        QueryWrapper<Device> exampleDevice = new QueryWrapper<>();
        exampleDevice.in("types", 1, 2, 6);
        exampleDevice.eq("status", 1);
        List<Device> deviceList = deviceMapper.selectList(exampleDevice);
        long modelNumber = 0L;
        for (Device device : deviceList) {
            String deviceId = device.getDeviceId();
            List<String> tables = iotStableMapper.selectTablesLike(deviceId);
            if(tables.size() == 1) {
                QueryWrapper<Map<String, Object>> exampleData = new QueryWrapper<>();
                exampleData.between("ts", startTimer, endTimer);
                long devCount = iotDataMapper.queryCountBy(deviceId, exampleData);
                modelNumber += devCount;
            }
        }
        return modelNumber;
    }
    private long getLogNumber(long startTimer, long endTimer) {
        List<String> tables = iotStableMapper.selectTablesLikeLift("log_");
        long logNumber = 0L;
        for (String table : tables) {
            QueryWrapper<Map<String, Object>> exampleData = new QueryWrapper<>();
            exampleData.between("ts", startTimer, endTimer);
            long count = iotDataMapper.queryCountBy(table, exampleData);
            logNumber += count;
        }
        return logNumber;
    }
    private long getStatisticsNumber(long startTimer, long endTimer) {
        List<String> tables = iotStableMapper.selectTablesLikeLift("statistics_");
        long statisticsNumber = 0L;
        for (String table : tables) {
            QueryWrapper<Map<String, Object>> exampleData = new QueryWrapper<>();
            exampleData.between("ts", startTimer, endTimer);
            long count = iotDataMapper.queryCountBy(table, exampleData);
            statisticsNumber += count;
        }
        return statisticsNumber;
    }
    private long getMessageNumber(long startTimer, long endTimer) {
        QueryWrapper<MessageUser> exampleMsg = new QueryWrapper<>();
        exampleMsg.between("message_time", startTimer, endTimer);
        return messageUserMapper.selectCount(exampleMsg);
    }

    /**
     * 场景联动执行统计
     */
    public void runStatisticsSceneLinkage() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        long endTimer = calendar.getTimeInMillis();

        calendar.add(Calendar.HOUR_OF_DAY, -1);
        long startTimer = calendar.getTimeInMillis();

        long successNumber = getSceneSuccessNumber(startTimer, endTimer);
        long failNumber = getSceneFailNumber(startTimer, endTimer);
        String id = InternalIdUtil.createId();

        StatisticsSceneLinkage data = new StatisticsSceneLinkage();
        data.setTs(System.currentTimeMillis());
        data.setLogId(id);
        data.setSuccessNumber(successNumber);
        data.setFailNumber(failNumber);
        data.setYear(year);
        data.setMonth(month);
        data.setDay(day);
        data.setHour(hour);
        statisticsSceneLinkageMapper.insert(data);
    }
    private long getSceneSuccessNumber(long startTimer, long endTimer) {
        QueryWrapper<LogSceneLinkage> example = new QueryWrapper<>();
        example.between("ts", startTimer, endTimer);
        example.eq("status", 1);
        return logSceneLinkageMapper.selectCount(example);
    }
    private long getSceneFailNumber(long startTimer, long endTimer) {
        QueryWrapper<LogSceneLinkage> example = new QueryWrapper<>();
        example.between("ts", startTimer, endTimer);
        example.eq("status", 2);
        return logSceneLinkageMapper.selectCount(example);
    }

    public void runStatisticsScheduleTask() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        long endTimer = calendar.getTimeInMillis();

        calendar.add(Calendar.HOUR_OF_DAY, -1);
        long startTimer = calendar.getTimeInMillis();

        long successNumber = getTaskSuccessNumber(startTimer, endTimer);
        long failNumber = getTaskFailNumber(startTimer, endTimer);
        String id = InternalIdUtil.createId();

        StatisticsScheduleTask data = new StatisticsScheduleTask();
        data.setTs(System.currentTimeMillis());
        data.setLogId(id);
        data.setSuccessNumber(successNumber);
        data.setFailNumber(failNumber);
        data.setYear(year);
        data.setMonth(month);
        data.setDay(day);
        data.setHour(hour);
        statisticsScheduleTaskMapper.insert(data);
    }
    private long getTaskSuccessNumber(long startTimer, long endTimer) {
        QueryWrapper<LogScheduleTask> example = new QueryWrapper<>();
        example.between("ts", startTimer, endTimer);
        example.eq("status", 1);
        return logScheduleTaskMapper.selectCount(example);
    }
    private long getTaskFailNumber(long startTimer, long endTimer) {
        QueryWrapper<LogScheduleTask> example = new QueryWrapper<>();
        example.between("ts", startTimer, endTimer);
        example.eq("status", 2);
        return logScheduleTaskMapper.selectCount(example);
    }
}
