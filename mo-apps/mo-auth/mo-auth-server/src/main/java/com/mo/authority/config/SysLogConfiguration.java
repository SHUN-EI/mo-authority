package com.mo.authority.config;

import com.mo.authority.service.common.OptLogService;
import com.mo.log.entity.OptLogDTO;
import com.mo.log.event.SysLogListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.function.Consumer;

/**
 * Created by mo on 2023/12/4
 * 系统操作日志配置类
 */
@Configuration
@EnableAsync
public class SysLogConfiguration {

    /**
     * 创建日志记录监听器对象
     *
     * @param optLogService
     * @return
     */
    @Bean
    public SysLogListener sysLogListener(OptLogService optLogService) {
        Consumer<OptLogDTO> consumer = optLog -> optLogService.save(optLog);
        return new SysLogListener(consumer);
    }
}
