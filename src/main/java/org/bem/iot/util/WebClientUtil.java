package org.bem.iot.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebClient工具类
 */
public class WebClientUtil {
    /**
     * WebClient发送GET请求
     * @param url 请求地址
     * @param paramsMap 请求参数
     * @param responseClass 响应数据的数据类型
     * @return 响应数据
     */
    public static <T> T get(String url, Map<String, String> paramsMap, Class<T> responseClass) {
        return get(url, new HashMap<>(), paramsMap, responseClass);
    }

    /**
     * WebClient发送GET请求
     * @param url 请求地址
     * @param headsMap 请求头
     * @param paramsMap 请求参数
     * @param responseClass 响应数据的数据类型
     * @return 响应数据
     */
    public static <T> T get(String url, Map<String, String> headsMap, Map<String, String> paramsMap, Class<T> responseClass) {
        List<NameValuePair> nameValuePairs;
        if (paramsMap != null && !paramsMap.isEmpty()) {
            nameValuePairs = new ArrayList<>(paramsMap.size());
            paramsMap.forEach((k, v) -> nameValuePairs.add(new BasicNameValuePair(k, v)));
        } else {
            nameValuePairs = new ArrayList<>();
        }

        URI uri;
        try {
            uri = new URIBuilder(url)
                    .setCharset(StandardCharsets.UTF_8)
                    .addParameters(nameValuePairs)
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage());
        }

        WebClient.Builder webClientBuilder = WebClient.builder();
        if (headsMap != null && !headsMap.isEmpty()) {
            headsMap.forEach(webClientBuilder::defaultHeader);
        }

        // 根据期望的响应类型进行处理
        if (responseClass == JSONObject.class || responseClass == Object.class) {
            String response = webClientBuilder
                    .build()
                    .method(HttpMethod.GET)
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            if (response == null || response.trim().isEmpty()) {
                return (T) new JSONObject();
            }
            
            // 清理响应字符串，移除可能的BOM和空白字符
            response = response.trim();
            if (response.startsWith("\ufeff")) { // 移除BOM
                response = response.substring(1);
            }
            
            try {
                return (T) JSON.parseObject(response);
            } catch (Exception e) {
                throw new RuntimeException("JSON解析错误: " + e.getMessage() + ", 响应内容: " + response, e);
            }
        } else if (responseClass == JSONArray.class) {
            String response = webClientBuilder
                    .build()
                    .method(HttpMethod.GET)
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            if (response == null || response.trim().isEmpty()) {
                return (T) new JSONArray();
            }
            
            // 清理响应字符串，移除可能的BOM和空白字符
            response = response.trim();
            if (response.startsWith("\ufeff")) { // 移除BOM
                response = response.substring(1);
            }
            
            try {
                return (T) JSON.parseArray(response);
            } catch (Exception e) {
                throw new RuntimeException("JSON解析错误: " + e.getMessage() + ", 响应内容: " + response, e);
            }
        } else {
            return webClientBuilder
                    .build()
                    .method(HttpMethod.GET)
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(responseClass)
                    .block();
        }
    }

    /**
     * 发送POST请求（form表单）
     * @param url 请求地址
     * @param paramsMap 请求参数
     * @param responseClass 响应数据的数据类型
     * @return 响应数据
     */
    public static <T> T postForm(String url, Map<String, Object> paramsMap, Class<T> responseClass) {
        return postForm(url, new HashMap<>(), paramsMap, responseClass);
    }

    /**
     * 发送POST请求（form表单）
     * @param url 请求地址
     * @param headsMap 请求头
     * @param paramsMap 请求参数
     * @param responseClass 响应数据的数据类型
     * @return 响应数据
     */
    public static <T> T postForm(String url, Map<String, String> headsMap, Map<String, Object> paramsMap, Class<T> responseClass) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        if (paramsMap != null && !paramsMap.isEmpty()) {
            paramsMap.forEach(params::add);
        }

        Map<String, String> finalHeadsMap = new HashMap<>();
        if (headsMap != null && !headsMap.isEmpty()) {
            finalHeadsMap.putAll(headsMap);
        }
        finalHeadsMap.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);


        // 根据期望的响应类型进行处理
        if (responseClass == JSONObject.class || responseClass == Object.class) {
            String response = WebClient
                    .create()
                    .method(HttpMethod.POST)
                    .uri(url)
                    .headers(httpHeaders -> finalHeadsMap.forEach(httpHeaders::set))
                    .bodyValue(params)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            if (response == null || response.trim().isEmpty()) {
                return (T) new JSONObject();
            }
            
            // 清理响应字符串，移除可能的BOM和空白字符
            response = response.trim();
            if (response.startsWith("\ufeff")) { // 移除BOM
                response = response.substring(1);
            }
            
            try {
                return (T) JSON.parseObject(response);
            } catch (Exception e) {
                throw new RuntimeException("JSON解析错误: " + e.getMessage() + ", 响应内容: " + response, e);
            }
        } else if (responseClass == JSONArray.class) {
            String response = WebClient
                    .create()
                    .method(HttpMethod.POST)
                    .uri(url)
                    .headers(httpHeaders -> finalHeadsMap.forEach(httpHeaders::set))
                    .bodyValue(params)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            if (response == null || response.trim().isEmpty()) {
                return (T) new JSONArray();
            }
            
            // 清理响应字符串，移除可能的BOM和空白字符
            response = response.trim();
            if (response.startsWith("\ufeff")) { // 移除BOM
                response = response.substring(1);
            }
            
            try {
                return (T) JSON.parseArray(response);
            } catch (Exception e) {
                throw new RuntimeException("JSON解析错误: " + e.getMessage() + ", 响应内容: " + response, e);
            }
        } else {
            return WebClient
                    .create()
                    .method(HttpMethod.POST)
                    .uri(url)
                    .headers(httpHeaders -> finalHeadsMap.forEach(httpHeaders::set))
                    .bodyValue(params)
                    .retrieve()
                    .bodyToMono(responseClass)
                    .block();
        }
    }

    /**
     * 发送post请求（JSON数据）
     * @param url 请求地址
     * @param params 请求入参
     * @param responseClass 响应数据数据类型
     * @return 响应数据
     */
    public static <T> T postJson(String url, Object params, Class<T> responseClass) {
        return postJson(url, new HashMap<>(), params, responseClass);
    }

    /**
     * 发送post请求（JSON数据）
     * @param url 请求地址
     * @param headsMap 请求头
     * @param params 请求入参
     * @param responseClass 响应数据数据类型
     * @return 响应数据
     */
    public static <T> T postJson(String url, Map<String, String> headsMap, Object params, Class<T> responseClass) {
        String paramsJson = convertToJson(params);

        Map<String, String> finalHeadsMap = new HashMap<>();
        if (headsMap != null && !headsMap.isEmpty()) {
            finalHeadsMap.putAll(headsMap);
        }
        finalHeadsMap.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        // 根据期望的响应类型进行处理
        if (responseClass == JSONObject.class || responseClass == Object.class) {
            String response = WebClient
                    .builder()
                    .build()
                    .method(HttpMethod.POST)
                    .uri(url)
                    .headers(httpHeaders -> finalHeadsMap.forEach(httpHeaders::set))
                    .bodyValue(paramsJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            if (response == null || response.trim().isEmpty()) {
                return (T) new JSONObject();
            }
            
            // 清理响应字符串，移除可能的BOM和空白字符
            response = response.trim();
            if (response.startsWith("\ufeff")) { // 移除BOM
                response = response.substring(1);
            }
            
            try {
                return (T) JSON.parseObject(response);
            } catch (Exception e) {
                throw new RuntimeException("JSON解析错误: " + e.getMessage() + ", 响应内容: " + response, e);
            }
        } else if (responseClass == JSONArray.class) {
            String response = WebClient
                    .builder()
                    .build()
                    .method(HttpMethod.POST)
                    .uri(url)
                    .headers(httpHeaders -> finalHeadsMap.forEach(httpHeaders::set))
                    .bodyValue(paramsJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            if (response == null || response.trim().isEmpty()) {
                return (T) new JSONArray();
            }
            
            // 清理响应字符串，移除可能的BOM和空白字符
            response = response.trim();
            if (response.startsWith("\ufeff")) { // 移除BOM
                response = response.substring(1);
            }
            
            try {
                return (T) JSON.parseArray(response);
            } catch (Exception e) {
                throw new RuntimeException("JSON解析错误: " + e.getMessage() + ", 响应内容: " + response, e);
            }
        } else {
            return WebClient
                    .builder()
                    .build()
                    .method(HttpMethod.POST)
                    .uri(url)
                    .headers(httpHeaders -> finalHeadsMap.forEach(httpHeaders::set))
                    .bodyValue(paramsJson)
                    .retrieve()
                    .bodyToMono(responseClass)
                    .block();
        }
    }

    /**
     * 发送PUT请求（JSON数据）
     * @param url 请求地址
     * @param params 请求入参
     * @param responseClass 响应数据数据类型
     * @return 响应数据
     */
    public static <T> T putJson(String url, Object params, Class<T> responseClass) {
        return putJson(url, new HashMap<>(), params, responseClass);
    }

    /**
     * 发送PUT请求（JSON数据）
     * @param url 请求地址
     * @param headsMap 请求头
     * @param params 请求入参
     * @param responseClass 响应数据数据类型
     * @return 响应数据
     */
    public static <T> T putJson(String url, Map<String, String> headsMap, Object params, Class<T> responseClass) {
        String paramsJson = convertToJson(params);

        Map<String, String> finalHeadsMap = new HashMap<>();
        if (headsMap != null && !headsMap.isEmpty()) {
            finalHeadsMap.putAll(headsMap);
        }
        finalHeadsMap.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        // 根据期望的响应类型进行处理
        if (responseClass == JSONObject.class || responseClass == Object.class) {
            String response = WebClient
                    .builder()
                    .build()
                    .method(HttpMethod.PUT)
                    .uri(url)
                    .headers(httpHeaders -> finalHeadsMap.forEach(httpHeaders::set))
                    .bodyValue(paramsJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            if (response == null || response.trim().isEmpty()) {
                return (T) new JSONObject();
            }
            
            // 清理响应字符串，移除可能的BOM和空白字符
            response = response.trim();
            if (response.startsWith("\ufeff")) { // 移除BOM
                response = response.substring(1);
            }
            
            try {
                return (T) JSON.parseObject(response);
            } catch (Exception e) {
                throw new RuntimeException("JSON解析错误: " + e.getMessage() + ", 响应内容: " + response, e);
            }
        } else if (responseClass == JSONArray.class) {
            String response = WebClient
                    .builder()
                    .build()
                    .method(HttpMethod.PUT)
                    .uri(url)
                    .headers(httpHeaders -> finalHeadsMap.forEach(httpHeaders::set))
                    .bodyValue(paramsJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            if (response == null || response.trim().isEmpty()) {
                return (T) new JSONArray();
            }
            
            // 清理响应字符串，移除可能的BOM和空白字符
            response = response.trim();
            if (response.startsWith("\ufeff")) { // 移除BOM
                response = response.substring(1);
            }
            
            try {
                return (T) JSON.parseArray(response);
            } catch (Exception e) {
                throw new RuntimeException("JSON解析错误: " + e.getMessage() + ", 响应内容: " + response, e);
            }
        } else {
            return WebClient
                    .builder()
                    .build()
                    .method(HttpMethod.PUT)
                    .uri(url)
                    .headers(httpHeaders -> finalHeadsMap.forEach(httpHeaders::set))
                    .bodyValue(paramsJson)
                    .retrieve()
                    .bodyToMono(responseClass)
                    .block();
        }
    }

    /**
     * 发送DELETE请求
     * @param url 请求地址
     * @param paramsMap 请求参数
     * @param responseClass 响应数据的数据类型
     * @return 响应数据
     */
    public static <T> T delete(String url, Map<String, String> paramsMap, Class<T> responseClass) {
        return delete(url, new HashMap<>(), paramsMap, responseClass);
    }

    /**
     * 发送DELETE请求
     * @param url 请求地址
     * @param headsMap 请求头
     * @param paramsMap 请求参数
     * @param responseClass 响应数据的数据类型
     * @return 响应数据
     */
    public static <T> T delete(String url, Map<String, String> headsMap, Map<String, String> paramsMap, Class<T> responseClass) {
        List<NameValuePair> nameValuePairs;
        if (paramsMap != null && !paramsMap.isEmpty()) {
            nameValuePairs = new ArrayList<>(paramsMap.size());
            paramsMap.forEach((k, v) -> nameValuePairs.add(new BasicNameValuePair(k, v)));
        } else {
            nameValuePairs = new ArrayList<>();
        }

        URI uri;
        try {
            uri = new URIBuilder(url)
                    .setCharset(StandardCharsets.UTF_8)
                    .addParameters(nameValuePairs)
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage());
        }

        Map<String, String> finalHeadsMap = new HashMap<>();
        if (headsMap != null && !headsMap.isEmpty()) {
            finalHeadsMap.putAll(headsMap);
        }

        // 根据期望的响应类型进行处理
        if (responseClass == JSONObject.class || responseClass == Object.class) {
            String response = WebClient
                    .builder()
                    .build()
                    .method(HttpMethod.DELETE)
                    .uri(uri)
                    .headers(httpHeaders -> finalHeadsMap.forEach(httpHeaders::set))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            if (response == null || response.trim().isEmpty()) {
                return (T) new JSONObject();
            }
            
            // 清理响应字符串，移除可能的BOM和空白字符
            response = response.trim();
            if (response.startsWith("\ufeff")) { // 移除BOM
                response = response.substring(1);
            }
            
            try {
                return (T) JSON.parseObject(response);
            } catch (Exception e) {
                throw new RuntimeException("JSON解析错误: " + e.getMessage() + ", 响应内容: " + response, e);
            }
        } else if (responseClass == JSONArray.class) {
            String response = WebClient
                    .builder()
                    .build()
                    .method(HttpMethod.DELETE)
                    .uri(uri)
                    .headers(httpHeaders -> finalHeadsMap.forEach(httpHeaders::set))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            if (response == null || response.trim().isEmpty()) {
                return (T) new JSONArray();
            }
            
            // 清理响应字符串，移除可能的BOM和空白字符
            response = response.trim();
            if (response.startsWith("\ufeff")) { // 移除BOM
                response = response.substring(1);
            }
            
            try {
                return (T) JSON.parseArray(response);
            } catch (Exception e) {
                throw new RuntimeException("JSON解析错误: " + e.getMessage() + ", 响应内容: " + response, e);
            }
        } else {
            return WebClient
                    .builder()
                    .build()
                    .method(HttpMethod.DELETE)
                    .uri(uri)
                    .headers(httpHeaders -> finalHeadsMap.forEach(httpHeaders::set))
                    .retrieve()
                    .bodyToMono(responseClass)
                    .block();
        }
    }

    /**
     * 将对象转换为JSON字符串
     * @param params 参数对象
     * @return JSON字符串
     */
    private static String convertToJson(Object params) {
        if (params instanceof JSONObject) {
            return ((JSONObject) params).toJSONString();
        } else {
            return JSON.toJSONString(params);
        }
    }

    public static void main(String[] args) {
        String url = "https://restapi.amap.com/v3/geocode/regeo";
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("key", "2b65b7926a006d8c4bb32f9ecb1733e5");
        paramsMap.put("location", "119.216748,26.040850");
        JSONObject jsonObject = get(url, paramsMap, JSONObject.class);
        System.out.println(jsonObject.toJSONString());
    }
}