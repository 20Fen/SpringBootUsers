package com.example.demo.model;

import com.config.BaseProtocolIn;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Description:
 */
@Data
@ApiModel(value = "用户信息", description = "请求参数body")
public class User extends BaseProtocolIn {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    private String email;
    @ApiModelProperty(value = "账号")
    @NotBlank(message = "账号不能为空")
    private String account;
    @NotBlank(message = "用户名不能为空")
    private String name;
    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
    @ApiModelProperty(value = "性别")
    @NotBlank(message = "性别不能为空")
    private String sex;
}
