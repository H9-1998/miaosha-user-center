package com.miaosha.usercenter.controller;

import com.miaosha.usercenter.entity.UserInfo;
import com.miaosha.usercenter.error.BusinessException;
import com.miaosha.usercenter.error.EmBusinessError;
import com.miaosha.usercenter.model.UserModel;
import com.miaosha.usercenter.response.CommonReturnType;
import com.miaosha.usercenter.service.UserService;
import com.miaosha.usercenter.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auhor: dhz
 * @date: 2020/11/12 17:49
 */

@RestController
@RequestMapping("/user")
@Api(tags = {"用户操作类"})
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 登录
     * @param telephone
     * @param password
     * @return
     * @throws BusinessException
     */
    @GetMapping("/login")
    @ApiOperation("登录接口")
    public CommonReturnType login(@RequestParam("telephone") String telephone, @RequestParam("password") String password) throws BusinessException {
        UserModel userModel = userService.login(telephone, password);
        if (userModel == null)
            throw new BusinessException(EmBusinessError.LOGIN_ERROR);

        UserVO userVO = convertFromModel(userModel);
        return CommonReturnType.create(userVO);
    }

    //    ----------------------------------------------------非业务方法-------------------------------------------------------------------

    private UserVO convertFromModel(UserModel userModel){
        if (userModel == null)
            return null;

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }
}
