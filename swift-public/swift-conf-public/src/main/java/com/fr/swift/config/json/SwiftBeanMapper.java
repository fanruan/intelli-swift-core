package com.fr.swift.config.json;

import com.fr.swift.base.json.mapper.BeanMapperWrapper;

/**
 * @author yee
 * @date 2018-12-04
 */
public class SwiftBeanMapper extends BeanMapperWrapper {
    protected SwiftBeanMapper() {
        super(DefaultBeanMapper.INSTANCE);
    }
}
