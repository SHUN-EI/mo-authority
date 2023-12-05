package com.mo.authority.service.auth;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.authority.entity.auth.RoleOrg;

import java.util.List;

/**
 * Created by mo on 2023/12/5
 * 角色组织关系-业务接口
 */
public interface RoleOrgService  extends IService<RoleOrg> {
    /**
     * 根据角色id查询
     */
    List<Long> listOrgByRoleId(Long id);
}
