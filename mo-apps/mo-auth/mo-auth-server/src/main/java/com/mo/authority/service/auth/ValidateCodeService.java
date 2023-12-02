package com.mo.authority.service.auth;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by mo on 2023/12/2
 */
public interface ValidateCodeService {

    void createCaptcha(String key, HttpServletResponse response);
}
