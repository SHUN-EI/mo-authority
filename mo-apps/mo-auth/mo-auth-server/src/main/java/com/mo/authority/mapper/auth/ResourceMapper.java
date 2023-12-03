package com.mo.authority.mapper.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mo.authority.dto.auth.ResourceQueryDTO;
import com.mo.authority.entity.auth.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mo on 2023/12/4
 */
@Repository
public interface ResourceMapper extends BaseMapper<Resource> {

    List<Resource> findVisibleResource(ResourceQueryDTO resource);
}
