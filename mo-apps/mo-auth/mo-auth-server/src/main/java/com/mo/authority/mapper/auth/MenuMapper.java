package com.mo.authority.mapper.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mo.authority.entity.auth.Menu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mo on 2023/12/4
 * 菜单 mapper接口
 */
@Repository
public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> findVisibleMenu(@Param("group") String group, @Param("userId") Long userId);
}
