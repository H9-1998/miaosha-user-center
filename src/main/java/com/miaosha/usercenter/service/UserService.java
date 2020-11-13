package com.miaosha.usercenter.service;

import com.miaosha.usercenter.dao.UserInfoDao;
import com.miaosha.usercenter.dao.UserPasswordDao;
import com.miaosha.usercenter.entity.UserInfo;
import com.miaosha.usercenter.entity.UserPassword;
import com.miaosha.usercenter.error.BusinessException;
import com.miaosha.usercenter.error.EmBusinessError;
import com.miaosha.usercenter.model.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * @auhor: dhz
 * @date: 2020/11/12 17:48
 */

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private UserPasswordDao userPasswordDao;

    @Autowired
    private RedisTemplate redisTemplate;

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

    /**
     * 生成验证码
     * @param telephone
     */
    public void generateOtpCode(String telephone) throws BusinessException {
        if (userInfoDao.selectByTelephone(telephone) != null) {
            // 该手机号已被注册
            throw new BusinessException(EmBusinessError.HAS_BEEN_REGISTER);
        }
        Random random = new Random();
        Integer otpCode = random.nextInt(99999) + 10000;
        redisTemplate.opsForValue().set("otp_code_"+telephone, otpCode, 5, TimeUnit.MINUTES);
        log.info("手机号: {} 的验证码为: {}", telephone, otpCode);
    }

    /**
     * 用户注册
     * @param userModel
     * @param otpCode
     * @throws BusinessException
     */
    @Transactional
    public void register(UserModel userModel, String otpCode) throws BusinessException {
        if (userModel == null)
            // 参数错误
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);

        if (userInfoDao.selectByTelephone(userModel.getTelephone()) != null)
            // 该手机号已被注册
            throw new BusinessException(EmBusinessError.HAS_BEEN_REGISTER);

        if (redisTemplate.opsForValue().get("otp_code_" + userModel.getTelephone()) == null)
            // 验证码过期或未获取
            throw new BusinessException(EmBusinessError.OTP_CODE_ERROR);

        String otpCodeInRedis = redisTemplate.opsForValue().get("otp_code_" + userModel.getTelephone()).toString();
        if (!StringUtils.equals(otpCode, otpCodeInRedis))
            // 验证码错误
            throw new BusinessException(EmBusinessError.OTP_CODE_ERROR);

        // 存入db
        UserInfo userInfo = convertUserInfoFromUserModel(userModel);
        UserPassword userPassword = convertUserPasswordFromUserModel(userModel);
        userInfoDao.insertSelective(userInfo);
        userPassword.setUserId(userInfo.getId());
        userPasswordDao.insertSelective(userPassword);
    }



//    ----------------------------------------------------非业务方法-------------------------------------------------------------------

//    将UserInfo和UserPassword转为model
    private UserModel convertFromDataObject(UserInfo userInfo, UserPassword userPassword){
        if (userInfo == null || userPassword == null)
            return null;
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userInfo, userModel);
        BeanUtils.copyProperties(userPassword, userInfo);
        return userModel;
    }

//    将UserModel转为UserInfo
    private UserInfo convertUserInfoFromUserModel(UserModel userModel){
        if (userModel == null)
            return null;
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userModel, userInfo);
        return userInfo;
    }
//    将UserModel转为UserPassword
    private UserPassword convertUserPasswordFromUserModel(UserModel userModel){
        if (userModel == null)
            return null;
        UserPassword userPassword = new UserPassword();
        BeanUtils.copyProperties(userModel, userPassword);
        return userPassword;
    }
}
