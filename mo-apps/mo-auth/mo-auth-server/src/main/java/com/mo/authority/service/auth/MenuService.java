package com.mo.authority.service.auth;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.authority.entity.auth.Menu;

import java.util.List;

/**
 * Created by mo on 2023/12/4
 * 菜单-业务接口
 */
public interface MenuService extends IService<Menu> {

    Boolean removeByIds(List<Long> ids);

    List<Menu> findVisibleMenu(String group, Long userId);
}
