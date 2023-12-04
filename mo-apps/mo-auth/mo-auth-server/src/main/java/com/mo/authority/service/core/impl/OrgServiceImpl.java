package com.mo.authority.service.core.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.authority.entity.core.Org;
import com.mo.authority.mapper.core.OrgMapper;
import com.mo.authority.service.core.OrgService;
import com.mo.databases.mybatis.conditions.Wraps;
import com.mo.databases.mybatis.conditions.query.LbqWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mo on 2023/12/4
 */
@Slf4j
@Service
public class OrgServiceImpl extends ServiceImpl<OrgMapper, Org> implements OrgService {

    /**
     * 查询指定id集合下的所有子集
     *
     * @param ids
     * @return
     */
    @Override
    public List<Org> findChildren(List<Long> ids) {

        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }

        // MySQL 全文索引
        String applySql = String.format(" MATCH(tree_path) AGAINST('%s' IN BOOLEAN MODE) ", StringUtils.join(ids, " "));
        LbqWrapper<Org> queryWrapper = Wraps.<Org>lbQ().in(Org::getId, ids).or(query -> query.apply(applySql));
        List<Org> orgList = super.list(queryWrapper);

        return orgList;
    }


    /**
     * 批量删除以及删除其子节点
     *
     * @param ids
     * @return
     */
    @Override
    public Boolean remove(List<Long> ids) {

        List<Org> orgList = findChildren(ids);

        List<Long> idList = orgList.stream().mapToLong(Org::getId).boxed()
                .collect(Collectors.toList());
        return !idList.isEmpty() ? super.removeByIds(idList) : true;
    }
}
