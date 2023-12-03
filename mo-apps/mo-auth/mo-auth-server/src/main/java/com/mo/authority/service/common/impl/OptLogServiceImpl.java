package com.mo.authority.service.common.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.authority.entity.common.OptLog;
import com.mo.authority.mapper.common.OptLogMapper;
import com.mo.authority.service.common.OptLogService;
import com.mo.dozer.DozerUtils;
import com.mo.log.entity.OptLogDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mo on 2023/12/4
 * 业务实现类
 * 操作日志
 */
@Service
public class OptLogServiceImpl extends ServiceImpl<OptLogMapper, OptLog> implements OptLogService {

    @Autowired
    private DozerUtils dozerUtils;

    /**
     * 保存日志
     * @param optLogDTO
     * @return
     */
    @Override
    public Boolean save(OptLogDTO optLogDTO) {
        return save(dozerUtils.map(optLogDTO, OptLog.class));
    }
}
