package com.mo.authority.service.auth.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.authority.entity.auth.Menu;
import com.mo.authority.mapper.auth.MenuMapper;
import com.mo.authority.service.auth.MenuService;
import com.mo.authority.service.auth.ResourceService;
import com.mo.utils.NumberHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mo.utils.StrPool.DEF_PARENT_ID;

/**
 * Created by mo on 2023/12/4
 * 菜单-业务实现类
 */
@Slf4j
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private ResourceService resourceService;

    /**
     * 根据ID删除
     *
     * @param ids
     * @return
     */
    @Override
    public Boolean removeByIds(List<Long> ids) {

        if (ids.isEmpty()) {
            return true;
        }

        boolean result = super.removeByIds(ids);

        //根据菜单id删除资源
        if (result) {
            resourceService.removeByMenuId(ids);
        }

        return result;
    }

    /**
     * 查询用户可用菜单
     *
     * @param group
     * @param userId
     * @return
     */
    @Override
    public List<Menu> findVisibleMenu(String group, Long userId) {

        List<Menu> visibleMenu = baseMapper.findVisibleMenu(group, userId);
        return visibleMenu;
    }

    /**
     * 修改Menu is_enable is_public parent_id等字段
     *
     * @param menu
     * @return
     */
    @Override
    public boolean save(Menu menu) {
        menu.setIsEnable(NumberHelper.getOrDef(menu.getIsEnable(), true));
        menu.setIsPublic(NumberHelper.getOrDef(menu.getIsPublic(), false));
        menu.setParentId(NumberHelper.getOrDef(menu.getParentId(), DEF_PARENT_ID));

        super.save(menu);
        return true;
    }
}
