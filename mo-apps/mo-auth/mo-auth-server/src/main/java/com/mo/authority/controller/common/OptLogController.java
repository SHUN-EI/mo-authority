package com.mo.authority.controller.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.authority.entity.common.OptLog;
import com.mo.authority.service.common.OptLogService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by mo on 2023/12/6
 * 系统操作日志-控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/optLog")
@Api(value = "OptLog", tags = "系统操作日志")
public class OptLogController extends BaseController {

    @Autowired
    private OptLogService optLogService;


    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页显示几条", dataType = "long", paramType = "query", defaultValue = "10"),
    })
    @ApiOperation(value = "分页查询系统操作日志", notes = "分页查询系统操作日志")
    @GetMapping("/page")
    public R<IPage<OptLog>> page(OptLog optLog) {

        IPage<OptLog> page = getPage();
        // 构建值不为null的查询条件
        LbqWrapper<OptLog> query = Wraps.lbQ(optLog)
                .leFooter(OptLog::getCreateTime, getEndCreateTime())
                .geHeader(OptLog::getCreateTime, getStartCreateTime())
                .orderByDesc(OptLog::getId);

        IPage<OptLog> optLogPage = optLogService.page(page, query);
        return success(optLogPage);
    }


    @ApiOperation(value = "查询系统操作日志", notes = "查询系统操作日志")
    @GetMapping("/{id}")
    public R<OptLog> get(@PathVariable Long id) {

        OptLog optLog = optLogService.getById(id);
        return success(optLog);
    }

    @ApiOperation(value = "删除系统操作日志", notes = "根据id物理删除系统操作日志")
    @DeleteMapping
    @SysLog("删除系统操作日志")
    public R<Boolean> delete(@RequestParam("ids[]")List<Long> ids) {
        optLogService.removeByIds(ids);

        return success(true);


    }

}
