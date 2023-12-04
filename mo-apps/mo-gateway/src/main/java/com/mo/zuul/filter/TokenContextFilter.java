package com.mo.zuul.filter;

import com.mo.auth.client.properties.AuthClientProperties;
import com.mo.auth.client.utils.JwtTokenClientUtils;
import com.mo.auth.utils.JwtUserInfo;
import com.mo.base.R;
import com.mo.context.BaseContextConstants;
import com.mo.exception.BizException;
import com.mo.utils.StrHelper;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by mo on 2023/12/4
 * 当前过滤器负责解析请求头中的jwt令牌并且将解析出的用户信息放入zuul的header中
 */
@Component
public class TokenContextFilter extends BaseFilter {

    @Autowired
    private AuthClientProperties authClientProperties;
    @Autowired
    private JwtTokenClientUtils jwtTokenClientUtils;


    /**
     * 前置过滤器
     *
     * @return
     */
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    /**
     * 设置过滤器执行顺序，数值越大优先级越低
     * 在 这个PRE_DECORATION_FILTER 之后执行
     *
     * @return
     */
    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;
    }

    /**
     * 是否执行当前过滤器
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 过滤逻辑
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        if(isIgnoreToken()){
            //直接放行
            return null;
        }

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        //从请求头中获取前端提交的jwt令牌
        String userToken = request.getHeader(authClientProperties.getUser().getHeaderName());

        JwtUserInfo userInfo = null;
        //解析jwt令牌
        try {
            userInfo = jwtTokenClientUtils.getUserInfo(userToken);
        }catch (BizException e){
            errorResponse(e.getMessage(),e.getCode(),200);
            return null;
        }catch (Exception e){
            errorResponse("解析jwt令牌出错", R.FAIL_CODE,200);
            return null;
        }

        //将信息放入header
        if (userInfo != null) {
            addHeader(ctx, BaseContextConstants.JWT_KEY_ACCOUNT,
                    userInfo.getAccount());
            addHeader(ctx, BaseContextConstants.JWT_KEY_USER_ID,
                    userInfo.getUserId());
            addHeader(ctx, BaseContextConstants.JWT_KEY_NAME,
                    userInfo.getName());
            addHeader(ctx, BaseContextConstants.JWT_KEY_ORG_ID,
                    userInfo.getOrgId());
            addHeader(ctx, BaseContextConstants.JWT_KEY_STATION_ID,
                    userInfo.getStationId());
        }
        return null;
    }

    /**
     * 将指定信息放入zuul的header中
     * @param ctx
     * @param name
     * @param value
     */
    private void addHeader(RequestContext ctx, String name, Object value) {
        if (StringUtils.isEmpty(value)) {
            return;
        }
        ctx.addZuulRequestHeader(name, StrHelper.encode(value.toString()));
    }
}
