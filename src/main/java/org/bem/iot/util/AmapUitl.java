package org.bem.iot.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.bem.iot.entity.amap.Location;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 高德地图工具类
 */
public class AmapUitl {
    /**
     * 获取经纬度信息
     * @param address 地址
     * @param city 城市（选填）
     * @param aMapKey 高德地图密钥
     * @return 返回经纬度信息
     */
    public static Location getGeoLocation(String address, String city, String aMapKey) throws Exception {
        String url = "https://restapi.amap.com/v3/geocode/geo";
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("key", aMapKey);
        paramsMap.put("address", address);
        if(!StrUtil.isEmpty(city)) {
            paramsMap.put("city", city);
        }
        JSONObject jsonObject = WebClientUtil.get(url, paramsMap, JSONObject.class);
        int status = Integer.parseInt(jsonObject.getString("status"));
        if(status == 1) {
            int count = Integer.parseInt(jsonObject.getString("count"));
            if(count > 0) {
                JSONArray geocodes = jsonObject.getJSONArray("geocodes");
                JSONObject geocode = geocodes.getJSONObject(0);
                String location = geocode.getString("location");
                String[] locationArray = location.split(",");
                BigDecimal longitude = new BigDecimal(locationArray[0].trim());
                BigDecimal latitude = new BigDecimal(locationArray[1].trim());

                return new Location(longitude, latitude);
            } else {
                throw new Exception("获取经纬度信息失败,无匹配结果");
            }
        } else {
            String errorCode = jsonObject.getString("info");
            throw new Exception("获取经纬度信息失败,错误码：" + errorCode);
        }

    }

    /**
     * 获取逆地理编码信息
     * @param location 经纬度坐标,经度在前，纬度在后，经纬度间以“,”分割，经纬度小数点后不要超过 6 位
     * @param aMapKey 高德地图密钥
     * @return 返回经纬度信息
     */
    public static String getGeoAddress(String location, String aMapKey) throws Exception {
        String url = "https://restapi.amap.com/v3/geocode/regeo";
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("key", aMapKey);
        paramsMap.put("location", location);
        JSONObject jsonObject = WebClientUtil.get(url, paramsMap, JSONObject.class);
        int status = Integer.parseInt(jsonObject.getString("status"));
        if(status == 1) {
            JSONObject regeocode = jsonObject.getJSONObject("regeocode");
            return regeocode.getString("formatted_address");
        } else {
            String errorCode = jsonObject.getString("info");
            throw new Exception("获取经纬度信息失败,错误码：" + errorCode);
        }

    }
}
