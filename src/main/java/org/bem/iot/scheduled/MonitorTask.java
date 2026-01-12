package org.bem.iot.scheduled;

import jakarta.annotation.Resource;
import org.bem.iot.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 监控任务
 * @author jakybland
 */
@Component
public class MonitorTask {
    @Resource
    MonitorsServerService monitorsServerService;

    @Resource
    MonitorsCacheService monitorsCacheService;

    @Resource
    MonitorsTotalService monitorsTotalService;

    @Resource
    MonitorsMessageService monitorsMessageService;

    @Resource
    LogScheduleTaskService LogScheduleTaskService;

    Logger logger = LoggerFactory.getLogger("MonitorTask");

    /**
     * 系统信息实时推送
     * <br>采集完后间隔2秒
     */
    @Async
    @Scheduled(fixedDelay = 2000)
    public void serverMonitor() {
        try {
            boolean isSend = monitorsServerService.sendMessage();
            if(isSend) {
                LogScheduleTaskService.insert("serverMonitor", "系统信息实时推送", "间隔2秒执行", 1);
            }
        } catch (Exception e) {
            LogScheduleTaskService.insert("serverMonitor", "系统信息实时监控", "间隔2秒执行", 2);
            logger.error(e.getMessage());
        }
    }

    /**
     * 缓存信息实时推送
     * <br>采集完后间隔2秒
     */
    @Async
    @Scheduled(fixedDelay = 2000)
    public void cacheMonitor() {
        try {
            boolean isSend = monitorsCacheService.sendMessage();
            if(isSend) {
                LogScheduleTaskService.insert("serverMonitor", "缓存信息实时推送", "间隔2秒执行", 1);
            }
        } catch (Exception e) {
            LogScheduleTaskService.insert("serverMonitor", "缓存信息实时监控", "间隔2秒执行", 2);
            logger.error(e.getMessage());
        }
    }

    /**
     * 消息队列在线统计
     * <br>间隔5秒
     */
    @Async
    @Scheduled(fixedDelay = 5000)
    public void mqOlineMonitor() {
        try {
            monitorsMessageService.countConnect();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 监控数据汇总存储
     * <br>整点执行
     */
    @Async
    @Scheduled(cron = "0 0 * * * ?")
    public void saveMonitorData() {
        try {
            monitorsTotalService.runStatisticsBaseData();
            monitorsTotalService.runStatisticsBusinessData();
            monitorsTotalService.runStatisticsSceneLinkage();
            monitorsTotalService.runStatisticsScheduleTask();
            monitorsMessageService.saveQueueMessage();
            monitorsMessageService.saveConnectMessage();
            monitorsMessageService.saveOptionMessage();

            LogScheduleTaskService.insert("saveMonitorData", "综合监控数据汇总", "每小时整点执行", 1);
        } catch (Exception e) {
            LogScheduleTaskService.insert("saveMonitorData", "综合监控数据汇总", "每小时整点执行", 2);
            logger.error(e.getMessage());
        }
    }

//    /**
//     * 删除过期数据
//     */
//    @Async
//    @Scheduled(cron = "0 0 * * * ?")
//    public void clearSystemMonitor() {
//        monitorSystemService.delOvertime();
//    }
}
