package com.xxx.admin.application;

import com.xxx.admin.domain.mapper.SysLogMapper;
import com.xxx.admin.domain.modle.SysLog;
import com.xxx.admin.domain.repository.SysLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xiexx on 2016/9/17.
 */
@Service
public class SysLogService {

//    @Autowired
//    protected SysLogRepository sysLogRepository;

    @Autowired
    protected SysLogMapper sysLogRepository;

    public void create(SysLog sysLog){
        sysLogRepository.add(sysLog);
    }

    @CacheEvict(value = "syslog", allEntries = true)
    public void clear(){
        sysLogRepository.clear();
    }

    @Cacheable(value = "syslog")
    public List<SysLog> list(){
        return sysLogRepository.list();
    }
}
