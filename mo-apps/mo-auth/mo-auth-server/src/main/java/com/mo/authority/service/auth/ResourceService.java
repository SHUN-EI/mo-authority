package com.mo.authority.service.auth;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.authority.dto.auth.ResourceQueryDTO;
import com.mo.authority.entity.auth.Resource;

import java.util.List;

/**
 * Created by mo on 2023/12/4
 * 资源-业务接口
 */
public interface ResourceService extends IService<Resource> {

    List<Resource> findVisibleResource(ResourceQueryDTO resourceQueryDTO);

    void removeByMenuId(List<Long> menuIds);

    List<Long> findMenuIdByResourceId(List<Long> resourceIdList);
}
