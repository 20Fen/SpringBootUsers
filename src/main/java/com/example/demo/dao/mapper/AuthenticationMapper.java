package com.example.demo.dao.mapper;

import com.example.demo.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * Description: mapper类
 */
@Mapper
public interface AuthenticationMapper {

    User selectOne(String account);

    void insert(User u);

    void updata(User u);
}
