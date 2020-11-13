package com.miaosha.usercenter.dao;

import com.miaosha.usercenter.entity.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @auhor: dhz
 * @date: 2020/11/12 17:45
 */
@Repository
public interface UserInfoDao extends Mapper<UserInfo> {

//    根据手机查找用户信息
    UserInfo selectByTelephone(@Param("telephone") String telephone);
}
