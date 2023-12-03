package com.mo.authority.service.auth;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.authority.dto.auth.ResourceQueryDTO;
import com.mo.authority.entity.auth.Resource;

import java.util.List;

/**
 * Created by mo on 2023/12/4
 */
public interface ResourceService extends IService<Resource> {

    List<Resource> findVisibleResource(ResourceQueryDTO resourceQueryDTO);
}
