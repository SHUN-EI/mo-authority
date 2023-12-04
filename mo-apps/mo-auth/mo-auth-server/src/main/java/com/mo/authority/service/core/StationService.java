package com.mo.authority.service.core;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.authority.dto.core.StationPageDTO;
import com.mo.authority.entity.core.Station;

/**
 * Created by mo on 2023/12/4
 * 岗位-业务接口
 */
public interface StationService extends IService<Station> {

    IPage<Station> findStationPage(Page page, StationPageDTO stationPageDTO);
}
