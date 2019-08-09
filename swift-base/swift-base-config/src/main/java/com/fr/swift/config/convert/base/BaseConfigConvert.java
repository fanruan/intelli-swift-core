package com.fr.swift.config.convert.base;

import com.fr.swift.config.service.SwiftConfigService;

/**
 * @author yee
 * @date 2018/7/17
 */
abstract class BaseConfigConvert<T> implements SwiftConfigService.ConfigConvert<T> {
    /**
     * 配置的命名空间
     *
     * @return
     */
    protected abstract String getNameSpace();

    protected String getKey(Object... keys) {
        StringBuffer buffer = new StringBuffer(getNameSpace());
        for (Object key : keys) {
            buffer.append(".").append(key);
        }
        return buffer.toString();
    }
}
