package com.mo.authority.controller.auth;

import com.mo.authority.entity.auth.RoleAuthority;
import com.mo.authority.service.auth.RoleAuthorityService;
import com.mo.base.BaseController;
import com.mo.base.R;
import com.mo.databases.mybatis.conditions.Wraps;
import com.mo.databases.mybatis.conditions.query.LbqWrapper;
import com.mo.log.annotation.SysLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by mo on 2023/12/5
 * 角色资源-控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/roleAuthority")
@Api(value = "RoleAuthority", tags = "角色的资源")
public class RoleAuthorityController extends BaseController {

    @Autowired
    private RoleAuthorityService roleAuthorityService;

    @ApiOperation(value = "查询指定角色关联的菜单和资源", notes = "查询指定角色关联的菜单和资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页显示几条", dataType = "long", paramType = "query", defaultValue = "10"),
    })
    @SysLog("查询指定角色关联的菜单和资源")
    @GetMapping("/{roleId}")
    public R<List<RoleAuthority>> page(@PathVariable Long roleId) {

        LbqWrapper<RoleAuthority> query = Wraps.<RoleAuthority>lbQ().eq(RoleAuthority::getRoleId, roleId);

        List<RoleAuthority> roleAuthorities = roleAuthorityService.list(query);

        return success(roleAuthorities);
    }
}
