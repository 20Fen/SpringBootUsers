package com.example.demo.util;


import com.example.magic.Constant;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.util.SerializationUtils;

import java.util.*;

/**
 * Description:
 *
 * @author yangfl
 * @date 2019年10月29日 15:27
 * Version 1.0
 */
public class CustomCache<K, V> implements Cache<K, V> {

    /**
     * 传入的key为token，得到会话sid，redis存储key为shiro:authorization:{sid},
     */
    private String getKey(Object key) {
        String account = JwtUtil.getClaim(key.toString(), Constant.JWT_ACCOUNT);
        return JedisUtil.getAuthorizationKey(account);
    }

    /**
     * 获取缓存
     */
    @Override
    public Object get(Object key) throws CacheException {
        String redisKey = this.getKey(key);
        return JedisUtil.getValue(redisKey);
    }

    /**
     * 保存缓存
     */
    @Override
    public Object put(Object key, Object value) throws CacheException {
        return JedisUtil.setValue(this.getKey(key), value, Integer.parseInt(AuthenticationPropUtil.getSessionExpireTime()));
    }

    /**
     * 移除缓存
     */
    @Override
    public Object remove(Object key) throws CacheException {
        return JedisUtil.del(this.getKey(key));
    }

    /**
     * 清空所有缓存
     */
    @Override
    public void clear() throws CacheException {
        Objects.requireNonNull(JedisUtil.getJedis()).flushDB();
    }

    /**
     * 缓存的个数
     */
    @Override
    public int size() {
        Long size = Objects.requireNonNull(JedisUtil.getJedis()).dbSize();
        return size.intValue();
    }

    /**
     * 获取所有的key
     */
    @Override
    public Set keys() {
        Set<byte[]> keys = Objects.requireNonNull(JedisUtil.getJedis()).keys("*".getBytes());
        Set<Object> set = new HashSet<>();
        for (byte[] bs : keys) {
            set.add(SerializationUtils.deserialize(bs));
        }
        return set;
    }

    /**
     * 获取所有的value
     */
    @Override
    public Collection values() {
        Set keys = this.keys();
        List<Object> values = new ArrayList<Object>();
        for (Object key : keys) {
            values.add(JedisUtil.getValue(this.getKey(key)));
        }
        return values;
    }
}

