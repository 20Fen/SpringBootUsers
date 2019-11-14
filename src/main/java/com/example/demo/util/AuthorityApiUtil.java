package com.example.demo.util;

import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Description:
 */
@Component
public class AuthorityApiUtil {

    private static String authorityUrl;

    /**
     * 配置文件application.properties中的权限认证管理接口地址
     *
     * @param authorityUrl
     */
    @Value("${auth.authority.url}")
    public void setAuthorityUrl(String authorityUrl) {
        AuthorityApiUtil.authorityUrl = authorityUrl;
    }

    /**
     * 按account查询用户信息
     * RestTemplate既包含rpc 有包含 http
     * @return
     */
    public static User getUser(String account) {
        return RestTemplateUtils.getInstance().getForObject(authorityUrl + "/account/user?account=" + account, User.class);
    }

    /**
     * 按account查询用户角色
     *RestTemplate既包含rpc 有包含 http
     * @return
     */
    public static List<String> getRole(String account) {
        String[] roles = RestTemplateUtils.getInstance().getForObject(authorityUrl + "/account/role?account=" + account, String[].class, account);
        return Arrays.asList(roles);
    }

    /**
     * 按account查询用户有权限的资源
     *RestTemplate既包含rpc 有包含 http
     * @return
     */
    public static List<String> getResource(String account) {
        String[] resources = RestTemplateUtils.getInstance().getForObject(authorityUrl + "/account/resource?account=" + account, String[].class, account);
        return Arrays.asList(resources);
    }
}
