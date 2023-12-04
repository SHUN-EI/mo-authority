package com.mo.authority.service.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.authority.dto.auth.UserUpdatePasswordDTO;
import com.mo.authority.entity.auth.User;
import com.mo.databases.mybatis.conditions.query.LbqWrapper;

import java.util.List;

/**
 * Created by mo on 2023/12/3
 * 用户-业务接口
 */
public interface UserService extends IService<User> {

    List<User> findUserByRoleId(Long roleId, String keyword);

    void updatePasswordErrorNumById(Long id);

    User getByAccount(String account);

    void updateLoginTime(String account);

    User saveUser(User user);

    boolean reset(List<Long> ids);

    User updateUser(User user);

    boolean remove(List<Long> ids);

    IPage<User> findPage(IPage<User> page, LbqWrapper<User> wrapper);

    Boolean updatePassword(UserUpdatePasswordDTO updatePasswordDTO);

    int resetPassErrorNum(Long id);


}
