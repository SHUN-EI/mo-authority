package com.mo.authority.service.auth.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mo.auth.server.utils.JwtTokenServerUtils;
import com.mo.auth.utils.JwtUserInfo;
import com.mo.auth.utils.Token;
import com.mo.authority.dto.auth.LoginDTO;
import com.mo.authority.dto.auth.ResourceQueryDTO;
import com.mo.authority.dto.auth.UserDTO;
import com.mo.authority.entity.auth.Resource;
import com.mo.authority.entity.auth.User;
import com.mo.authority.service.auth.LoginService;
import com.mo.authority.service.auth.ResourceService;
import com.mo.authority.service.auth.UserService;
import com.mo.base.R;
import com.mo.common.constant.CacheKey;
import com.mo.dozer.DozerUtils;
import com.mo.exception.code.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mo on 2023/12/3
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenServerUtils jwtTokenServerUtils;
    @Autowired
    private DozerUtils dozerUtils;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private CacheChannel cacheChannel;

    /**
     * 登录认证
     *
     * @param account
     * @param password
     * @return
     */
    @Override
    public R<LoginDTO> login(String account, String password) {

        //校验账号、密码是否正确
        R<User> user = checkAccount(account, password);

        //判断是认证失败还是认证成功
        Boolean isError = user.getIsError();

        if (isError) {
            return R.fail("认证失败");
        }

        //为用户生成jwt令牌
        User userData = user.getData();
        Token token = generateUserToken(userData);

        //查询当前用户可以访问的资源权限
        ResourceQueryDTO resourceQueryDTO = ResourceQueryDTO.builder().userId(userData.getId()).build();

        List<Resource> userResource = resourceService.findVisibleResource(resourceQueryDTO);
        log.info("当前用户拥有的资源权限为：" + userResource);

        List<String> permissionList = null;
        if (userResource != null && userResource.size() > 0) {
            //用户对应的权限（给前端使用的,对应的是 auth_source表中的code字段
            permissionList = userResource.stream()
                    .map(Resource::getCode)
                    .collect(Collectors.toList());

            //将用户对应的权限（给后端网关使用的）进行缓存,对应的是 auth_source表中的 method+url字段
            List<String> visibleResource = userResource.stream()
                    .map(resource -> resource.getMethod() + resource.getUrl())
                    .collect(Collectors.toList());

            //缓存权限数据
            cacheChannel.set(CacheKey.USER_RESOURCE, userData.getId().toString(), visibleResource);

        }


        //封装返回结果
        LoginDTO loginDTO = LoginDTO.builder()
                .user(dozerUtils.map(userData, UserDTO.class))
                .token(token)
                .permissionsList(permissionList)
                .build();

        return R.success(loginDTO);
    }


    /**
     * 为当前登录用户生成对应的jwt令牌
     *
     * @param user
     * @return
     */
    private Token generateUserToken(User user) {

        JwtUserInfo jwtUserInfo = new JwtUserInfo(user.getId(), user.getAccount(), user.getName(), user.getOrgId(), user.getStationId());
        Token token = jwtTokenServerUtils.generateUserToken(jwtUserInfo, null);
        return token;

    }

    /**
     * 校验账号、密码是否正确
     *
     * @param account
     * @param password
     * @return
     */
    private R<User> checkAccount(String account, String password) {

        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getAccount, account));

        //将前端提交的密码进行md5加密
        String md5pwd = DigestUtils.md5Hex(password);

        if (null == user || !user.getPassword().equals(md5pwd)) {
            //认证失败
            return R.fail(ExceptionCode.JWT_USER_INVALID);
        }
        //认证成功
        return R.success(user);
    }
}
