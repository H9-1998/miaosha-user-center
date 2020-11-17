package com.miaosha.usercenter.controller;

import com.miaosha.usercenter.entity.UserInfo;
import com.miaosha.usercenter.error.BusinessException;
import com.miaosha.usercenter.model.UserModel;
import com.miaosha.usercenter.response.CommonReturnType;
import com.miaosha.usercenter.service.UserService;
import com.miaosha.usercenter.util.JwtUtil;
import com.miaosha.usercenter.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @auhor: dhz
 * @date: 2020/11/12 17:49
 */

@RestController
@RequestMapping("/user")
@Api(tags = {"用户操作类"})
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/get-otpCode")
    @ApiOperation("生成验证码")
    public CommonReturnType getOtpCode(@RequestParam("telephone") String telephone) throws BusinessException {
        userService.generateOtpCode(telephone);
        return CommonReturnType.create("生成验证码成功");
    }

    @PostMapping("/register")
    @ApiOperation("用户注册")
    public CommonReturnType register(@RequestParam("telephone") String telephone,
                                     @RequestParam("password") String password,
                                     @RequestParam("gender") Integer gender,
                                     @RequestParam("age") Integer age,
                                     @RequestParam("name") String name,
                                     @RequestParam("otpCode") String otpCode) throws BusinessException {
        UserModel userModer = UserModel.builder()
                .telephone(telephone)
                .encryptPassword(password)
                .gender(gender.byteValue())
                .age(age)
                .name(name)
                .registerMode("byPhone")
                .thirdPartyId("").build();
        userService.register(userModer, otpCode);
        return CommonReturnType.create("注册成功");
    }

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
        String token = userService.login(telephone, password);

        // 返回token
        return CommonReturnType.create(token);
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/get-user-info")
    public CommonReturnType getUserInfo(@RequestHeader("x-token") String token){
        Integer userId = jwtUtil.getUserIdFromToken(token);
        log.info("8082被调用");
        return CommonReturnType.create(userService.getUserInfo(userId));
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
