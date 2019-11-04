package com.example.demo.dao.mapper;

import com.example.demo.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * Description:
 *
 * @author yangfl
 * @date 2019年10月28日 11:38
 * Version 1.0
 */
@Mapper
public interface AuthenticationMapper {

    User selectOne(String account);

    void insert(User u);

    void updata(User u);
}
