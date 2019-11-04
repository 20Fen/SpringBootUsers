package com.example.demo.util;

import com.example.demo.model.Session;
import com.example.magic.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * Description: redis操作工具类
 */
@Component
public class JedisUtil {

    private static JedisPool jedisPool;

    @Autowired
    public void setJedisPool(JedisPool jedisPool) {
        JedisUtil.jedisPool = jedisPool;
    }

    /**
     * 获取Jedis实例
     *
     * @return
     */
    public static Jedis getJedis() {
        if (jedisPool != null) {
            return jedisPool.getResource();
        } else {
            return null;
        }
    }

    /**
     * 按key获取value
     *
     * @param key
     * @return
     */
    public static Object getValue(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return SerializationUtils.deserialize(jedis.get(key.getBytes()));
        }
    }

    /**
     * 按key设置value
     *
     * @param key
     * @param value
     * @return
     */
    public static String setValue(String key, Object value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key.getBytes(), SerializationUtils.serialize(value));
        }
    }

    /**
     * 按key设置value,expiretime(秒)
     *
     * @param key
     * @param value
     * @param expiretime
     * @return
     */
    public static String setValue(String key, Object value, int expiretime) {
        try (Jedis jedis = jedisPool.getResource()) {
            String result = jedis.set(key.getBytes(), SerializationUtils.serialize(value));
            if (Constant.OK.equals(result)) {
                jedis.expire(key.getBytes(), expiretime);
            }
            return result;
        }
    }

    /**
     * 按key刪除
     *
     * @param key
     * @return
     */
    public static Long del(String... key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(key);
        }
    }

    /**
     * 检查key是否存在
     *
     * @param key
     * @return
     */
    public static Boolean exists(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key.getBytes());
        }
    }

    /**
     * 按key重新过期时间
     *
     * @param key
     * @return
     */
    public static Long expire(String key, int seconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expire(key.getBytes(), seconds);
        }
    }

    /**
     * 按key模糊查询keys
     *
     * @param key
     * @return
     */
    public static Set<String> keys(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys(key);
        }
    }

    public static String getAuthorizationKey(String account) {
        return Constant.PREFIX_SHIRO_CACHE_AUTHORIZATION + account;
    }

    public static String getSessionKey(String account) {
        return Constant.PREFIX_SHIRO_CACHE_SESSION + account ;
    }

    public static String getCurrTokenKey(String account) {
        return Constant.PREFIX_SHIRO_CACHE_CURRTOKEN + account;
    }

    public static String getExpiredTokenKey(String account) {
        return Constant.PREFIX_SHIRO_CACHE_EXPIREDTOKEN + account;
    }

    /**
     * 清除账号为account的缓存
     *
     * @param account
     */
    public static void clearCache(String account) {
        JedisUtil.del(getAuthorizationKey(account), getSessionKey(account), getCurrTokenKey(account), getExpiredTokenKey(account));
    }

    /**
     * 缓存会话信息
     *
     * @param account
     * @param
     * @param expiretime
     * @param session
     */
    public static void cacheSession(String account,  int expiretime, Session session) {
        JedisUtil.setValue(getSessionKey(account), session, expiretime);
    }

    /**
     * 按会话id获取会话信息
     *
     * @param account
     * @return
     */
    public static Session getSession(String account) {
        return (Session) JedisUtil.getValue(getSessionKey(account));
    }

    /**
     * 缓存过期的token，设置60s过期，解决并发刷新token的问题
     *
     * @param account
     * @param expiredToken
     */
    public static void cacheExpiredToken(String account, String expiredToken) {
        JedisUtil.setValue(getExpiredTokenKey(account), expiredToken, 60);
    }

    /**
     * 缓存当前生效的token
     *  @param account
     * @param tid
     * @param expiretime
     */
    public static void cacheCurrToken(String account, String tid, int expiretime) {
        JedisUtil.setValue(getCurrTokenKey(account) ,tid,expiretime);
    }
}
