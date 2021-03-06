package com.example.demo.service.impl;

import com.example.demo.exception.CustomException;
import com.example.demo.model.JwtToken;
import com.example.demo.util.AuthorityApiUtil;
import com.example.magic.Constant;
import com.example.demo.util.JedisUtil;
import com.example.demo.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Service;

/**
 * Description:Realm是Shiro授权管理，重写父类中的doGetAuthorizationInfo（权限相关）、doGetAuthenticationInfo（身份认证）这两个方法
 */
@Service
public class UserRealm extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof JwtToken;
    }

    /**
     * 授权逻辑，获取用户拥有的角色及资源，做权限校验（如注解@RequiresPermissions）且缓存中无权限信息时执行
     */
    @Override
    public AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        String account = JwtUtil.getClaim(principalCollection.toString(), Constant.JWT_ACCOUNT);
        // 设置用户的角色
        simpleAuthorizationInfo.addRoles(AuthorityApiUtil.getRole(account));
        // 设置用户的权限
        simpleAuthorizationInfo.addStringPermissions(AuthorityApiUtil.getResource(account));
        return simpleAuthorizationInfo;
    }

    /**
     * token和用户验证，做认证时调用（如注解@RequiresAuthentication或者getSubject().login()）时执行
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials();
        String account = JwtUtil.getClaim(token, Constant.JWT_ACCOUNT);
        String tid = JwtUtil.getClaim(token, Constant.JWT_TID);
        //token自定义信息完整性检查
        if (StringUtils.isBlank(account) ||  StringUtils.isBlank(tid)) {
            throw new AuthenticationException("token信息不完整");
        }
        boolean cached = JedisUtil.exists(JedisUtil.getSessionKey(account));
        // 有会话缓存且token验证通过，则shiro认证通过，token验证不通过会抛异常，在JwtFilter.isAccessAllowed中处理
            if (cached && JwtUtil.verify(token)) {
                return new SimpleAuthenticationInfo(token, token, "userRealm");
            }
        // 用户删除会清掉redis缓存，用户不存在放在最后检查，减少接口调用
        if (AuthorityApiUtil.getUser(account) == null) {
            throw new AuthenticationException("用户不存在");
        }
        // 会话缓存不存在
        throw new AuthenticationException("会话已过期");
    }
}
