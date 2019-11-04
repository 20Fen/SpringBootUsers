package com.example.demo.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * Description:会话信息
 */
@Data
public class Session extends User implements Serializable {

    private long loginTime;
    private int tokenExpireTime;
    private int sessionExpireTime;
    private Collection<String> permissions;
}
