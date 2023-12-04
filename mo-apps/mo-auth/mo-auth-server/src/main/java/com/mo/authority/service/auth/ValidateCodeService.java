package com.mo.authority.service.auth;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by mo on 2023/12/2
 * 验证码-业务接口
 */
public interface ValidateCodeService {

    void createCaptcha(String key, HttpServletResponse response);

    Boolean checkCode(String key, String code);
}
