package com.miaosha.usercenter.service;

import com.miaosha.usercenter.dao.UserInfoDao;
import com.miaosha.usercenter.dao.UserPasswordDao;
import com.miaosha.usercenter.entity.UserInfo;
import com.miaosha.usercenter.entity.UserPassword;
import com.miaosha.usercenter.error.BusinessException;
import com.miaosha.usercenter.error.EmBusinessError;
import com.miaosha.usercenter.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @auhor: dhz
 * @date: 2020/11/12 17:48
 */

@Service
public class UserService {

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private UserPasswordDao userPasswordDao;

    /**
     * 登录
     * @param telephone 手机号
     * @param password 密码
     * @return
     * @throws BusinessException
     */
    public UserModel login(String telephone, String password) throws BusinessException {
        // 判空
        if (StringUtils.isEmpty(telephone) || StringUtils.isEmpty(password))
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);

        if (userInfoDao.selectByTelephone(telephone) == null){
            // 用户不存在
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        // 查找密码并校验
        UserInfo userInfo = userInfoDao.selectByTelephone(telephone);
        UserPassword userPassword = userPasswordDao.selectByUserId(userInfo.getId());
        if (!StringUtils.equals(password, userPassword.getEncryptPassword()))
            throw new BusinessException(EmBusinessError.LOGIN_ERROR);
        UserModel userModel = convertFromDataObject(userInfo, userPassword);

        return userModel;
    }



//    ----------------------------------------------------非业务方法-------------------------------------------------------------------

    private UserModel convertFromDataObject(UserInfo userInfo, UserPassword userPassword){
        if (userInfo == null || userPassword == null)
            return null;
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userInfo, userModel);
        BeanUtils.copyProperties(userPassword, userInfo);
        return userModel;

    }
}
