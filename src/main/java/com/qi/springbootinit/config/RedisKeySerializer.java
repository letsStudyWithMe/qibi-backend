package com.qi.springbootinit.config;

import org.springframework.data.redis.serializer.RedisSerializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
 
/**
 * 为redis key 统一加上前缀
 */
public class RedisKeySerializer implements RedisSerializer<String> {
 
    /**
     * 编码格式
     */
    private final Charset charset;
 
    /**
     * 前缀
     */
    private final String PREFIX_KEY = "qibi:";
 
    public RedisKeySerializer() {
        this(StandardCharsets.UTF_8);
    }
 
    public RedisKeySerializer(Charset charset) {
        this.charset = charset;
    }
 
    /**
     * 反序列化
     */
    @Override
    public String deserialize(byte[] bytes) {
        String key = new String(bytes, charset);
        return key.replace(PREFIX_KEY, "");
    }
 
    /**
     * 序列化
     */
    @Override
    public byte[] serialize(String key) {
        key = PREFIX_KEY + key;
        return key.getBytes(charset);
    }
}