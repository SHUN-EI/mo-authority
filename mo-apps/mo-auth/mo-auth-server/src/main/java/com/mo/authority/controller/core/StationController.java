package com.mo.authority.controller.core;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.authority.dto.core.StationPageDTO;
import com.mo.authority.dto.core.StationSaveDTO;
import com.mo.authority.dto.core.StationUpdateDTO;
import com.mo.authority.entity.core.Station;
import com.mo.authority.service.core.StationService;
import com.mo.base.BaseController;
import com.mo.base.R;
import com.mo.base.entity.SuperEntity;
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

/**
 * Created by mo on 2023/12/5
 * 岗位-控制器
 */
@Slf4j
@Api(value = "Station", tags = "岗位")
@RestController
@RequestMapping("/station")
public class StationController extends BaseController {

    @Autowired
    private StationService stationService;
    @Autowired
    private DozerUtils dozerUtils;


    @SysLog("分页查询岗位")
    @ApiOperation(value = "分页查询岗位", notes = "分页查询岗位")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页显示几条", dataType = "long", paramType = "query", defaultValue = "10"),
    })
    @GetMapping("/page")
    public R<IPage<Station>> page(@RequestBody StationPageDTO stationPageDTO) {
        Page<Station> page = getPage();

        IPage<Station> stationPage = stationService.findStationPage(page, stationPageDTO);

        return success(stationPage);

    }

    @SysLog("查询岗位")
    @ApiOperation(value = "查询岗位", notes = "查询岗位")
    @GetMapping("/{id}")
    public R<Station> get(@PathVariable Long id) {
        Station station = stationService.getById(id);

        return success(station);
    }

    @SysLog("新增岗位")
    @ApiOperation(value = "新增岗位", notes = "新增岗位不为空的字段")
    @PostMapping("/save")
    public R<Station> save(@RequestBody @Validated StationSaveDTO stationSaveDTO) {
        Station station = dozerUtils.map(stationSaveDTO, Station.class);
        stationService.save(station);

        return success(station);

    }

    @ApiOperation(value = "修改岗位", notes = "修改岗位不为空的字段")
    @PutMapping
    @SysLog("修改岗位")
    public R<Station> update(@RequestBody @Validated(SuperEntity.Update.class) StationUpdateDTO stationUpdateDTO) {

        Station station = dozerUtils.map(stationUpdateDTO, Station.class);
        stationService.updateById(station);

        return success(station);
    }

    @ApiOperation(value = "删除岗位", notes = "根据id物理删除岗位")
    @SysLog("删除岗位")
    @DeleteMapping
    public R<Boolean> delete(@RequestParam("ids[]") List<Long> ids) {
        stationService.removeByIds(ids);

        return success(true);

    }
}
