package com.example.demo.service;

import com.config.AjaxResult;
import com.example.demo.model.User;
import com.example.demo.model.UserModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Description:  接口
 */
public interface AuthenticationService {

    AjaxResult login(UserModel userModel, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception;

    AjaxResult logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    AjaxResult permission(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    User getUser(String account);

    List<String> getRole(String account);

    List<String> getReource(String account);

    String createUser(User user) throws Exception;
}
