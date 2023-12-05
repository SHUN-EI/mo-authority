package com.mo.authority.controller.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.authority.dto.auth.*;
import com.mo.authority.entity.auth.Role;
import com.mo.authority.entity.auth.RoleAuthority;
import com.mo.authority.entity.auth.UserRole;
import com.mo.authority.enumeration.auth.AuthorizeType;
import com.mo.authority.service.auth.RoleAuthorityService;
import com.mo.authority.service.auth.RoleOrgService;
import com.mo.authority.service.auth.RoleService;
import com.mo.authority.service.auth.UserRoleService;
import com.mo.base.BaseController;
import com.mo.base.R;
import com.mo.base.entity.SuperEntity;
import com.mo.databases.mybatis.conditions.Wraps;
import com.mo.databases.mybatis.conditions.query.LbqWrapper;
import com.mo.dozer.DozerUtils;
import com.mo.log.annotation.SysLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mo on 2023/12/5
 * 角色-控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/role")
@Api(value = "Role", tags = "角色")
public class RoleController extends BaseController {

    @Autowired
    private DozerUtils dozerUtils;
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleOrgService roleOrgService;
    @Autowired
    private RoleAuthorityService roleAuthorityService;
    @Autowired
    private UserRoleService userRoleService;


    @SysLog("分页查询角色")
    @ApiOperation(value = "分页查询角色", notes = "分页查询角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页显示几条", dataType = "long", paramType = "query", defaultValue = "10"),
    })
    @GetMapping("/page")
    public R<IPage<Role>> page(RolePageDTO rolePageDTO) {

        IPage<Role> page = getPage();
        Role role = dozerUtils.map(rolePageDTO, Role.class);

        // 构建值不为null的查询条件
        LbqWrapper<Role> query = Wraps.lbQ(role)
                .geHeader(Role::getCreateTime, rolePageDTO.getStartCreateTime())
                .leFooter(Role::getCreateTime, rolePageDTO.getEndCreateTime())
                .orderByDesc(Role::getId);

        IPage<Role> rolePage = roleService.page(page, query);

        return success(rolePage);

    }

    @ApiOperation(value = "查询角色", notes = "查询角色")
    @GetMapping("/{id}")
    @SysLog("查询角色")
    public R<RoleQueryDTO> get(@PathVariable Long id) {

        Role role = roleService.getById(id);
        RoleQueryDTO roleQueryDTO = dozerUtils.map(role, RoleQueryDTO.class);
        List<Long> orgList = roleOrgService.listOrgByRoleId(role.getId());
        roleQueryDTO.setOrgList(orgList);

        return success(roleQueryDTO);
    }

    @ApiOperation(value = "检测角色编码", notes = "检测角色编码")
    @GetMapping("/check/{code}")
    @SysLog("新增角色")
    public R<Boolean> check(@PathVariable String code) {
        Boolean result = roleService.check(code);

        return success(result);

    }

    @ApiOperation(value = "新增角色", notes = "新增角色不为空的字段")
    @PostMapping
    @SysLog("新增角色")
    public R<RoleSaveDTO> save(@RequestBody @Validated RoleSaveDTO roleSaveDTO) {
        roleService.saveRole(roleSaveDTO, getUserId());

        return success(roleSaveDTO);
    }

    @ApiOperation(value = "修改角色", notes = "修改角色不为空的字段")
    @PutMapping
    @SysLog("修改角色")
    public R<RoleUpdateDTO> update(@RequestBody @Validated(SuperEntity.Update.class) RoleUpdateDTO roleUpdateDTO) {

        roleService.updateRole(roleUpdateDTO, getUserId());
        return success(roleUpdateDTO);
    }


    @ApiOperation(value = "删除角色", notes = "根据id物理删除角色")
    @DeleteMapping
    @SysLog("删除角色")
    public R<Boolean> delete(@RequestParam("ids[]") List<Long> ids) {

        roleService.removeById(ids);

        return success(true);
    }

    @ApiOperation(value = "给用户分配角色", notes = "给用户分配角色")
    @PostMapping("/user")
    @SysLog("给角色分配用户")
    public R<Boolean> saveUserRole(@RequestBody UserRoleSaveDTO userRoleSaveDTO) {
        Boolean result = roleAuthorityService.saveUserRole(userRoleSaveDTO);

        return success(result);

    }

    @ApiOperation(value = "查询角色的用户", notes = "查询角色的用户")
    @GetMapping("/user/{roleId}")
    @SysLog("查询角色的用户")
    public R<List<Long>> findUserIdByRoleId(@PathVariable Long roleId) {

        LbqWrapper<UserRole> query = Wraps.<UserRole>lbQ().eq(UserRole::getRoleId, roleId);
        List<UserRole> userRoleList = userRoleService.list(query);

        List<Long> list = userRoleList.stream()
                .mapToLong(UserRole::getUserId).boxed()
                .collect(Collectors.toList());

        return success(list);
    }

    @ApiOperation(value = "查询角色拥有的资源id集合", notes = "查询角色拥有的资源id集合")
    @GetMapping("/authority/{roleId}")
    @SysLog("查询角色拥有的资源")
    public R<RoleAuthoritySaveDTO> findAuthorityIdByRoleId(@PathVariable Long roleId) {

        LbqWrapper<RoleAuthority> query = Wraps.<RoleAuthority>lbQ().eq(RoleAuthority::getRoleId, roleId);

        List<RoleAuthority> roleAuthorities = roleAuthorityService.list(query);
        List<Long> menuIdList = roleAuthorities.stream()
                .filter(item -> AuthorizeType.MENU.eq(item.getAuthorityType()))
                .mapToLong(RoleAuthority::getAuthorityId)
                .boxed()
                .collect(Collectors.toList());

        List<Long> resourceIdList = roleAuthorities.stream()
                .filter(item -> AuthorizeType.RESOURCE.eq(item.getAuthorityType()))
                .mapToLong(RoleAuthority::getAuthorityId)
                .boxed()
                .collect(Collectors.toList());

        RoleAuthoritySaveDTO roleAuthoritySaveDTO = RoleAuthoritySaveDTO.builder()
                .menuIdList(menuIdList)
                .resourceIdList(resourceIdList)
                .build();

        return success(roleAuthoritySaveDTO);

    }

    @ApiOperation(value = "给角色配置权限", notes = "给角色配置权限")
    @PostMapping("/authority")
    @SysLog("给角色配置权限")
    public R<Boolean> saveRoleAuthority(@RequestBody RoleAuthoritySaveDTO roleAuthoritySaveDTO) {

        Boolean result = roleAuthorityService.saveRoleAuthority(roleAuthoritySaveDTO);
        return success(result);
    }

    @ApiOperation(value = "根据角色编码查询用户ID", notes = "根据角色编码查询用户ID")
    @GetMapping("/codes")
    @SysLog("根据角色编码查询用户ID")
    public R<List<Long>> findUserIdByCode(@RequestParam("codes") String[] codes) {
        List<Long> codeList = roleService.findUserIdByCode(codes);

        return success(codeList);
    }


}
