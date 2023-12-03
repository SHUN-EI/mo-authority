package com.mo.authority.controller.auth;

import com.mo.authority.dto.auth.LoginDTO;
import com.mo.authority.dto.auth.LoginParamDTO;
import com.mo.authority.service.auth.LoginService;
import com.mo.authority.service.auth.ValidateCodeService;
import com.mo.base.BaseController;
import com.mo.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by mo on 2023/12/2
 * 登录(认证)控制器
 */
@RestController
@RequestMapping("/anno")
@Api(tags = "登录控制器", value = "LoginController")
public class LoginController extends BaseController {

    @Autowired
    private ValidateCodeService validateCodeService;
    @Autowired
    private LoginService loginService;

    @GetMapping(value = "/captcha", produces = "image/png")
    @ApiOperation(notes = "获取图形验证码", value = "获取图形验证码")
    public void captcha(@RequestParam(value = "key") String key, HttpServletResponse response) {
        validateCodeService.createCaptcha(key, response);
    }

    @PostMapping("/check")
    @ApiOperation(notes = "校验验证码", value = "校验验证码")
    public Boolean checkCode(@RequestBody LoginParamDTO loginParamDTO) {
        return validateCodeService.checkCode(loginParamDTO.getKey(), loginParamDTO.getCode());

    }

    @PostMapping("/login")
    @ApiOperation(notes = "登录", value = "登录")
    public R<LoginDTO> login(@Validated @RequestBody LoginParamDTO loginParamDTO) {

        Boolean check = validateCodeService.checkCode(loginParamDTO.getKey(), loginParamDTO.getCode());
        if (check) {
            //验证码校验通过，执行具体的登录认证逻辑
            R<LoginDTO> loginResult = loginService.login(loginParamDTO.getAccount(), loginParamDTO.getPassword());
            return loginResult;
        }
        //验证码校验不通过，直接返回
        return success(null);
    }

}
