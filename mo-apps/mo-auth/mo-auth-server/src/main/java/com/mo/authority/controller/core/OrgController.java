package com.mo.authority.controller.core;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.authority.dto.core.OrgSaveDTO;
import com.mo.authority.dto.core.OrgTreeDTO;
import com.mo.authority.dto.core.OrgUpdateDTO;
import com.mo.authority.entity.core.Org;
import com.mo.authority.service.core.OrgService;
import com.mo.base.BaseController;
import com.mo.base.R;
import com.mo.base.entity.SuperEntity;
import com.mo.databases.mybatis.conditions.Wraps;
import com.mo.databases.mybatis.conditions.query.LbqWrapper;
import com.mo.dozer.DozerUtils;
import com.mo.log.annotation.SysLog;
import com.mo.utils.BizAssert;
import com.mo.utils.TreeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mo.utils.StrPool.DEF_PARENT_ID;
import static com.mo.utils.StrPool.DEF_ROOT_PATH;

/**
 * Created by mo on 2023/12/4
 * 组织-控制器
 */
@Slf4j
@RestController
@RequestMapping("/org")
@Api(value = "Org", tags = "组织")
public class OrgController extends BaseController {

    @Autowired
    private OrgService orgService;
    @Autowired
    private DozerUtils dozerUtils;

    @ApiOperation(value = "分页查询组织", notes = "分页查询组织")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页显示几条", dataType = "long", paramType = "query", defaultValue = "10"),
    })
    @GetMapping("/page")
    @SysLog("分页查询组织")
    public R<IPage<Org>> page(Org org) {

        IPage<Org> page = this.getPage();
        // 构建值不为null的查询条件

        LbqWrapper<Org> orgLbqWrapper = Wraps.lbQ(org);
        IPage<Org> pageResult = orgService.page(page, orgLbqWrapper);

        return success(pageResult);
    }


    @SysLog("查询组织")
    @ApiOperation(value = "查询组织", notes = "查询组织")
    @GetMapping("/{id}")
    public R<Org> get(@PathVariable Long id) {

        Org org = orgService.getById(id);
        return success(org);

    }

    @ApiOperation(value = "新增组织", notes = "新增组织不为空的字段")
    @PostMapping("/save")
    @SysLog("新增组织")
    public R<Org> save(@RequestBody @Validated OrgSaveDTO orgSaveDTO) {

        Org org = dozerUtils.map(orgSaveDTO, Org.class);
        if (org.getParentId() == null || org.getParentId() <= 0) {
            org.setParentId(DEF_PARENT_ID);
            org.setTreePath(DEF_ROOT_PATH);
        } else {

            Org parentOrg = orgService.getById(org.getParentId());
            BizAssert.notNull(parentOrg, "父组织不能为空");

            String treePath = StringUtils.join(parentOrg.getTreePath(), parentOrg.getId(), DEF_ROOT_PATH);
            org.setTreePath(treePath);
        }

        orgService.save(org);

        return success(org);
    }

    @ApiOperation(value = "修改组织", notes = "修改组织不为空的字段")
    @PutMapping("/update")
    @SysLog("修改组织")
    public R<Org> update(@RequestBody @Validated(SuperEntity.Update.class) OrgUpdateDTO orgUpdateDTO) {

        Org org = dozerUtils.map(orgUpdateDTO, Org.class);
        orgService.updateById(org);
        return success(org);
    }

    @ApiOperation(value = "删除组织", notes = "根据id物理删除组织")
    @SysLog("删除组织")
    @DeleteMapping("/delete")
    public R<Boolean> delete(@RequestParam("ids[]") List<Long> ids) {
        orgService.remove(ids);

        return success(true);

    }

    @ApiOperation(value = "查询系统所有的组织树", notes = "查询系统所有的组织树")
    @GetMapping("/tree")
    @SysLog("查询系统所有的组织树")
    public R<List<OrgTreeDTO>> tree(@RequestParam(value = "name", required = false) String name,
                                    @RequestParam(value = "status", required = false) Boolean status) {


        LbqWrapper<Org> query = Wraps.<Org>lbQ().like(Org::getName, name)
                .eq(Org::getStatus, status)
                .orderByAsc(Org::getSortValue);
        List<Org> orgList = orgService.list(query);

        List<OrgTreeDTO> orgTreeDTOList = dozerUtils.mapList(orgList, OrgTreeDTO.class);
        List<OrgTreeDTO> treeList = TreeUtil.build(orgTreeDTOList);

        return success(treeList);
    }
}
