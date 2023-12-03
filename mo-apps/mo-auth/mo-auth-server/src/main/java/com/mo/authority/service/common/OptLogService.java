package com.mo.authority.service.common;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.authority.entity.common.OptLog;
import com.mo.log.entity.OptLogDTO;

/**
 * Created by mo on 2023/12/4
 * 业务接口
 * 操作日志
 */
public interface OptLogService extends IService<OptLog> {

    Boolean save(OptLogDTO optLogDTO);
}
