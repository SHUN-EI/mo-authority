package com.mo.authority.service.auth;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.authority.dto.auth.RoleAuthoritySaveDTO;
import com.mo.authority.dto.auth.UserRoleSaveDTO;
import com.mo.authority.entity.auth.RoleAuthority;

/**
 * Created by mo on 2023/12/5
 * 角色资源关系-业务接口
 */
public interface RoleAuthorityService extends IService<RoleAuthority> {

    /**
     * 给用户分配角色
     */
    Boolean saveUserRole(UserRoleSaveDTO userRoleSaveDTO);

    /**
     * 给角色重新分配 权限（资源/菜单）
     */
    Boolean saveRoleAuthority(RoleAuthoritySaveDTO roleAuthoritySaveDTO);
}
