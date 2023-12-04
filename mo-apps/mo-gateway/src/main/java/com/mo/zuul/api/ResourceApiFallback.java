package com.mo.zuul.api;

import com.mo.authority.dto.auth.ResourceQueryDTO;
import com.mo.authority.entity.auth.Resource;
import com.mo.base.R;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by mo on 2023/12/4
 * 资源API熔断,返回null
 */
@Component
public class ResourceApiFallback implements ResourceApi {
    @Override
    public R<List> list() {
        return null;
    }

    @Override
    public R<List<Resource>> visible(ResourceQueryDTO resource) {
        return null;
    }
}
