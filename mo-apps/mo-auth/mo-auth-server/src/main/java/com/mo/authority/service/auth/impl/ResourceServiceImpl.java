package com.mo.authority.service.auth.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.authority.dto.auth.ResourceQueryDTO;
import com.mo.authority.entity.auth.Resource;
import com.mo.authority.mapper.auth.ResourceMapper;
import com.mo.authority.service.auth.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mo on 2023/12/4
 */
@Slf4j
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {


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
}
