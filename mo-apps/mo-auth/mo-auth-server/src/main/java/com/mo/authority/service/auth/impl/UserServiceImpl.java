package com.mo.authority.service.auth.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.authority.dto.auth.UserUpdatePasswordDTO;
import com.mo.authority.entity.auth.User;
import com.mo.authority.entity.auth.UserRole;
import com.mo.authority.mapper.auth.UserMapper;
import com.mo.authority.service.auth.UserRoleService;
import com.mo.authority.service.auth.UserService;
import com.mo.databases.mybatis.conditions.Wraps;
import com.mo.databases.mybatis.conditions.query.LbqWrapper;
import com.mo.utils.BizAssert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by mo on 2023/12/3
 * 用户-业务实现类
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserRoleService userRoleService;


    /**
     * 根据角色id 和 账号或名称 查询角色关联的用户
     * 注意，该接口只返回 id，账号，姓名，手机，性别
     *
     * @param roleId  角色id
     * @param keyword 账号或名称
     */
    @Override
    public List<User> findUserByRoleId(Long roleId, String keyword) {

        List<User> userList = baseMapper.findUserByRoleId(roleId, keyword);
        return userList;
    }

    /**
     * 修改输错密码的次数
     */
    @Override
    public void updatePasswordErrorNumById(Long id) {

        baseMapper.incrPasswordErrorNumById(id);

    }

    /**
     * 根据账号查询用户
     */
    @Override
    public User getByAccount(String account) {

        User user = super.getOne(Wraps.<User>lbQ().eq(User::getAccount, account));
        return user;
    }

    /**
     * 修改用户最后一次登录时间
     */
    @Override
    public void updateLoginTime(String account) {

        baseMapper.updateLastLoginTime(account, LocalDateTime.now());

    }

    /**
     * 保存
     */
    @Override
    public User saveUser(User user) {

        // 永不过期
        user.setPasswordExpireTime(null);
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        user.setPasswordErrorNum(0);

        super.save(user);

        return user;
    }

    /**
     * 重置密码
     */
    @Override
    public boolean reset(List<Long> ids) {
        LocalDateTime passwordExpireTime = null;

        String defPassword = "cea87ef1cb2e47570020bf7c014e1074";//pinda123
        super.update(Wraps.<User>lbU()
                .set(User::getPassword, defPassword)
                .set(User::getPasswordErrorNum, 0L)
                .set(User::getPasswordErrorLastTime, null)
                .set(User::getPasswordExpireTime, passwordExpireTime)
                .in(User::getId, ids)
        );

        return true;
    }

    /**
     * 修改
     */
    @Override
    public User updateUser(User user) {
        // 永不过期
        user.setPasswordExpireTime(null);

        if (StrUtil.isNotEmpty(user.getPassword())) {
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        }

        super.updateById(user);
        return user;
    }

    /**
     * 删除
     */
    @Override
    public boolean remove(List<Long> ids) {

        userRoleService.remove(Wraps.<UserRole>lbQ()
                .in(UserRole::getUserId, ids)
        );
        return super.removeByIds(ids);
    }

    /**
     * 分页查询
     */
    @Override
    public IPage<User> findPage(IPage<User> page, LbqWrapper<User> wrapper) {

        IPage<User> userPage = baseMapper.findPage(page, wrapper);

        return userPage;
    }

    /**
     * 修改密码
     */
    @Override
    public Boolean updatePassword(UserUpdatePasswordDTO userUpdatePasswordDTO) {
        BizAssert.equals(userUpdatePasswordDTO.getConfirmPassword(), userUpdatePasswordDTO.getPassword(), "密码与确认密码不一致");

        User user = getById(userUpdatePasswordDTO.getId());
        BizAssert.notNull(user, "用户不存在");

        String oldPassword = DigestUtils.md5Hex(userUpdatePasswordDTO.getOldPassword());
        BizAssert.equals(user.getPassword(), oldPassword, "旧密码错误");

        User updateUser = User.builder()
                .password(userUpdatePasswordDTO.getPassword())
                .id(userUpdatePasswordDTO.getId())
                .build();
        this.updateUser(updateUser);

        return true;
    }

    /**
     * 重置密码错误次数
     */
    @Override
    public int resetPassErrorNum(Long id) {
        return 0;
    }
}
