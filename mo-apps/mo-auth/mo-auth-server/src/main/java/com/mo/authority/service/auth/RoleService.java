package com.mo.authority.service.auth;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.authority.dto.auth.RoleSaveDTO;
import com.mo.authority.dto.auth.RoleUpdateDTO;
import com.mo.authority.entity.auth.Role;

import java.util.List;

/**
 * Created by mo on 2023/12/5
 */
public interface RoleService extends IService<Role> {

    Boolean removeById(List<Long> ids);

    List<Role> findRoleByUserId(Long userId);

    void saveRole(RoleSaveDTO roleSaveDTO, Long userId);

    void updateRole(RoleUpdateDTO roleUpdateDTO, Long userId);

    List<Long> findUserIdByCode(String[] codes);

    Boolean check(String code);


}
