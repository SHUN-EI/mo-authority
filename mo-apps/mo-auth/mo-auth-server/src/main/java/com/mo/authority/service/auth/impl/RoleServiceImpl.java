package com.mo.authority.service.auth.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.authority.dto.auth.RoleSaveDTO;
import com.mo.authority.dto.auth.RoleUpdateDTO;
import com.mo.authority.entity.auth.*;
import com.mo.authority.mapper.auth.RoleMapper;
import com.mo.authority.service.auth.*;
import com.mo.base.id.CodeGenerate;
import com.mo.common.constant.CacheKey;
import com.mo.databases.mybatis.conditions.Wraps;
import com.mo.databases.mybatis.conditions.query.LbqWrapper;
import com.mo.dozer.DozerUtils;
import com.mo.utils.StrHelper;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mo on 2023/12/5
 * 角色-业务实现类
 */
@Slf4j
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private UserService userService;
    @Autowired
    private CacheChannel cacheChannel;
    @Autowired
    private RoleOrgService roleOrgService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleAuthorityService roleAuthorityService;
    @Autowired
    private DozerUtils dozerUtils;
    @Autowired
    private CodeGenerate codeGenerate;

    /**
     * 根据ID删除
     *
     * @param ids
     * @return
     */
    @Override
    public Boolean removeById(List<Long> ids) {
        if (ids.isEmpty()) return true;


        ids.forEach(roleId -> {
            List<User> userList = userService.findUserByRoleId(roleId, null);

            if (userList != null && userList.size() > 0) {
                userList.forEach(user -> cacheChannel.evict(CacheKey.USER_RESOURCE, user.getId().toString()));
            }
        });

        //删除主表pd_auth_role数据
        super.removeByIds(ids);
        //删除pd_auth_role_org关系表数据
        LbqWrapper<RoleOrg> roleOrgLbqWrapper = Wraps.<RoleOrg>lbQ().in(RoleOrg::getRoleId, ids);
        roleOrgService.remove(roleOrgLbqWrapper);

        //删除pd_auth_role_authority关系表数据
        LbqWrapper<RoleAuthority> roleAuthorityLbqWrapper = Wraps.<RoleAuthority>lbQ().in(RoleAuthority::getRoleId, ids);
        roleAuthorityService.remove(roleAuthorityLbqWrapper);

        //删除pd_auth_user_role关系表数据
        LbqWrapper<UserRole> userRoleLbqWrapper = Wraps.<UserRole>lbQ().in(UserRole::getRoleId, ids);
        userRoleService.remove(userRoleLbqWrapper);

        return true;
    }

    /**
     * 查询用户拥有的角色
     *
     * @param userId
     * @return
     */
    @Override
    public List<Role> findRoleByUserId(Long userId) {

        return baseMapper.findRoleByUserId(userId);
    }

    /**
     * 保存角色
     *
     * @param roleSaveDTO
     * @param userId
     */
    @Override
    public void saveRole(RoleSaveDTO roleSaveDTO, Long userId) {
        Role role = dozerUtils.map(roleSaveDTO, Role.class);
        role.setCode(StrHelper.getOrDef(roleSaveDTO.getCode(), codeGenerate.next()));
        role.setReadonly(false);

        //保存角色
        save(role);
        //保存角色与组织的关系
        saveRoleOrg(userId, role, roleSaveDTO.getOrgList());
    }

    /**
     * 保存角色与组织的关系
     */
    private void saveRoleOrg(Long userId, Role role, List<Long> orgList) {
        if (orgList != null && !orgList.isEmpty()) {
            List<RoleOrg> roleOrgList = orgList.stream()
                    .map(orgId -> RoleOrg.builder()
                            .orgId(orgId)
                            .roleId(role.getId())
                            .build())
                    .collect(Collectors.toList());
            roleOrgService.saveBatch(roleOrgList);
        }

    }

    /**
     * 修改角色
     *
     * @param roleUpdateDTO
     * @param userId
     */
    @Override
    public void updateRole(RoleUpdateDTO roleUpdateDTO, Long userId) {
        Role role = dozerUtils.map(roleUpdateDTO, Role.class);
        updateById(role);

        LbqWrapper<RoleOrg> query = Wraps.<RoleOrg>lbQ().eq(RoleOrg::getRoleId, roleUpdateDTO.getId());
        roleOrgService.remove(query);
        saveRoleOrg(userId, role, roleUpdateDTO.getOrgList());

    }

    /**
     * 根据角色编码查询用户ID
     *
     * @param codes
     * @return
     */
    @Override
    public List<Long> findUserIdByCode(String[] codes) {
        return baseMapper.findUserIdByCode(codes);
    }

    /**
     * 检测编码重复 存在返回真
     *
     * @param code
     * @return
     */
    @Override
    public Boolean check(String code) {

        LbqWrapper<Role> query = Wraps.<Role>lbQ().eq(Role::getCode, code);
        int result = count(query);

        return result > 0;
    }
}
