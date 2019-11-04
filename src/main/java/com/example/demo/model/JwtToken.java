package com.example.demo.model;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * Description:
 *
 * @author yangfl
 * @date 2019年10月29日 10:28
 * Version 1.0
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
