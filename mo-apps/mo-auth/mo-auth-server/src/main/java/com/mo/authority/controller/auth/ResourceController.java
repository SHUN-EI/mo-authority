package com.mo.authority.controller.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.authority.dto.auth.ResourceQueryDTO;
import com.mo.authority.dto.auth.ResourceSaveDTO;
import com.mo.authority.dto.auth.ResourceUpdateDTO;
import com.mo.authority.entity.auth.Resource;
import com.mo.authority.service.auth.ResourceService;
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
 * Created by mo on 2023/12/4
 * 资源-控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/resource")
@Api(value = "Resource", tags = "资源")
public class ResourceController extends BaseController {

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private DozerUtils dozerUtils;


    @ApiOperation(value = "分页查询资源", notes = "分页查询资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页显示几条", dataType = "long", paramType = "query", defaultValue = "10"),
    })
    @GetMapping("/page")
    @SysLog("分页查询资源")
    public R<IPage<Resource>> page(Resource resource) {
        IPage<Resource> page = getPage();

        // 构建值不为null的查询条件
        LbqWrapper<Resource> resourceLbqWrapper = Wraps.lbQ(resource);
        resourceService.page(page, resourceLbqWrapper);

        return success(page);

    }


    @SysLog("查询资源")
    @ApiOperation(value = "查询资源", notes = "查询资源")
    @GetMapping("/{id}")
    public R<Resource> get(@PathVariable Long id) {

        Resource resource = resourceService.getById(id);
        return success(resource);

    }

    @ApiOperation(value = "新增资源", notes = "新增资源不为空的字段")
    @PostMapping("/save")
    @SysLog("新增资源")
    public R<Resource> save(@RequestBody @Validated ResourceSaveDTO resourceSaveDTO) {
        Resource resource = dozerUtils.map(resourceSaveDTO, Resource.class);
        resourceService.save(resource);

        return success(resource);

    }


    @ApiOperation(value = "修改资源", notes = "修改资源不为空的字段")
    @PutMapping("/update")
    @SysLog("修改资源")
    public R<Resource> update(@RequestBody @Validated(SuperEntity.Update.class) ResourceUpdateDTO resourceUpdateDTO) {

        Resource resource = dozerUtils.map(resourceUpdateDTO, Resource.class);
        resourceService.updateById(resource);
        return success(resource);
    }

    /**
     * 删除资源
     * 链接类型的资源 只清空 menu_id
     * 按钮和数据列 则物理删除
     */
    @ApiOperation(value = "删除资源", notes = "根据id物理删除资源")
    @DeleteMapping("/delete")
    @SysLog("删除资源")
    public R<Boolean> delete(@RequestParam("ids[]") List<Long> ids) {

        Boolean result = resourceService.removeByIds(ids);
        return success(result);

    }


    @SysLog("查询用户可用的所有资源")
    @ApiOperation(value = "查询用户可用的所有资源", notes = "查询用户可用的所有资源")
    @GetMapping("/visible")
    public R<List<Resource>> visible(ResourceQueryDTO resourceQueryDTO) {

        if (null == resourceQueryDTO) {
            resourceQueryDTO = new ResourceQueryDTO();
        }

        //获取当前用户id
        if (null == resourceQueryDTO.getUserId()) {
            resourceQueryDTO.setUserId(getUserId());
        }

        List<Resource> visibleResources = resourceService.findVisibleResource(resourceQueryDTO);

        return success(visibleResources);

    }

    @SysLog("查询所有资源")
    @ApiOperation(value = "查询所有资源", notes = "查询所有资源")
    @GetMapping("/list")
    public R<List> list() {

        List<Resource> resourceList = resourceService.list();

        List<String> resources = resourceList.stream()
                .map(resource -> resource.getMethod() + resource.getUrl())
                .collect(Collectors.toList());

        return success(resources);

    }


}
