package com.mo.authority.service.auth.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.authority.entity.auth.RoleOrg;
import com.mo.authority.mapper.auth.RoleOrgMapper;
import com.mo.authority.service.auth.RoleOrgService;
import com.mo.databases.mybatis.conditions.Wraps;
import com.mo.databases.mybatis.conditions.query.LbqWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mo on 2023/12/5
 * 角色组织关系-业务实现类
 */
@Slf4j
@Service
public class RoleOrgServiceImpl extends ServiceImpl<RoleOrgMapper, RoleOrg> implements RoleOrgService {

    /**
     * 根据角色id查询
     *
     * @param id
     * @return
     */
    @Override
    public List<Long> listOrgByRoleId(Long id) {
        List<RoleOrg> roleOrgList = list(Wraps.<RoleOrg>lbQ().eq(RoleOrg::getRoleId, id));

        List<Long> orgList = roleOrgList.stream()
                .mapToLong(RoleOrg::getOrgId)
                .boxed()
                .collect(Collectors.toList());

        return orgList;
    }
}
