package com.am.adastra.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.am.adastra.entity.User;
import com.am.adastra.entity.UserDBO;
import com.am.adastra.entity.dto.UpdatePwdDTO;
import com.am.adastra.entity.dto.UserRegisterDTO;
import com.am.adastra.entity.param.ValidationRules;
import com.am.adastra.ex.SystemException;
import com.am.adastra.ex.UserNotLoginException;
import com.am.adastra.ex.ValidException;
import com.am.adastra.mapper.AvatarMapper;
import com.am.adastra.mapper.UserMapper;
import com.am.adastra.service.UserService;
import com.am.adastra.util.*;
import com.am.adastra.util.wx.JwtUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.regex.Pattern;

/**
 * @Author : ArturiaMu KMUST-Stu
 * @Date : 2022/5/27 10:34
 * @Params : @param null
 * @Exception :
 * @Return :
 * @Description ：
 */
@Slf4j
@Api(tags = "用户模块")
@RestController
@RequestMapping(value = "/user", produces = "application/json;charset=utf-8")
public class UserController {
    public static final String VERIFICATION_CODE_SESSION = "verificationCodeSession";
    public static final String USER_INFO_SESSION = "userInfoSession";
    public static final Pattern patternMail = Pattern.compile("^\\s*\\w+(?:\\.?[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
    public static final Pattern patternPhone = Pattern.compile("^1[3|4|5|8|9][0-9]\\d{8}$");
    public static final String AD_ASTRA = "ad-astra 官方团队";
    public static final String WELCOME = "hi，欢迎加入ad astra，让我们开始愉快的探索之旅吧~";

    @Resource
    private AvatarMapper avatarMapper;

    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private SMSUtil smsUtil;
    @Resource
    private EmailUtil emailUtil;

    @ApiOperation("发送验证码")
    @ApiOperationSupport(order = 0)
    @PostMapping("/verifyCode")
    public Result<Void> verifyCode(@RequestBody @NotBlank String account, HttpServletRequest request) {
        log.info("发送验证码：{}", account);
        Result<Void> result = new Result<>();
        JSONObject jsonObject = JSON.parseObject(account);
        String acc = jsonObject.getString("account");
        if (patternMail.matcher(acc).matches()) {
            if (emailUtil.sendRegisterMail(acc, request)) {
                log.info("获取邮箱验证码成功");
                result.setSuccess("获取邮箱验证码成功！", null);
            } else {
                throw new SystemException("系统繁忙，请稍后重试");
            }
        } else if (patternPhone.matcher(acc).matches()) {
            if (smsUtil.sendSMS(acc, request)) {
                log.info("获取手机验证码成功");
                result.setSuccess("获取手机验证码成功！", null);
            } else {
                throw new SystemException("系统繁忙，请稍后重试");
            }
        } else {
            log.info("账号不合法");
            result.setFail("账号不合法");
        }
        return result;
    }

    @ApiOperation("用户注册")
    @ApiOperationSupport(order = 5)
    @PostMapping("/register")
    public Result<User> register(@RequestBody @Validated(ValidationRules.register.class) UserRegisterDTO rp, BindingResult errors, HttpServletRequest request) {
        log.info("用户注册: {}", rp);
        Result<User> result = new Result<>();
        if (errors.hasErrors()) {
            throw new ValidException(errors.getFieldError().getDefaultMessage());
        }
        if (rp.getVerCode().equals(request.getSession().getAttribute(VERIFICATION_CODE_SESSION))) {
            User getUser = userService.register(rp.getUser());
            result.setSuccess("注册成功，已为您自动登录", getUser);
            request.getSession().setAttribute(USER_INFO_SESSION, getUser);
        } else {
            throw new ValidException("验证码错误");
        }
        return result;
    }


    @ApiOperation("用户登录")
    @ApiOperationSupport(order = 10)
    @PostMapping("/login")
    public Result<User> login(@RequestBody @Validated(ValidationRules.login.class) User user, BindingResult errors, HttpServletRequest request) {
        log.info("用户登录 ： {}", user);
        Result<User> result = new Result<>();
        if (errors.hasErrors()) {
            throw new ValidException(errors.getFieldError().getDefaultMessage());
        }
        String ip = IPUtil.getIP(request);
        log.info(ip);
        User getUser = userService.login(user, ip);
        request.getSession().setAttribute(USER_INFO_SESSION, getUser);
        result.setSuccess(getUser);
        return result;
    }

    @ApiOperation("修改用户密码")
    @ApiOperationSupport(order = 15)
    @PostMapping("/updatePwd")
    public Result<User> updatePwd(@RequestBody @Validated UpdatePwdDTO up, BindingResult errors, HttpServletRequest request) {
        log.info("修改密码 ： {}", up);
        Result<User> result = new Result<>();
        if (errors.hasErrors()) {
            throw new ValidException(errors.getFieldError().getDefaultMessage());
        }
        if (up.getVerCode().equals(request.getSession().getAttribute(VERIFICATION_CODE_SESSION))) {
            User getUser = userService.updatePwd(up.getPassword(), up.getAccount());
            result.setSuccess(getUser);
            request.getSession().setAttribute(USER_INFO_SESSION, null);
        } else {
            throw new ValidException("验证码错误");
        }
        return result;
    }

    @ApiOperation("判断用户是否登录")
    @ApiOperationSupport(order = 13)
    @GetMapping("/isLogin")
    public Result<User> isLogin(HttpServletRequest request) {
        User getUser = userService.isLogin(request.getSession());
        Result<User> result = new Result<>();
        result.setSuccess(getUser);
        return result;
    }

    @ApiOperation("判断用户是否vip")
    @ApiOperationSupport(order = 13)
    @GetMapping("/isVip")
    public Result<Boolean> isVip(HttpServletRequest request) {
        User getUser = userService.isLogin(request.getSession());
        Result<Boolean> result = new Result<>();
        result.setSuccess(userMapper.isVip(getUser.getId()) > 0);
        return result;
    }

    @ApiOperation("退出登录")
    @ApiOperationSupport(order = 30)
    @GetMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        Result<Void> result = new Result<>();
        result.setSuccess("用户退出登录成功！", null);
        request.getSession().setAttribute(USER_INFO_SESSION, null);
        return result;
    }

    @ApiOperation("判断验证码")
    @ApiOperationSupport(order = 1)
    @PostMapping("/verify")
    public Result<Void> verify(@RequestBody @NotBlank String code, HttpServletRequest request) {
        log.info("验证 ： {}", code);
        JSONObject jsonObject = JSON.parseObject(code);
        String acc = jsonObject.getString("code");
        Result<Void> result = new Result<>();
        if (acc.equals(request.getSession().getAttribute(VERIFICATION_CODE_SESSION))) {
            result.setSuccess();
        } else {
            result.setFail("验证码错误");
        }
        return result;
    }

    @ApiOperation("更新用户信息")
    @ApiOperationSupport(order = 16)
    @PostMapping("/update")
    public Result<User> update(@RequestBody User user, HttpServletRequest request) {
        if (userService.isLogin(request.getSession()) == null) {
            throw new UserNotLoginException("用户未登录");
        }
        log.info("修改信息：{}", user);
        Result<User> result = new Result<>();
        User sessionUser = (User) request.getSession().getAttribute(USER_INFO_SESSION);
        User getUser = userService.updateDBO(sessionUser, user);
        result.setSuccess(getUser);
        return result;
    }


    @ApiOperation(value = "根据token获取登录信息")
    @PostMapping("/auth/getLoginInfo")
    public Result<User> getLoginInfo(@RequestBody String token, HttpServletRequest request) {
        if (token.isEmpty()) {
            throw new ValidException("token 无效");
        }
        try {
            log.info("token：{}", token);
            JSONObject jsonObject = JSON.parseObject(token);
            String jsToken = jsonObject.getString("token");
            log.info("jsToken：{}", jsToken);
            String  tk = jsToken.substring(6);
            log.info("tk：{}", tk);
            String memberId = JwtUtils.getMemberIdByJwtToken(tk);
            log.info("微信用户jwt id：{}", memberId);
            UserDBO loginInfoVo = userMapper.getDBOByAccount(memberId);
            log.info("微信用户：{}", loginInfoVo);
            Result<User> result = new Result<>();
            request.getSession().setAttribute(USER_INFO_SESSION, POJOUtils.DBToUser(loginInfoVo));
            result.setSuccess(POJOUtils.DBToUser(loginInfoVo));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValidException("微信登录错误");
        }
    }
}
