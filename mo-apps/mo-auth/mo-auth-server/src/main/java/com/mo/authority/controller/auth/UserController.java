package com.mo.authority.controller.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mo.authority.dto.auth.*;
import com.mo.authority.entity.auth.Role;
import com.mo.authority.entity.auth.User;
import com.mo.authority.entity.core.Org;
import com.mo.authority.entity.core.Station;
import com.mo.authority.service.auth.RoleService;
import com.mo.authority.service.auth.UserService;
import com.mo.authority.service.core.OrgService;
import com.mo.authority.service.core.StationService;
import com.mo.base.BaseController;
import com.mo.base.R;
import com.mo.base.entity.SuperEntity;
import com.mo.databases.mybatis.conditions.Wraps;
import com.mo.databases.mybatis.conditions.query.LbqWrapper;
import com.mo.dozer.DozerUtils;
import com.mo.log.annotation.SysLog;
import com.mo.user.feign.UserQuery;
import com.mo.user.model.SysOrg;
import com.mo.user.model.SysRole;
import com.mo.user.model.SysStation;
import com.mo.user.model.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mo on 2023/12/4
 * 用户-控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/user")
@Api(value = "User", tags = "用户")
public class UserController extends BaseController {

    @Autowired
    private DozerUtils dozerUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private StationService stationService;
    @Autowired
    private RoleService roleService;


    @ApiOperation(value = "分页查询用户", notes = "分页查询用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码", dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "分页条数", dataType = "long", paramType = "query", defaultValue = "10"),
    })
    @GetMapping("/page")
    @SysLog("分页查询用户")
    public R<IPage<User>> page(UserPageDTO userPage) {

        IPage<User> page = getPage();
        User user = dozerUtils.map2(userPage, User.class);

        if (userPage.getOrgId() != null && userPage.getOrgId() >= 0) {
            user.setOrgId(null);
        }

        LbqWrapper<User> wrapper = Wraps.lbQ(user);
        if (userPage.getOrgId() != null && userPage.getOrgId() >= 0) {
            List<Org> children = orgService.findChildren(Arrays.asList(userPage.getOrgId()));

            List<Long> list = children.stream().mapToLong(Org::getId).boxed().collect(Collectors.toList());
            wrapper.in(User::getOrgId, list);
        }

        wrapper.geHeader(User::getCreateTime, userPage.getStartCreateTime())
                .leFooter(User::getCreateTime, userPage.getEndCreateTime())
                .like(User::getName, userPage.getName())
                .like(User::getAccount, userPage.getAccount())
                .like(User::getEmail, userPage.getEmail())
                .like(User::getMobile, userPage.getMobile())
                .eq(User::getSex, userPage.getSex())
                .eq(User::getStatus, userPage.getStatus())
                .orderByDesc(User::getId);
        IPage<User> userIPage = userService.findPage(page, wrapper);

        return success(userIPage);

    }

    @ApiOperation(value = "查询用户", notes = "查询用户")
    @SysLog("查询用户")
    @GetMapping("/{id}")
    public R<User> get(@PathVariable Long id) {
        User user = userService.getById(id);
        return success(user);
    }


    @ApiOperation(value = "查询所有用户", notes = "查询所有用户")
    @SysLog("查询所有用户")
    @GetMapping("/find")
    public R<List<Long>> findAllUserId() {

        List<Long> list = userService.list().stream()
                .mapToLong(User::getId).boxed()
                .collect(Collectors.toList());

        return success(list);
    }


    @ApiOperation(value = "新增用户", notes = "新增用户不为空的字段")
    @SysLog("新增用户")
    @PostMapping("/save")
    public R<User> save(@RequestBody @Validated UserSaveDTO userSaveDTO) {
        User user = dozerUtils.map(userSaveDTO, User.class);
        userService.save(user);

        return success(user);
    }


    @ApiOperation(value = "修改用户", notes = "修改用户不为空的字段")
    @PutMapping
    @SysLog("修改用户")
    public R<User> update(@RequestBody @Validated(SuperEntity.Update.class) UserUpdateDTO userUpdateDTO) {

        User user = dozerUtils.map(userUpdateDTO, User.class);
        userService.updateUser(user);

        return success(user);
    }


    @ApiOperation(value = "修改头像", notes = "修改头像")
    @PutMapping("/avatar")
    @SysLog("修改头像")
    public R<User> avatar(@RequestBody @Validated(SuperEntity.Update.class) UserUpdateAvatarDTO userUpdateAvatarDTO) {

        User user = dozerUtils.map(userUpdateAvatarDTO, User.class);

        userService.updateUser(user);
        return success(user);
    }

    @ApiOperation(value = "修改密码", notes = "修改密码")
    @PutMapping("/password")
    @SysLog("修改密码")
    public R<Boolean> updatePassword(@RequestBody @Validated(SuperEntity.Update.class) UserUpdatePasswordDTO userUpdatePasswordDTO) {

        Boolean result = userService.updatePassword(userUpdatePasswordDTO);

        return success(result);
    }


    @ApiOperation(value = "重置密码", notes = "重置密码")
    @GetMapping("/reset")
    @SysLog("重置密码")
    public R<Boolean> resetTx(@RequestParam("ids[]") List<Long> ids) {

        Boolean result = userService.reset(ids);
        return success();
    }


    @ApiOperation(value = "删除用户", notes = "根据id物理删除用户")
    @DeleteMapping
    @SysLog("删除用户")
    public R<Boolean> delete(@RequestParam("ids[]") List<Long> ids) {
        Boolean result = userService.remove(ids);
        return success(result);
    }


    @ApiOperation(value = "查询用户详细", notes = "查询用户详细")
    @PostMapping(value = "/anno/id/{id}")
    public R<SysUser> getById(@PathVariable Long id, @RequestBody UserQuery query) {

        User user = userService.getById(id);
        if (null == user) {
            return success(null);
        }

        SysUser sysUser = dozerUtils.map(user, SysUser.class);

        if (query.getFull() || query.getOrg()) {

            Org org = orgService.getById(user.getOrgId());
            SysOrg sysOrg = dozerUtils.map(org, SysOrg.class);
            sysUser.setOrg(sysOrg);
        }

        if (query.getFull() || query.getStation()) {
            Station station = stationService.getById(user.getStationId());
            SysStation sysStation = dozerUtils.map(station, SysStation.class);
            sysUser.setStation(sysStation);
        }

        // 设置角色
        if (query.getFull() || query.getRoles()) {
            List<Role> roles = roleService.findRoleByUserId(id);
            List<SysRole> sysRoles = dozerUtils.mapList(roles, SysRole.class);
            sysUser.setRoles(sysRoles);
        }
        return success(sysUser);
    }

    @ApiOperation(value = "查询角色的已关联用户", notes = "查询角色的已关联用户")
    @GetMapping(value = "/role/{roleId}")
    public R<UserRoleDTO> findUserByRoleId(@PathVariable("roleId") Long roleId,
                                           @RequestParam(value = "keyword", required = false) String keyword) {


        List<User> userList = userService.findUserByRoleId(roleId, keyword);

        List<Long> idList = userList.stream().mapToLong(User::getId).boxed()
                .collect(Collectors.toList());

        UserRoleDTO userRoleDTO = UserRoleDTO.builder()
                .idList(idList).build();

        return success(userRoleDTO);
    }

}
