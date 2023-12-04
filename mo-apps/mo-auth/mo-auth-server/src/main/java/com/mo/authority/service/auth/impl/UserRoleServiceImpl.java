package com.mo.authority.service.auth.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.authority.entity.auth.UserRole;
import com.mo.authority.mapper.auth.UserRoleMapper;
import com.mo.authority.service.auth.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * Created by mo on 2023/12/4
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}
