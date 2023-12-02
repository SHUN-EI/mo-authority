package com.mo.authority.controller.auth;

import com.mo.authority.service.auth.ValidateCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by mo on 2023/12/2
 * 登录(认证)控制器
 */
@RestController
@RequestMapping("/anno")
@Api(tags = "登录控制器", value = "LoginController")
public class LoginController {

    @Autowired
    private ValidateCodeService validateCodeService;

    @GetMapping(value = "/captcha", produces = "image/png")
    @ApiOperation(notes = "获取图形验证码", value = "获取图形验证码")
    public void captcha(@RequestParam(value = "key") String key, HttpServletResponse response) {
        validateCodeService.createCaptcha(key, response);
    }
}
