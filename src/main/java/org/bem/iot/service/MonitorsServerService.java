package org.bem.iot.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.entity.system.Cpu;
import org.bem.iot.entity.system.DiskStore;
import org.bem.iot.entity.system.Memory;
import org.bem.iot.entity.system.Network;
import org.bem.iot.global.ServerMonitorGlobal;
import org.bem.iot.util.ConvertUtil;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.util.SystemInfoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import oshi.SystemInfo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统监控
 */
@Service
public class MonitorsServerService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 客户端连接 Key:客户端id  Value:SseEmitter
     */
    private static final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    public SseEmitter connect(String clientId) {
        SseEmitter sseEmitter = new SseEmitter();
        JSONObject sseMessage;
        if (StrUtil.isEmpty(clientId)) {
            return SseExceptionHandle(sseEmitter, "clientId不能为空");
        }
        if (sseEmitterMap.containsKey(clientId)) {
            return SseExceptionHandle(sseEmitter, "该clientId已绑定指定的客户端！clientId:" + clientId);
        }

        if (sseEmitterMap.size() > 1000) {
            return SseExceptionHandle(sseEmitter, "客户端连接过多，请稍后重试！");
        }

        // 连接成功需要返回数据，否则会出现待处理状态
        try {
            sseMessage = ResponseUtil.getSuccessJson(HttpServletResponse.SC_OK, "连接成功！");
            sseEmitter.send(sseMessage, MediaType.APPLICATION_JSON);
        } catch (IOException e) {
            logger.error("sse连接发送数据时出现异常：", e);
            throw new RuntimeException("sse连接发送数据时出现异常！");
        }

        // 连接断开
        sseEmitter.onCompletion(() -> {
            logger.info("sse连接断开，clientId为：{}", clientId);
            sseEmitterMap.remove(clientId);
        });

        // 连接超时
        sseEmitter.onTimeout(() -> {
            logger.info("sse连接已超时，clientId为：{}", clientId);
            sseEmitterMap.remove(clientId);
        });

        // 连接报错
        sseEmitter.onError((throwable) -> {
            logger.info("sse连接异常:", throwable);
            sseEmitterMap.remove(clientId);
        });
        sseEmitterMap.put(clientId, sseEmitter);

        return sseEmitter;
    }

    /**
     * 获取服务器概要信息
     * @return 返回服务器概要信息
     */
    public JSONObject getServerOverview() {
        SystemInfo systemInfo = new SystemInfo();
        Cpu cpu = SystemInfoUtil.getCpuInfo(systemInfo);
        Memory memory = SystemInfoUtil.getMemoryInfo(systemInfo);
        List<DiskStore> diskStoreList = SystemInfoUtil.getDiskStore(systemInfo);
        List<Network> networkList = SystemInfoUtil.getEthernet(systemInfo);


        JSONObject cpuObj = new JSONObject();
        cpuObj.put("bit", cpu.getCpuBit());
        cpuObj.put("physicalNum", cpu.getCpuPhysicalNumber());
        cpuObj.put("logicalNumber", cpu.getCpuLogicalNumber());
        cpuObj.put("vendorFreq", cpu.getVendorFreq());
        cpuObj.put("vendor", cpu.getVendor());
        cpuObj.put("microarchitecture", cpu.getMicroarchitecture());

        JSONObject memoryObj = new JSONObject();
        memoryObj.put("total", memory.getTotal());
        memoryObj.put("number", memory.getNumber());
        memoryObj.put("swapTotal", memory.getSwapTotal());

        JSONObject diskObj = new JSONObject();
        long capacityTotal = 0;
        StringBuilder diskName = new StringBuilder();
        for (DiskStore diskStore : diskStoreList) {
            capacityTotal += diskStore.getDiskSize();
            String diskModel = diskStore.getDiskModel();
            String[] diskModelArr = diskModel.split("\\(");
            String dName = diskModelArr[0].trim();
            if(diskName.isEmpty()) {
                diskName = new StringBuilder(dName);
            } else {
                diskName.append(",").append(dName);
            }
        }
        String size = ConvertUtil.formatCapacity(capacityTotal, true, 0);
        diskObj.put("diskName", diskName.toString());
        diskObj.put("number", diskStoreList.size());
        diskObj.put("size", size);

        JSONObject netObj = new JSONObject();
        StringBuilder netName = new StringBuilder();
        StringBuilder address = new StringBuilder();
        int i = 0;
        for (Network network : networkList) {
            if(StrUtil.isNotEmpty(network.getIpAddress())) {
                if (i == 0) {
                    netName = new StringBuilder(network.getDisplayName());
                    address = new StringBuilder(network.getIpAddress());
                } else {
                    netName.append(",").append(network.getDisplayName());
                    address.append(",").append(network.getIpAddress());
                }
                i++;
            }
        }
        netObj.put("netName", netName.toString());
        netObj.put("number", i);
        netObj.put("address", address.toString());


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cpu", cpuObj);
        jsonObject.put("memory", memoryObj);
        jsonObject.put("disk", diskObj);
        jsonObject.put("net", netObj);
        return jsonObject;
    }

    public boolean sendMessage() {
        if (ObjectUtil.isEmpty(sseEmitterMap)) {
            return false;
        }

        SystemInfo systemInfo = new SystemInfo();
        ServerMonitorGlobal.cpuRate = SystemInfoUtil.cpuUtilizationRate(systemInfo);
        ServerMonitorGlobal.memoryUse = SystemInfoUtil.memoryUsage(systemInfo);
        long[] diskUsage;
        long[] netThroughput;

        if(ServerMonitorGlobal.isFirstRun) {
            diskUsage = SystemInfoUtil.distReadWrite(systemInfo, 0, 0, true);
            netThroughput = SystemInfoUtil.netThroughput(systemInfo, 0, 0, true);
            ServerMonitorGlobal.isFirstRun = false;
        } else {
            diskUsage = SystemInfoUtil.distReadWrite(systemInfo, ServerMonitorGlobal.lastRead, ServerMonitorGlobal.lastWrite, false);
            netThroughput = SystemInfoUtil.netThroughput(systemInfo, ServerMonitorGlobal.lastRecvBytes, ServerMonitorGlobal.lastSendBytes, false);
        }
        ServerMonitorGlobal.distRead = diskUsage[0];
        ServerMonitorGlobal.distWrite = diskUsage[1];
        ServerMonitorGlobal.lastRead = diskUsage[2];
        ServerMonitorGlobal.lastWrite = diskUsage[3];
        ServerMonitorGlobal.netRecv = netThroughput[0];
        ServerMonitorGlobal.netSend = netThroughput[1];
        ServerMonitorGlobal.lastRecvBytes = netThroughput[2];
        ServerMonitorGlobal.lastSendBytes = netThroughput[3];

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cpu", ServerMonitorGlobal.cpuRate);
        jsonObject.put("memory", ServerMonitorGlobal.memoryUse);
        jsonObject.put("distRead", ServerMonitorGlobal.distRead);
        jsonObject.put("distWrite", ServerMonitorGlobal.distWrite);
        jsonObject.put("netRecv", ServerMonitorGlobal.netRecv);
        jsonObject.put("netSend", ServerMonitorGlobal.netSend);

        for (Map.Entry<String, SseEmitter> entry : sseEmitterMap.entrySet()) {
            SseEmitter sseEmitter = entry.getValue();
            JSONObject sseMessage = ResponseUtil.getSuccessJson(jsonObject);
            try {
                sseEmitter.send(sseMessage, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                logger.error("sse发送数据异常：", e);
            }
        }
        return true;
    }

    private static SseEmitter SseExceptionHandle(SseEmitter sseEmitter, String exceptionMessage) {
        try {
            JSONObject sseMessage = ResponseUtil.getErrorJson(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exceptionMessage);
            sseEmitter.send(sseMessage, MediaType.APPLICATION_JSON);
        } catch (IOException e) {
            throw new RuntimeException("sse发送数据异常！");
        }
        return sseEmitter;
    }
}
