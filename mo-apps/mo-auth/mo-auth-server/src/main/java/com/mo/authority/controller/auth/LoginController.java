package com.mo.authority.controller.auth;

import com.mo.authority.dto.auth.LoginDTO;
import com.mo.authority.dto.auth.LoginParamDTO;
import com.mo.authority.service.auth.ValidateCodeService;
import com.mo.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/check")
    @ApiOperation(notes = "校验验证码", value = "校验验证码")
    public Boolean checkCode(@RequestBody LoginParamDTO loginParamDTO) {
        return validateCodeService.checkCode(loginParamDTO.getKey(), loginParamDTO.getCode());

    }

}
