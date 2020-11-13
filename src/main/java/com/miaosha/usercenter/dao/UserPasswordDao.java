package com.miaosha.usercenter.dao;

import com.miaosha.usercenter.entity.UserPassword;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @auhor: dhz
 * @date: 2020/11/13 14:40
 */
@Repository
public interface UserPasswordDao extends Mapper<UserPassword> {

//    根据用户id查找密码
    UserPassword selectByUserId(@Param("userId") Integer userId);
}
