package com.example.demo.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Description: properties工具类
 */
@Component
public class AuthenticationPropUtil {

//    token时间
    private static String tokenExpireTime;
//    session时间
    private static String sessionExpireTime;

    /**
     * 权限管理application.properties中的值，(tokenExpireTime:)默认值为空，防止取其它子系统无此配置报错
     *
     * @param tokenExpireTime
     */
    @Value("${auth.tokenExpireTime:}")
    public void setTokenExpireTime(String tokenExpireTime) {
        AuthenticationPropUtil.tokenExpireTime = tokenExpireTime;
    }

    @Value("${auth.sessionExpireTime:}")
    public void setSessionExpireTime(String sessionExpireTime) {
        AuthenticationPropUtil.sessionExpireTime = sessionExpireTime;
    }


    private static final Properties PROP = new Properties();

    static {
        try {
            AuthenticationPropUtil.readProperties("authentication.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据key读取对应的value
     *
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        return PROP.getProperty(key);
    }

    /**
     * 读取配置文件
     *
     * @param fileName
     */
    public static void readProperties(String fileName) throws Exception {
        try (InputStream in = AuthenticationPropUtil.class.getResourceAsStream("/" + fileName)) {
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            PROP.load(bf);
        } catch (IOException e) {
            throw new Exception("读取配置文件出现IOException异常:" + e.getMessage());
        }
    }

    /**
     * 根据key读取对应的value
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getProperty(String key, String defaultValue) {
        String value = PROP.getProperty(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 获取单个token过期时间，权限管理有配置过则使用，否则使用authentication.properties中的配置
     *
     * @return
     */
    public static String getTokenExpireTime() {
        // 权限管理配置优先
        if (StringUtils.isNotEmpty(AuthenticationPropUtil.tokenExpireTime)) {
            return AuthenticationPropUtil.tokenExpireTime;
        }
        return getProperty("auth.tokenExpireTime", "600");
    }

    /**
     * 获取会话过期时间
     *
     * @return
     */
    public static String getSessionExpireTime() {
        // 权限管理配置优先
        if (StringUtils.isNotEmpty(AuthenticationPropUtil.sessionExpireTime)) {
            return AuthenticationPropUtil.sessionExpireTime;
        }
        return getProperty("auth.sessionExpireTime", "1800");
    }

    /**
     * 获取url过滤规则
     *
     * @return
     */
    public static Map<String, String> getAuthFilters() {
        Map<String, String> filters = new LinkedHashMap<String, String>();
        PROP.stringPropertyNames().stream().forEach(key -> {
                        if (key.startsWith("auth.filters[")) {
                            String filter = PROP.getProperty(key);
                            if (StringUtils.isNotBlank(filter)) {
                                String[] _filters = filter.split("#");
                    // #后面的规则不配默认为anon白名单放行
                    ArrayUtils.add(_filters, "anon");
                    filters.put(_filters[0], _filters[1]);
                }
            }
        });
        return filters;
    }
}
