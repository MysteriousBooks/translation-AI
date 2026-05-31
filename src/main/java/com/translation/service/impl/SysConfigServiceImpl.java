package com.translation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.translation.common.utils.RedisUtil;
import com.translation.entity.SysConfig;
import com.translation.mapper.SysConfigMapper;
import com.translation.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;
    private final RedisUtil redisUtil;

    private static final String CONFIG_CACHE_PREFIX = "sys:config:";

    @Override
    public String getConfigValue(String key) {
        /* 先查缓存 */
        String cacheKey = CONFIG_CACHE_PREFIX + key;
        String cachedValue = redisUtil.get(cacheKey);
        if (cachedValue != null) {
            return cachedValue;
        }

        /* 查数据库 */
        SysConfig config = lambdaQuery().eq(SysConfig::getConfigKey, key).one();
        if (config == null) {
            return null;
        }

        /* 写入缓存，1小时过期 */
        redisUtil.set(cacheKey, config.getConfigValue(), 1, TimeUnit.HOURS);
        return config.getConfigValue();
    }

    @Override
    public void updateConfigValue(String key, String value) {
        SysConfig config = lambdaQuery().eq(SysConfig::getConfigKey, key).one();
        if (config != null) {
            config.setConfigValue(value);
            sysConfigMapper.updateById(config);
        } else {
            config = new SysConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            sysConfigMapper.insert(config);
        }

        /* 更新缓存 */
        String cacheKey = CONFIG_CACHE_PREFIX + key;
        redisUtil.set(cacheKey, value, 1, TimeUnit.HOURS);
    }

    @Override
    public List<SysConfig> getAllConfigs() {
        return lambdaQuery().list();
    }
}