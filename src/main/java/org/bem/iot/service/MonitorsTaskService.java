package org.bem.iot.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.bem.iot.mapper.postgresql.SceneGroupMapper;
import org.bem.iot.mapper.tdengine.LogSceneLinkageMapper;
import org.bem.iot.mapper.tdengine.LogScheduleTaskMapper;
import org.bem.iot.model.log.LogSceneLinkage;
import org.bem.iot.model.log.LogScheduleTask;
import org.bem.iot.model.scene.SceneGroup;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 任务监控
 */
@Service
public class MonitorsTaskService {
    @Resource
    LogScheduleTaskMapper logScheduleTaskMapper;

    @Resource
    LogSceneLinkageMapper logSceneLinkageMapper;

    @Resource
    SceneGroupMapper sceneGroupMapper;

    /**
     * 任务日统计
     * @return 返回统计结果
     */
    public JSONObject statisticsTaskDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long[] successData = new long[24];
        long[] failData = new long[24];
        String[] xAxis = new String[24];
        for (int i = 0; i < 24; i++) {
            calendar.set(Calendar.HOUR_OF_DAY, i);
            long startTime = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, i + 1);
            long endTime = calendar.getTimeInMillis();
            xAxis[i] = i + ":00";

            QueryWrapper<LogScheduleTask> example = new QueryWrapper<>();
            example.between("ts", startTime, endTime);
            example.eq("status", 1);
            successData[i] = logScheduleTaskMapper.selectCount(example);

            example = new QueryWrapper<>();
            example.between("ts", startTime, endTime);
            example.eq("status", 2);
            failData[i] = logScheduleTaskMapper.selectCount(example);
        }
        JSONObject data = new JSONObject();
        data.put("xAxis", xAxis);
        data.put("successData", successData);
        data.put("failData", failData);
        return data;
    }

    /**
     * 任务周统计
     * @return 返回统计结果
     */
    public JSONObject statisticsTaskWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.CHINESE);

        long[] successData = new long[7];
        long[] failData = new long[7];
        String[] xAxis = new String[7];
        for (int i = 0; i < 7; i++) {
            int dayNum = 6 - i;

            Calendar calendar = Calendar.getInstance();
            if(dayNum > 0) {
                calendar.add(Calendar.DAY_OF_MONTH, -dayNum);
            }
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long startTime = calendar.getTimeInMillis();
            xAxis[i] = sdf.format(calendar.getTime());

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            long endTime = calendar.getTimeInMillis();

            QueryWrapper<LogScheduleTask> example = new QueryWrapper<>();
            example.between("ts", startTime, endTime);
            example.eq("status", 1);
            successData[i] = logScheduleTaskMapper.selectCount(example);

            example = new QueryWrapper<>();
            example.between("ts", startTime, endTime);
            example.eq("status", 2);
            failData[i] = logScheduleTaskMapper.selectCount(example);
        }
        JSONObject data = new JSONObject();
        data.put("xAxis", xAxis);
        data.put("successData", successData);
        data.put("failData", failData);
        return data;
    }

    /**
     * 任务月统计
     * @return 返回统计结果
     */
    public JSONObject statisticsTaskMonth() {
        Calendar now = Calendar.getInstance();
        int d = now.getActualMaximum(Calendar.DAY_OF_MONTH);

        long[] successData = new long[d];
        long[] failData = new long[d];
        String[] xAxis = new String[d];
        for (int i = 0; i < d; i++) {
            int n = i + 1;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, n);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long startTime = calendar.getTimeInMillis();
            xAxis[i] = n + "日";

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            long endTime = calendar.getTimeInMillis();

            QueryWrapper<LogScheduleTask> example = new QueryWrapper<>();
            example.between("ts", startTime, endTime);
            example.eq("status", 1);
            successData[i] = logScheduleTaskMapper.selectCount(example);

            example = new QueryWrapper<>();
            example.between("ts", startTime, endTime);
            example.eq("status", 2);
            failData[i] = logScheduleTaskMapper.selectCount(example);
        }
        JSONObject data = new JSONObject();
        data.put("xAxis", xAxis);
        data.put("successData", successData);
        data.put("failData", failData);
        return data;
    }

    /**
     * 任务分类统计
     * @return 返回统计结果
     */
    public JSONArray statisticsTaskClass() {
        String[] classArray = {"数据推送", "数据汇总", "业务执行", "数据清理"};
        String[] keyArray = {"推送", "汇总", "执行", "清理"};
        int len = classArray.length;

        JSONArray dataArray = new JSONArray();
        for(int i = 0; i < len; i++) {
            String className = classArray[i];
            String key = keyArray[i];

            QueryWrapper<LogScheduleTask> example = new QueryWrapper<>();
            example.like("description", key);
            long count = logScheduleTaskMapper.selectCount(example);

            JSONObject item = new JSONObject();
            item.put("name", className);
            item.put("value", count);
            dataArray.add(item);
        }
        return dataArray;
    }

    /**
     * 场景日统计
     * @return 返回统计结果
     */
    public JSONObject statisticsSceneDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long[] successData = new long[24];
        long[] failData = new long[24];
        String[] xAxis = new String[24];
        for (int i = 0; i < 24; i++) {
            calendar.set(Calendar.HOUR_OF_DAY, i);
            long startTime = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, i + 1);
            long endTime = calendar.getTimeInMillis();
            xAxis[i] = i + ":00";

            QueryWrapper<LogSceneLinkage> example = new QueryWrapper<>();
            example.between("ts", startTime, endTime);
            example.eq("status", 1);
            successData[i] = logSceneLinkageMapper.selectCount(example);

            example = new QueryWrapper<>();
            example.between("ts", startTime, endTime);
            example.eq("status", 2);
            failData[i] = logSceneLinkageMapper.selectCount(example);
        }
        JSONObject data = new JSONObject();
        data.put("xAxis", xAxis);
        data.put("successData", successData);
        data.put("failData", failData);
        return data;
    }

    /**
     * 场景周统计
     * @return 返回统计结果
     */
    public JSONObject statisticsSceneWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.CHINESE);

        long[] successData = new long[7];
        long[] failData = new long[7];
        String[] xAxis = new String[7];
        for (int i = 0; i < 7; i++) {
            int dayNum = 6 - i;

            Calendar calendar = Calendar.getInstance();
            if(dayNum > 0) {
                calendar.add(Calendar.DAY_OF_MONTH, -dayNum);
            }
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long startTime = calendar.getTimeInMillis();
            xAxis[i] = sdf.format(calendar.getTime());

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            long endTime = calendar.getTimeInMillis();

            QueryWrapper<LogSceneLinkage> example = new QueryWrapper<>();
            example.between("ts", startTime, endTime);
            example.eq("status", 1);
            successData[i] = logSceneLinkageMapper.selectCount(example);

            example = new QueryWrapper<>();
            example.between("ts", startTime, endTime);
            example.eq("status", 2);
            failData[i] = logSceneLinkageMapper.selectCount(example);
        }
        JSONObject data = new JSONObject();
        data.put("xAxis", xAxis);
        data.put("successData", successData);
        data.put("failData", failData);
        return data;
    }

    /**
     * 场景月统计
     * @return 返回统计结果
     */
    public JSONObject statisticsSceneMonth() {
        Calendar now = Calendar.getInstance();
        int d = now.getActualMaximum(Calendar.DAY_OF_MONTH);

        long[] successData = new long[d];
        long[] failData = new long[d];
        String[] xAxis = new String[d];
        for (int i = 0; i < d; i++) {
            int n = i + 1;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, n);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long startTime = calendar.getTimeInMillis();
            xAxis[i] = n + "日";

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            long endTime = calendar.getTimeInMillis();

            QueryWrapper<LogSceneLinkage> example = new QueryWrapper<>();
            example.between("ts", startTime, endTime);
            example.eq("status", 1);
            successData[i] = logSceneLinkageMapper.selectCount(example);

            example = new QueryWrapper<>();
            example.between("ts", startTime, endTime);
            example.eq("status", 2);
            failData[i] = logSceneLinkageMapper.selectCount(example);
        }
        JSONObject data = new JSONObject();
        data.put("xAxis", xAxis);
        data.put("successData", successData);
        data.put("failData", failData);
        return data;
    }

    /**
     * 场景分组统计
     * @return 返回统计结果
     */
    public JSONArray statisticsSceneGroup() {
        QueryWrapper<SceneGroup> exampleGroup = new QueryWrapper<>();
        exampleGroup.orderByAsc("order_num");
        List<SceneGroup> groupList = sceneGroupMapper.selectList(exampleGroup);
        int len = groupList.size();

        JSONArray dataArray = new JSONArray();
        for (SceneGroup group : groupList) {
            int sceneGroupId = group.getSceneGroupId();
            String groupName = group.getGroupName();

            QueryWrapper<LogSceneLinkage> example = new QueryWrapper<>();
            example.eq("scene_group_id", sceneGroupId);
            long count = logSceneLinkageMapper.selectCount(example);

            JSONObject item = new JSONObject();
            item.put("name", groupName);
            item.put("value", count);
            dataArray.add(item);
        }
        return dataArray;
    }
}
