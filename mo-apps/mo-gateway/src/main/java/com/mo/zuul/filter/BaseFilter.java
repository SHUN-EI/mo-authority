package com.mo.zuul.filter;

import cn.hutool.core.util.StrUtil;
import com.mo.base.R;
import com.mo.common.adapter.IgnoreTokenConfig;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by mo on 2023/12/4
 * 基础过滤器
 */
public abstract class BaseFilter  extends ZuulFilter {

    // /api
    @Value("${server.servlet.context-path}")
    protected String zuulPrefix;


    /**
     * 判断当前请求uri是否需要忽略（直接放行）
     * @return
     */
    protected boolean isIgnoreToken(){
        //动态获取当前请求的uri
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String uri = request.getRequestURI();

        // /api/authority/user/list  ——>  /authority/user/list
        uri = StrUtil.subSuf(uri,zuulPrefix.length());
        // /authority/user/list  ——>  /user/list
        uri = StrUtil.subSuf(uri,uri.indexOf("/",1));
        boolean ignoreToken = IgnoreTokenConfig.isIgnoreToken(uri);
        return ignoreToken;
    }

    /**
     * 网关抛异常，不再进行路由，而是直接返回到前端
     * @param errMsg
     * @param errCode
     * @param httpStatusCode
     */
    protected void errorResponse(String errMsg,int errCode,int httpStatusCode){
        RequestContext ctx = RequestContext.getCurrentContext();
        //设置响应状态码
        ctx.setResponseStatusCode(httpStatusCode);
        //设置响应头信息
        ctx.addZuulResponseHeader("Content-Type","application/json;charset=utf-8");
        if(ctx.getResponseBody() == null){
            //设置响应体
            ctx.setResponseBody(R.fail(errCode,errMsg).toString());
            //不进行路由，直接返回
            ctx.setSendZuulResponse(false);
        }
    }
}
