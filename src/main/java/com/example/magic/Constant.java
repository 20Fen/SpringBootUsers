package com.example.magic;

/**
 * Description: 常量
 */
public class Constant {

    /**
     * redis-OK
     */
    public static final String OK = "OK";

    /**
     * 授权信息缓存key前缀
     */
    public static final String PREFIX_SHIRO_CACHE_AUTHORIZATION = "shiro:authorization:";

    /**
     * 会话ID缓存key前缀
     */
    public static final String PREFIX_SHIRO_CACHE_SESSION = "shiro:session:";

    /**
     * 上次过期的token缓存key前缀
     */
    public static final String PREFIX_SHIRO_CACHE_EXPIREDTOKEN = "shiro:expiredToken:";

    /**
     * 当前在用的token缓存key前缀
     */
    public static final String PREFIX_SHIRO_CACHE_CURRTOKEN = "shiro:currToken:";

    /**
     * jwt-account:
     */
    public static final String JWT_ACCOUNT = "account";

    /**
     * jwt-tid:
     */
    public static final String JWT_TID = "tid";

    /**
     * header中的token key
     */
    public static final String JWT_HEADER_KEY = "Authorization";
}
