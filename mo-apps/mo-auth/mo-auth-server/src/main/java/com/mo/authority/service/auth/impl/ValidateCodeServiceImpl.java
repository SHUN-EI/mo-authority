package com.mo.authority.service.auth.impl;

import com.mo.authority.service.auth.ValidateCodeService;
import com.mo.common.constant.CacheKey;
import com.mo.exception.BizException;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import net.oschina.j2cache.CacheChannel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by mo on 2023/12/2
 * 验证码服务
 */
@Service
public class ValidateCodeServiceImpl implements ValidateCodeService {

    @Autowired
    private CacheChannel cacheChannel;

    /**
     * 生成验证码
     *
     * @param key
     * @param response
     */
    @Override
    public void createCaptcha(String key, HttpServletResponse response) {

        if (StringUtils.isBlank(key)) {
            throw BizException.validFail("验证码key不能为空");
        }

        Captcha captcha = new ArithmeticCaptcha(115, 42);
        captcha.setCharType(2);

        //缓存验证码
        cacheChannel.set(CacheKey.CAPTCHA, key, StringUtils.lowerCase(captcha.text()));

        //将生成的图片验证码通过输出流写回客户端浏览器页面
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        response.setHeader(HttpHeaders.PRAGMA, "No-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "No-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0L);

        try (ServletOutputStream ops = response.getOutputStream()) {
            captcha.out(ops);
        } catch (IOException e) {
            throw BizException.validFail("获取图形验证码异常:{}", e);
        }

    }
}
