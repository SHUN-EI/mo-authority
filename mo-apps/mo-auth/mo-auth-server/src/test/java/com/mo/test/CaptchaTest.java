package com.mo.test;

import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.base.Captcha;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by mo on 2023/12/2
 */
public class CaptchaTest {

    public static void main(String[] args) throws FileNotFoundException {

        //中文验证码
        Captcha captcha = new ChineseCaptcha(150, 60);
        //获取本次生成的验证码
        String code = captcha.text();
        System.out.println(code);
        captcha.out(new FileOutputStream("/Users/mo/develop/logs/code.png"));

        //算术验证码
        Captcha captcha1 =new ArithmeticCaptcha();
        //获取本次生成的验证码
        String code1 = captcha1.text();
        System.out.println(code1);
        captcha1.out(new FileOutputStream("/Users/mo/develop/logs/code1.png"));

    }
}
