package com.mo.authority.service.core;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.authority.entity.core.Org;

import java.util.List;

/**
 * Created by mo on 2023/12/4
 * 组织-业务接口
 */
public interface OrgService extends IService<Org> {

    List<Org> findChildren(List<Long> ids);

    Boolean remove(List<Long> ids);
}
