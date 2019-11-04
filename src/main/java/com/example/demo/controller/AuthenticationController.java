package com.example.demo.controller;

import com.config.AjaxResult;
import com.example.demo.model.User;
import com.example.demo.model.UserModel;
import com.example.demo.service.AuthenticationService;
import io.swagger.annotations.Api;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Description:
 */
@RestController
@Api("登录接口")
public class AuthenticationController {

    @Resource
    private AuthenticationService authenticationService;

    /**
     * @description 授权登录
     * @author Y
     * @Param [ null ]
     * @return
     * @date 2019/10/28 11:21
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody UserModel userModel, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return authenticationService.login(userModel, request, response);
    }

    /**
     * @description 退出
     * @author Y
     * @Param [ null ]
     * @return
     * @date 2019/10/28 11:21
     */
    @PostMapping("/logout")
    public AjaxResult logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return authenticationService.logout( request, response);
    }

    /**
     * @description 创建用户
     * @author Y
     * @Param [ null ]
     * @return
     * @date 2019/10/28 11:21
     */
    @PostMapping("/user")
    public String createUser(@Valid @RequestBody User user, BindingResult result) throws Exception {
        user.validate(result);
        return authenticationService.createUser(user);
    }

}
