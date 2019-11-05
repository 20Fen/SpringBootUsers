package com.example.demo.model;

import com.config.BaseProtocolIn;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Description: PO类
 */
@Data
@ApiModel(value = "用户信息", description = "请求参数body")
public class UserModel extends BaseProtocolIn implements Serializable {
    //生成UID:File->settings->Editor->Inspections->Serialization issues->Serializable class without 'serialVersionUID'

    private static final long serialVersionUID = 7034302540275683167L;
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "用户名")
    @NotBlank(message = "账号不能为空")
    private String name;
    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
}
