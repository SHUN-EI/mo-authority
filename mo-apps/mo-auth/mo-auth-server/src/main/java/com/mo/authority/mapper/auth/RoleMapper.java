package com.mo.authority.mapper.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mo.authority.entity.auth.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mo on 2023/12/5
 * 角色-Mapper 接口
 */
@Repository
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 查询用户拥有的角色
     *
     * @param userId
     * @return
     */
    List<Role> findRoleByUserId(@Param("userId") Long userId);

    /**
     * 根据角色编码查询用户ID
     *
     * @param codes 角色编码
     * @return
     */
    List<Long> findUserIdByCode(@Param("codes") String[] codes);
}
