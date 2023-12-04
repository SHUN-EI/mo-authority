package com.mo.authority.service.auth.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.authority.dto.auth.ResourceQueryDTO;
import com.mo.authority.entity.auth.Resource;
import com.mo.authority.mapper.auth.ResourceMapper;
import com.mo.authority.service.auth.ResourceService;
import com.mo.base.id.CodeGenerate;
import com.mo.databases.mybatis.conditions.Wraps;
import com.mo.exception.BizException;
import com.mo.utils.StrHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mo on 2023/12/4
 * 资源-业务实现类
 */
@Slf4j
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {


    @Autowired
    private CodeGenerate codeGenerate;

    /**
     * 查询用户的可用资源
     *
     * @param resourceQueryDTO
     * @return
     */
    @Override
    public List<Resource> findVisibleResource(ResourceQueryDTO resourceQueryDTO) {

        List<Resource> visibleResource = baseMapper.findVisibleResource(resourceQueryDTO);
        return visibleResource;
    }

    /**
     * 根据菜单id删除资源
     *
     * @param menuIds
     */
    @Override
    public void removeByMenuId(List<Long> menuIds) {

        List<Resource> resources = super.list(Wraps.<Resource>lbQ().in(Resource::getMenuId, menuIds));
        if (resources.isEmpty()) return;

        List<Long> idList = resources.stream().mapToLong(Resource::getId).boxed().collect(Collectors.toList());
        super.removeByIds(idList);
    }

    /**
     * 根据资源id 查询菜单id
     *
     * @param resourceIdList
     * @return
     */
    @Override
    public List<Long> findMenuIdByResourceId(List<Long> resourceIdList) {

        return baseMapper.findMenuIdByResourceId(resourceIdList);
    }

    /**
     * 自定义Resource表中 code字段保存格式
     * 如 resource:add
     * @param resource
     * @return
     */
    @Override
    public boolean save(Resource resource) {
        resource.setCode(StrHelper.getOrDef(resource.getCode(), codeGenerate.next()));

        int codeCount = super.count(Wraps.<Resource>lbQ().eq(Resource::getCode, resource.getCode()));
        if (codeCount > 0) {
            throw BizException.validFail("编码[%s]重复", resource.getCode());
        }
        super.save(resource);
        return true;
    }


}
