package com.example.demo.model;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * Description:AuthenticationToken 用于收集用户提交的身份（如用户名）及凭据（如密码）。
 * Shiro会调用CredentialsMatcher对象的doCredentialsMatch方法对AuthenticationInfo对象和AuthenticationToken进行匹配。
 * 匹配成功则表示主体（Subject）认证成功，否则表示认证失败。
 */
public class JwtToken implements AuthenticationToken {
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;}
}
