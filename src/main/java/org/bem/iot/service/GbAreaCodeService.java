package org.bem.iot.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.bem.iot.mapper.postgresql.GbAreaCodeMapper;
import org.bem.iot.model.general.GbAreaCode;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * GB28181区域编码
 * @author jakybland
 */
@Service
public class GbAreaCodeService {
    @Resource
    GbAreaCodeMapper gbAreaCodeMapper;

    /**
     * 获取树形结构
     * @return 固件数量
     */
    public JSONArray selectTree() {
        QueryWrapper<GbAreaCode> example = new QueryWrapper<>();
        example.eq("level", "province");
        example.orderByAsc("id");
        List<GbAreaCode> provinceList = gbAreaCodeMapper.selectList(example);
        JSONArray arrayList = new JSONArray();
        for (GbAreaCode province : provinceList) {
            String provinceId = province.getId();
            JSONArray subArray = getSubTree(provinceId);

            JSONObject subItem = new JSONObject();
            subItem.put("key",provinceId);
            subItem.put("label", province.getPosition());
            subItem.put("value", provinceId);
            subItem.put("parentId", province.getParentId());
            if(!subArray.isEmpty()) {
                subItem.put("children", subArray);
            }
            arrayList.add(subItem);
        }
        return arrayList;
    }
    private JSONArray getSubTree(String parentId) {
        QueryWrapper<GbAreaCode> example = new QueryWrapper<>();
        example.eq("parent_id", parentId);
        example.orderByAsc("id");
        List<GbAreaCode> childList = gbAreaCodeMapper.selectList(example);
        JSONArray subList = new JSONArray();
        for (GbAreaCode sub : childList) {
            String id = sub.getId();
            JSONArray subArray = getSubTree(id);

            JSONObject subItem = new JSONObject();
            subItem.put("key",id);
            subItem.put("label", sub.getPosition());
            subItem.put("value", id);
            subItem.put("parentId", sub.getParentId());
            if(!subArray.isEmpty()) {
                subItem.put("children", subArray);
            }
            subList.add(subItem);
        }
        return subList;
    }
}
