package com.mo.authority.service.auth.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.authority.entity.auth.User;
import com.mo.authority.mapper.auth.UserMapper;
import com.mo.authority.service.auth.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by mo on 2023/12/3
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
