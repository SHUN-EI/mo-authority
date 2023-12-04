package com.mo.zuul.api;

import com.mo.authority.dto.auth.ResourceQueryDTO;
import com.mo.authority.entity.auth.Resource;
import com.mo.base.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Created by mo on 2023/12/4
 * 调用权限服务相关接口api
 */
@FeignClient(name = "${feign.authority-server:mo-auth-server}",
        fallback = ResourceApiFallback.class)
public interface ResourceApi {

    /**
     * 获取所有需要鉴权的资源
     * @return
     */
    @GetMapping("/resource/list")
    R<List> list();


    /**
     * 查询当前登录用户拥有的资源权限
     * @param resource
     * @return
     */
    @GetMapping("/resource/visible")
     R<List<Resource>> visible(ResourceQueryDTO resource);
}
