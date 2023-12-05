package com.mo.authority.service.auth.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.authority.dto.auth.RoleAuthoritySaveDTO;
import com.mo.authority.dto.auth.UserRoleSaveDTO;
import com.mo.authority.entity.auth.RoleAuthority;
import com.mo.authority.entity.auth.UserRole;
import com.mo.authority.enumeration.auth.AuthorizeType;
import com.mo.authority.mapper.auth.RoleAuthorityMapper;
import com.mo.authority.service.auth.ResourceService;
import com.mo.authority.service.auth.RoleAuthorityService;
import com.mo.authority.service.auth.UserRoleService;
import com.mo.common.constant.CacheKey;
import com.mo.databases.mybatis.conditions.Wraps;
import com.mo.databases.mybatis.conditions.query.LbqWrapper;
import com.mo.utils.NumberHelper;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mo on 2023/12/5
 * 角色资源关系-业务实现类
 */
@Slf4j
@Service
public class RoleAuthorityServiceImpl extends ServiceImpl<RoleAuthorityMapper, RoleAuthority> implements RoleAuthorityService {

    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private CacheChannel cacheChannel;
    @Autowired
    private ResourceService resourceService;

    /**
     * 给用户分配角色
     *
     * @param userRoleSaveDTO
     * @return
     */
    @Override
    public Boolean saveUserRole(UserRoleSaveDTO userRoleSaveDTO) {

        LbqWrapper<UserRole> userRoleLbqWrapper = Wraps.<UserRole>lbQ().eq(UserRole::getRoleId, userRoleSaveDTO.getRoleId());
        userRoleService.remove(userRoleLbqWrapper);

        List<Long> userIdList = userRoleSaveDTO.getUserIdList();
        List<UserRole> userRoleList = userIdList
                .stream()
                .map(userId -> UserRole.builder()
                        .userId(userId)
                        .roleId(userRoleSaveDTO.getRoleId())
                        .build())
                .collect(Collectors.toList());

        userRoleService.saveBatch(userRoleList);

        //清除 用户拥有的资源列表
        userIdList.forEach(userId -> {
            String key = CacheKey.buildKey(userId);
            cacheChannel.evict(CacheKey.USER_RESOURCE, key);

        });

        return true;
    }

    /**
     * 给角色重新分配 权限（资源/菜单）
     *
     * @param roleAuthoritySaveDTO
     * @return
     */
    @Override
    public Boolean saveRoleAuthority(RoleAuthoritySaveDTO roleAuthoritySaveDTO) {

        //删除角色和资源的关联
        LbqWrapper<RoleAuthority> query = Wraps.<RoleAuthority>lbQ().eq(RoleAuthority::getRoleId, roleAuthoritySaveDTO.getRoleId());
        remove(query);

        List<RoleAuthority> roleAuthorityList = new ArrayList<>();

        //资源id
        List<Long> resourceIdList = roleAuthoritySaveDTO.getResourceIdList();
        if (resourceIdList != null && !resourceIdList.isEmpty()) {

            List<Long> menuIdList = resourceService.findMenuIdByResourceId(resourceIdList);
            List<Long> menuIds = roleAuthoritySaveDTO.getMenuIdList();

            if (menuIds == null || menuIds.isEmpty()) {
                roleAuthoritySaveDTO.setMenuIdList(menuIdList);
            } else {
                menuIds.addAll(menuIdList);
            }

            //保存授予的资源
            List<RoleAuthority> roleAuthorities = roleAuthoritySaveDTO.getResourceIdList()
                    .stream()
                    .map(resourceId -> RoleAuthority.builder()
                            .authorityType(AuthorizeType.RESOURCE)
                            .authorityId(resourceId)
                            .roleId(roleAuthoritySaveDTO.getRoleId())
                            .build())
                    .collect(Collectors.toList());

            roleAuthorityList.addAll(roleAuthorities);
        }

        //菜单Id
        List<Long> menuIdList = roleAuthoritySaveDTO.getMenuIdList();
        if (menuIdList != null && !menuIdList.isEmpty()) {
            //保存授予的菜单
            List<RoleAuthority> authorities = menuIdList.stream()
                    .map(menuId -> RoleAuthority.builder()
                            .authorityType(AuthorizeType.MENU)
                            .authorityId(menuId)
                            .roleId(roleAuthoritySaveDTO.getRoleId())
                            .build())
                    .collect(Collectors.toList());
            roleAuthorityList.addAll(authorities);
        }

        super.saveBatch(roleAuthorityList);

        // 清理
        LbqWrapper<UserRole> lbqWrapper = Wraps.<UserRole>lbQ().select(UserRole::getUserId).eq(UserRole::getRoleId, roleAuthoritySaveDTO.getRoleId());

        List<Long> userIdList = userRoleService.listObjs(lbqWrapper, userId -> NumberHelper.longValueOf0(userId));

        userIdList.stream()
                .collect(Collectors.toSet())
                .forEach(userId -> {
                    log.info("清理了 {} 的菜单/资源", userId);
                    cacheChannel.evict(CacheKey.USER_RESOURCE, String.valueOf(userId));
                });
        return true;
    }
}
