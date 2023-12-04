package com.mo.authority.service.core.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.authority.dto.core.StationPageDTO;
import com.mo.authority.entity.core.Station;
import com.mo.authority.mapper.core.StationMapper;
import com.mo.authority.service.core.StationService;
import com.mo.databases.mybatis.conditions.Wraps;
import com.mo.databases.mybatis.conditions.query.LbqWrapper;
import com.mo.dozer.DozerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mo on 2023/12/4
 */
@Slf4j
@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements StationService {


    @Autowired
    private DozerUtils dozerUtils;

    /**
     * 分页查询岗位信息（含角色）
     *
     * @param page
     * @param stationPageDTO
     * @return
     */
    @Override
    public IPage<Station> findStationPage(Page page, StationPageDTO stationPageDTO) {

        Station station = dozerUtils.map(stationPageDTO, Station.class);

        //Wraps.lbQ(station); 这种写法值 不能和  ${ew.customSqlSegment} 一起使用
        LbqWrapper<Station> wrapper = Wraps.lbQ();

        // ${ew.customSqlSegment} 语法一定要手动eq like 等
        wrapper.like(Station::getName, station.getName())
                .like(Station::getDescribe, station.getDescribe())
                .eq(Station::getOrgId, station.getOrgId())
                .eq(Station::getStatus, station.getStatus())
                .geHeader(Station::getCreateTime, stationPageDTO.getStartCreateTime())
                .leFooter(Station::getCreateTime, stationPageDTO.getEndCreateTime())
        ;

        wrapper.orderByDesc(Station::getId);
        return baseMapper.findStationPage(page, wrapper);
    }
}
