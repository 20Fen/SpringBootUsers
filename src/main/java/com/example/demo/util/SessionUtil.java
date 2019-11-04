package com.example.demo.util;

import com.example.demo.model.User;
import com.example.magic.Constant;
import com.google.common.collect.Sets;
import org.apache.shiro.SecurityUtils;

import java.util.Set;

/**
 * Description:
 *
 * @author yangfl
 * @date 2019年10月28日 16:23
 * Version 1.0
 */
public class SessionUtil {
    /**
     * 获取当前登录用户
     *
     * @return
     */
    public User getUser() {
        String token = SecurityUtils.getSubject().getPrincipal().toString();
        String account = JwtUtil.getClaim(token, Constant.JWT_ACCOUNT);
        String sid = JwtUtil.getClaim(token, Constant.JWT_TID);
        return JedisUtil.getSession(account);
    }

    /**
     * 按account清除redis缓存
     *
     * @param account
     */
    public void clearCache(String account) {
        Set<String> keys = Sets.newHashSet();
        // 模糊会话相关的key
        keys.addAll(JedisUtil.keys(JedisUtil.getAuthorizationKey(account)));
        keys.addAll(JedisUtil.keys(JedisUtil.getSessionKey(account)));
        keys.addAll(JedisUtil.keys(JedisUtil.getCurrTokenKey(account)));
        keys.addAll(JedisUtil.keys(JedisUtil.getExpiredTokenKey(account)));
        //批量删除缓存
        if (keys.size() > 0) {
            JedisUtil.del(keys.toArray(new String[keys.size()]));
        }
    }

    /**
     * 获取当前登录人的token
     *
     * @return
     */
    public String getToken() {
        return SecurityUtils.getSubject().getPrincipal().toString();
    }
}