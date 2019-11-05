package com.example.demo.jwt;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.config.AjaxResult;
import com.example.demo.model.JwtToken;
import com.example.demo.model.Session;
import com.example.magic.Constant;
import com.example.demo.util.JedisUtil;
import com.example.demo.util.JwtUtil;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Description:  jwt拦截认证
 */
@Log4j2
public class JwtFilter extends BasicHttpAuthenticationFilter {
    /**
     * 判定白名单之外的全部request是否允许访问
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (this.isLoginAttempt(request, response)) {
            // 通用token放行
            if (isGenericToken(request)) {
                return true;
            }
            try {
                this.executeLogin(request, response);
            } catch (Exception e) {
                // token已过期，尝试token刷新
                if (e.getCause() instanceof TokenExpiredException) {
                    try {
                        if (this.tryRefreshToken(request, response)) {
                            return true;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                // 返回401
                try {
                    this.response401(response, logAuthenticationException(request, e));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        } else {
            // 没有携带token，拦截
            // 通过注解拦截登录权限此处return true
            // 返回401
            try {
                this.response401(response, "未登录系统，无权访问");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 去除executeLogin方法，防止循环调用doGetAuthenticationInfo
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        this.sendChallenge(request, response);
        return false;
    }

    /**
     * header中是否有token
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        return this.getAuthzHeader(request) != null;
    }

    /**
     * 是通用token
     */
    protected boolean isGenericToken(ServletRequest request) {
        return JwtUtil.GENERIC_TOKEN.equals(this.getAuthzHeader(request));
    }

    /**
     * 进行token登录认证
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        JwtToken token = new JwtToken(this.getAuthzHeader(request));
        // 提交给UserRealm进行认证，如果错误会抛出异常并被捕获
        this.getSubject(request, response).login(token);
        // 如果没有抛出异常则登录成功
        return true;
    }

    /**
     * 前后端请求，跨域携带token处理
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求，直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    /**
     * token刷新，缓存中存在会话缓存，则会话未过期，刷新token，按新的token继续访问
     */
    private boolean tryRefreshToken(ServletRequest request, ServletResponse response) throws Exception {
        String token = this.getAuthzHeader(request);
        String account = JwtUtil.getClaim(token, Constant.JWT_ACCOUNT);
        String sessionKey = JedisUtil.getSessionKey(account);
        boolean sessionNotExpired = JedisUtil.exists(sessionKey);
        // 会话未过期
        if (sessionNotExpired) {
            // 过期token
            String expiredToken = (String) JedisUtil.getValue(JedisUtil.getExpiredTokenKey(account));
            // 放行并发来的老token，在60s内可以继续使用，前端并发ajax正常情况不会超过60s
            if (StringUtils.equals(token, expiredToken)) {
                return true;
            }

            //当前传入的token
            String tid = JwtUtil.getClaim(token, Constant.JWT_TID);
            //被缓存的当前使用的token tid
            String currToken = (String) JedisUtil.getValue(JedisUtil.getCurrTokenKey(account));
            //不同则阻止访问，过期token将不可用
            if (!StringUtils.equals(tid, currToken)) {
                return false;
            }

            // 缓存的session
            Session session = JedisUtil.getSession(account);
            tid = JwtUtil.randomUUID();
            // 重新生成token
            String newToken = JwtUtil.sign(account,session.getTokenExpireTime(),tid);
            // 新的token再次登录验证，执行userRealm.doGetAuthenticationInfo()，如果错误会抛出异常并被捕获，否则登录成功
            this.getSubject(request, response).login(new JwtToken(newToken));
            int sessionExpireTime = session.getSessionExpireTime();
            //  重新设置会话过期时间
            JedisUtil.expire(sessionKey, sessionExpireTime);
            // 缓存过期的token
            JedisUtil.cacheExpiredToken(account, token);
            // 缓存当前使用的token
            JedisUtil.cacheCurrToken(account, tid, sessionExpireTime);

            // 新的token在header中返回
            HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
            httpServletResponse.setHeader(Constant.JWT_HEADER_KEY, newToken);
            httpServletResponse.setHeader("Access-Control-Expose-Headers", Constant.JWT_HEADER_KEY);

            return true;
        }
        //会话过期，阻止访问
        return false;
    }


    /**
     * 返回Response 401
     */
    private void response401(ServletResponse response, String msg) throws Exception {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        try (PrintWriter out = httpServletResponse.getWriter()) {
            out.append(JSONObject.toJSONString(new AjaxResult(msg).setStatuscode(-1)));
        } catch (IOException e) {
            log.error("返回response信息异常:" + e.getMessage());
            throw new Exception("返回response信息异常:" + e.getMessage());
        }
    }

    /**
     * 处理认证异常信息
     *
     * @param request
     * @param e
     * @return
     */
    private String logAuthenticationException(ServletRequest request, Exception e) {
        try {
            String token = this.getAuthzHeader(request);
            String account = JwtUtil.getClaim(token, Constant.JWT_ACCOUNT);
            Session session = JedisUtil.getSession(account);

            String requestURI = WebUtils.toHttp(request).getRequestURI();
            String exception = ExceptionUtils.getRootCauseMessage(e);

            String authenticationException = String.format("url=%s,login=%s,request=%s,msg=%s", requestURI,  exception);
            log.info(authenticationException);
            return authenticationException;
        } catch (Exception _e) {
            return ExceptionUtils.getRootCauseMessage(_e);
        }
    }
}
