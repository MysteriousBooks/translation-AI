package com.translation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.translation.entity.SysConfig;

import java.util.List;

public interface SysConfigService extends IService<SysConfig> {

    String getConfigValue(String key);

    void updateConfigValue(String key, String value);

    List<SysConfig> getAllConfigs();
}