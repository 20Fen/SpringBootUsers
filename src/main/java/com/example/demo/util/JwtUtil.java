package com.example.demo.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.magic.Constant;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

/**
 * Description: jwt工具类
 */
public class JwtUtil {

    public static boolean verify(String token) throws Exception {
        try {
            // 帐号加JWT私钥解密
        String secret = null;
        try {
            secret = getClaim(token, Constant.JWT_ACCOUNT) + Base64ConvertUtil.decode(AuthenticationPropUtil.getProperty("auth.encryptJWTKey"));
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(token);
        return true;
    } catch (
    UnsupportedEncodingException e) {
            throw new Exception("jwt认证解密异常");
    }}
    /**
     * 获得Token中的信息，无需secret解密也能获得
     *
     * @param token
     * @param claim
     * @return
     */

        public static String getClaim(String token, String claim) {
            return JWT.decode(token).getClaim(claim).asString();
        }

    /**
     * 生成token
     *
     * @param account
     * @param tokenExpireTime
     * @param uuid
     * @return
     */
    public static String sign(String account, int tokenExpireTime, String uuid) throws Exception {
        try {
            // 帐号加JWT私钥加密
            String secret = account + Base64ConvertUtil.decode(AuthenticationPropUtil.getProperty("auth.encryptJWTKey"));
            // 过期时间（毫秒）
            Date date = new Date(System.currentTimeMillis() + tokenExpireTime * 1000);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带account、会话id
            return JWT.create()
                    .withClaim(Constant.JWT_ACCOUNT, account)
                    .withClaim(Constant.JWT_TID,uuid)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            throw new Exception("jwt加密异常:" + e.getMessage());
        }
    }

    /**
     * 通用token
     */
    public static final String GENERIC_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjIxOTcwMTAwODIsInRpZCI6ImZiOTgyYmNjYWNjMTRhNWE5MjU0MDAxM2ExZTA3NDdlIiwiYWNjb3VudCI6ImdlbmVyaWMiLCJzaWQiOiI4OWQxZjYwNWUzODM0YmJhODRkNzBkMjlkN2.1jmLXAmo9NIdzEE3dzwVJ1T5vdasI5lldI_31fdJdL4";

    public static void main(String[] args) throws Exception {
        try {
            // 帐号加JWT私钥加密
            String secret = "generic" + Base64ConvertUtil.decode(AuthenticationPropUtil.getProperty("auth.encryptJWTKey"));
            // 过期时间（毫秒）
            Date date = new Date(System.currentTimeMillis() + 60 * 60 * 24 * 365 * 20 * 1000L);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带account、会话id
            String token = JWT.create()
                    .withClaim(Constant.JWT_ACCOUNT, "generic")
                    .withExpiresAt(date)
                    .sign(algorithm);
            System.out.println(token);
        } catch (UnsupportedEncodingException e) {
            throw new Exception("jwt加密异常:" + e.getMessage());
        }
    }

    /**
     * 生成uuid
     *
     * @return
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
