package com.example.demo.service.impl;

import com.config.AjaxResult;
import com.example.demo.dao.mapper.AuthenticationMapper;
import com.example.demo.model.Session;
import com.example.demo.model.User;
import com.example.demo.model.UserModel;
import com.example.demo.service.AuthenticationService;
import com.example.demo.util.*;
import com.example.magic.Constant;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

/**
 * Description: service层
 */
@Service
@Log4j2
public class AuthenticationServiceImpl implements AuthenticationService {

    @Resource
    private AuthenticationMapper authenticationMapper;

    /**
     * @description  登录
     * @author Y
     * @Param [ userModel httpServletRequest httpServletResponse ]
     * @return com.config.AjaxResult
     */
    @Override
    public AjaxResult login(UserModel userModel, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        AjaxResult result = new AjaxResult();
        String account = userModel.getName();
        User user = authenticationMapper.selectOne(userModel.getId());
        if (null == user) {
            result.addFail("用户名或密码错误");
        } else {
            if (user.getPassword().equals(user.getPassword())) {
                log.info("用户密码:：" + user.getPassword());
                log.info("是否登录" + StringUtils.equals(userModel.getPassword(), user.getPassword()));
                Integer tokenTime = Integer.valueOf(AuthenticationPropUtil.getTokenExpireTime());
                Integer sessionTime = Integer.valueOf(AuthenticationPropUtil.getSessionExpireTime());

                String uuid = JwtUtil.randomUUID();
                String token = JwtUtil.sign(account, tokenTime, uuid);

                // 会话信息
                Session session = new Session();
                BeanUtils.copyProperties(user, session);
                session.setLoginTime(System.currentTimeMillis());
                session.setPassword(null);
                // 缓存话过期时间和token过期时间，各个子系统刷新token时用
                session.setSessionExpireTime(sessionTime);
                session.setTokenExpireTime(tokenTime);
                // 缓存会话信息
                JedisUtil.cacheSession(account, sessionTime, session);
                // 缓存当前使用的token
                JedisUtil.cacheCurrToken(account,uuid,sessionTime);
                // header返回token
                httpServletResponse.setHeader(Constant.JWT_HEADER_KEY, token);
                httpServletResponse.setHeader("Access-Control-Expose-Headers", Constant.JWT_HEADER_KEY);

                result.addSuccess("登录成功");
            } else {
                result.addFail("用户名或密码错误");
            }
        }
        return result;
    }

    /**
     * 退出登录
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    @Override
    public AjaxResult logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        AjaxResult result = new AjaxResult();
        try {
            Subject subject = SecurityUtils.getSubject();
            String account = JwtUtil.getClaim(subject.getPrincipal().toString(), Constant.JWT_ACCOUNT);
            JedisUtil.clearCache(account);
            subject.logout();
            result.addSuccess("操作成功");
        } catch (Exception e) {
            result.addError("退出登录失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * @description 新建用户 修改用户
     * @author Y
     * @Param [ user ]
     * @return java.lang.String
     */
    @Override
    public String createUser(User user) {

        String uuid = UUID.randomUUID().toString();
        String uid = user.getId();
        uuid = StringUtils.isEmpty(user.getId())? uuid : uid;
        String account = user.getAccount();
            User u  =new User();
            u.setName(user.getName());
            String password = MD5.md5PlusSalt(user.getPassword());
            u.setPassword(password);
            u.setAccount(user.getAccount());
            u.setEmail(user.getEmail());
            u.setSex(user.getSex());
            if(StringUtils.isEmpty(uid)){
                u.setId(uuid);
                authenticationMapper.insert(u);
            }else {
                User us = authenticationMapper.selectOne(user.getId());
                if(null != us){
                    if(account.equals(us.getAccount())){
                        u.setId(us.getId());
                        authenticationMapper.updata(u);
                    }
                }
            }
        return account;
    }

    @Override
    public AjaxResult permission(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        AjaxResult result = new AjaxResult();
        try {
            Subject subject = SecurityUtils.getSubject();
            String account = JwtUtil.getClaim(subject.getPrincipal().toString(), Constant.JWT_ACCOUNT);
            Session session = JedisUtil.getSession(account);
            session.setPermissions(getReource(account));
            result.setResults(session);
        } catch (Exception e) {
            result.addError("获取资源权限出错：" + e.getMessage());
        }
        return result;
    }

    @Override
    public User getUser(String account) {
        return null;
    }

    @Override
    public List<String> getRole(String account) {
        return null;
    }

    @Override
    public List<String> getReource(String account) {
        return null;
    }



    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString().replace("-", ""));
    }
}
