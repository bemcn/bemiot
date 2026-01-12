package org.bem.iot.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Redis使用FastJson序列化
 * @author Administrator
 *
 * @param <T> 类泛型
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {
    /**
     * 指定UTF-8
     */
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * 序列化的数据类
     */
    private final Class<T> classSource;
    
    /**
     * FastJson Redis序列化
     * @param classSource 序列化的数据类
     */
    public FastJsonRedisSerializer(Class<T> classSource) {
        super();
        this.classSource = classSource;
    }
 
    /**
     * 序列化为byte[]字节流
     */
    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (null == t) {
            return new byte[0];
        }
        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }
 
    /**
     * 反序列化字节流
     */
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (null == bytes || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        return JSON.parseObject(str, classSource);
    }
}
