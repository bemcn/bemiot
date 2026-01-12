package org.bem.iot.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.DeviceMapper;
import org.bem.iot.mapper.postgresql.SpacePositionMapper;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.general.SpacePosition;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 空间位置
 * @author jakybland
 */
@Service
public class SpacePositionService {
    @Resource
    SpacePositionMapper spacePositionMapper;

    @Resource
    DeviceMapper deviceMapper;

    /**
     * 获取空间位置树型表
     * @return 空间位置列表
     */
    public JSONArray selectTreeTable() {
        QueryWrapper<SpacePosition> example = new QueryWrapper<>();
        example.eq("level_id", 0);
        example.orderByAsc("order_num");
        List<SpacePosition> spaceList = spacePositionMapper.selectList(example);
        JSONArray arrayList = new JSONArray();
        for (SpacePosition space : spaceList) {
            int spaceId = space.getSpaceId();
            JSONArray subArray = getSubTable(spaceId);

            JSONObject subItem = new JSONObject();
            subItem.put("spaceId", spaceId);
            subItem.put("spaceName", space.getSpaceName());
            subItem.put("levelId", 0);
            subItem.put("spaceRoute", space.getSpaceRoute());
            subItem.put("spaceRouteName", space.getSpaceRouteName());
            subItem.put("orderNum", space.getOrderNum());
            if(!subArray.isEmpty()) {
                subItem.put("children", subArray);
            }
            arrayList.add(subItem);
        }
        return arrayList;
    }
    private JSONArray getSubTable(int levelId) {
        QueryWrapper<SpacePosition> example = new QueryWrapper<>();
        example.eq("level_id", levelId);
        example.orderByAsc("order_num");
        List<SpacePosition> spaceList = spacePositionMapper.selectList(example);
        JSONArray subList = new JSONArray();
        for (SpacePosition space : spaceList) {
            int spaceId = space.getSpaceId();
            JSONArray subArray = getSubTable(spaceId);

            JSONObject subItem = new JSONObject();
            subItem.put("spaceId", spaceId);
            subItem.put("spaceName", space.getSpaceName());
            subItem.put("levelId", levelId);
            subItem.put("spaceRoute", space.getSpaceRoute());
            subItem.put("spaceRouteName", space.getSpaceRouteName());
            subItem.put("orderNum", space.getOrderNum());
            if(!subArray.isEmpty()) {
                subItem.put("children", subArray);
            }
            subList.add(subItem);
        }
        return subList;
    }

    /**
     * 获取空间位置树
     * @return 空间位置树
     */
    public JSONArray selectTree() {
        QueryWrapper<SpacePosition> example = new QueryWrapper<>();
        example.eq("level_id", 0);
        example.orderByAsc("order_num");
        List<SpacePosition> spaceList = spacePositionMapper.selectList(example);
        JSONArray arrayList = new JSONArray();
        for (SpacePosition space : spaceList) {
            int spaceId = space.getSpaceId();
            JSONArray subArray = getSubTree(spaceId);

            JSONObject subItem = new JSONObject();
            subItem.put("key", spaceId + "");
            subItem.put("label", space.getSpaceName());
            subItem.put("value", spaceId + "");
            subItem.put("levelId", space.getLevelId());
            if(!subArray.isEmpty()) {
                subItem.put("children", subArray);
            }
            arrayList.add(subItem);
        }
        return arrayList;
    }
    private JSONArray getSubTree(int levelId) {
        QueryWrapper<SpacePosition> example = new QueryWrapper<>();
        example.eq("level_id", levelId);
        example.orderByAsc("order_num");
        List<SpacePosition> spaceList = spacePositionMapper.selectList(example);
        JSONArray subList = new JSONArray();
        for (SpacePosition space : spaceList) {
            int spaceId = space.getSpaceId();
            JSONArray subArray = getSubTree(spaceId);

            JSONObject subItem = new JSONObject();
            subItem.put("key", spaceId + "");
            subItem.put("label", space.getSpaceName());
            subItem.put("value", spaceId + "");
            subItem.put("levelId", space.getLevelId());
            if(!subArray.isEmpty()) {
                subItem.put("children", subArray);
            }
            subList.add(subItem);
        }
        return subList;
    }

    /**
     * 判断空间位置ID是否存在
     * @param spaceId id
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotSpaceId(int spaceId) {
        QueryWrapper<SpacePosition> example = new QueryWrapper<>();
        example.eq("space_id", spaceId);
        return !spacePositionMapper.exists(example);
    }

    /**
     * 查询空间位置
     * @param spaceId 空间位置ID
     * @return 空间位置信息
     */
    public SpacePosition find(@Param("spaceId") int spaceId) {
        return spacePositionMapper.selectById(spaceId);
    }

    /**
     * 添加空间位置
     * @param record 空间位置信息
     * @throws Exception 异常信息
     */
    public void insert(SpacePosition record) throws Exception {
        String spaceName = record.getSpaceName();
        int levelId = 0;
        if(record.getLevelId() != null) {
            levelId = record.getLevelId();
        }
        int orderNum = spacePositionMapper.selectMax(levelId) + 1;

        record.setSpaceId(null);
        record.setOrderNum(orderNum);
        int count = spacePositionMapper.insert(record);
        if(count > 0) {
            int spaceId = record.getSpaceId();
            String spaceRoute = ":" + spaceId + ":";
            String spaceRouteName = spaceName;
            if (levelId > 0) {
                SpacePosition levelPosition = spacePositionMapper.selectById(levelId);
                spaceRoute = levelPosition.getSpaceRoute() + ":" + spaceId + ":";
                spaceRouteName = levelPosition.getSpaceRouteName() + " > " + spaceName;
            }
            record.setSpaceRoute(spaceRoute);
            record.setSpaceRouteName(spaceRouteName);
            spacePositionMapper.updateById(record);
        } else {
            throw new Exception("新增空间位置失败");
        }
    }

    /**
     * 修改空间位置
     * @param record 空间位置信息
     */
    public void update(SpacePosition record) {
        int spaceId = record.getSpaceId();
        int levelId = record.getLevelId();
        String spaceName = record.getSpaceName();

        SpacePosition position = spacePositionMapper.selectById(spaceId);
        int oldLevelId = position.getLevelId();
        if(levelId != oldLevelId) {
            // 获取旧级联作为查询KEY和替换关键字
            String oldSpaceRoute = position.getSpaceRoute();
            String oldSpaceRouteName = position.getSpaceRouteName();

            // 获取新的级联作为替换内容
            String newSpaceRoute = ":" + spaceId + ":";
            String newSpaceRouteName = spaceName;
            if(levelId > 0) {
                SpacePosition levelPosition = spacePositionMapper.selectById(levelId);
                newSpaceRoute = levelPosition.getSpaceRoute() + ":" + spaceId + ":";
                newSpaceRouteName = levelPosition.getSpaceRouteName() + " > " + spaceName;
            }

            record.setSpaceRoute(newSpaceRoute);
            record.setSpaceRouteName(newSpaceRouteName);
            int ret = spacePositionMapper.updateById(record);
            if(ret >= 0) {
                // 替换下级子类级联
                QueryWrapper<SpacePosition> spaceExample = new QueryWrapper<>();
                spaceExample.likeRight("space_route", oldSpaceRoute);
                List<SpacePosition> spaceList = spacePositionMapper.selectList(spaceExample);
                for(SpacePosition spaceItem : spaceList) {
                    String itemRoute = spaceItem.getSpaceRoute();
                    String itemRouteName = spaceItem.getSpaceRouteName();
                    itemRoute = itemRoute.replace(oldSpaceRoute, newSpaceRoute);
                    itemRouteName = itemRouteName.replace(oldSpaceRouteName, newSpaceRouteName);
                    spaceItem.setSpaceRoute(itemRoute);
                    spaceItem.setSpaceRouteName(itemRouteName);
                    spacePositionMapper.updateById(spaceItem);
                }

                // 替换设备级联
                QueryWrapper<Device> devExample = new QueryWrapper<>();
                devExample.likeRight("space_route", oldSpaceRoute);
                List<Device> devList = deviceMapper.selectList(devExample);
                for(Device device : devList) {
                    String devRoute = device.getSpaceRoute();
                    String devRouteName = device.getSpaceRouteName();
                    devRoute = devRoute.replace(oldSpaceRoute, newSpaceRoute);
                    devRouteName = devRouteName.replace(oldSpaceRouteName, newSpaceRouteName);
                    device.setSpaceRoute(devRoute);
                    device.setSpaceRouteName(devRouteName);
                    deviceMapper.updateById(device);
                }
            }
        } else {
            spacePositionMapper.updateById(record);
        }
    }

    /**
     * 修改排序
     * @param spaceId 空间位置ID
     * @param orderNumber 排序值
     */
    public void updateOrder(int spaceId, int orderNumber) {
        SpacePosition record = find(spaceId);
        record.setOrderNum(orderNumber);
        spacePositionMapper.updateById(record);
    }



    /**
     * 删除空间位置 (删除前需验证是否存在产品)
     * @param spaceId 空间位置ID
     * @return 删除数量
     */
    public int del(int spaceId) {
        deleteByDevice(spaceId);
        int count = spacePositionMapper.deleteById(spaceId);

        String key = ":" + spaceId + ":";
        QueryWrapper<SpacePosition> example = new QueryWrapper<>();
        example.like("space_route", key);
        spacePositionMapper.delete(example);

        return count;
    }

    /**
     * 批量删除空间位置 (删除前需验证是否存在用户)
     * @param idList 空间位置ID列表
     * @return 删除数量
     */
    public int delArray(List<Integer> idList) {
        deleteArrayByDevice(idList);
        int count = deviceMapper.deleteBatchIds(idList);
        for (int id : idList) {
            String key = ":" + id + ":";

            QueryWrapper<SpacePosition> example = new QueryWrapper<>();
            example.like("space_route", key);
            spacePositionMapper.delete(example);
        }
        return count;
    }

    private void deleteByDevice(int spaceId) {
        String key = ":" + spaceId + ":";

        QueryWrapper<Device> example = new QueryWrapper<>();
        example.like("space_route", key);
        deviceMapper.delete(example);
    }

    private void deleteArrayByDevice(List<Integer> idList) {
        for (int spaceId : idList) {
            deleteByDevice(spaceId);
        }
    }
}
