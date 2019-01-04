package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.IRuleService;
import com.fr.swift.config.service.SwiftConfigService;

/**
 * @author yee
 * @date 2018/7/16
 */
public class BasicRuleService<T> implements IRuleService<T> {

    private SwiftConfigService.ConfigConvert<T> convert;
    private SwiftConfigService configService;

    public BasicRuleService(SwiftConfigService.ConfigConvert<T> convert) {
        this.convert = convert;
        configService = SwiftContext.get().getBean(SwiftConfigService.class);
    }

    @Override
    public T getCurrentRule() {
        return configService.getConfigBean(convert);
    }

    @Override
    public void setCurrentRule(T rule) {
        T current = getCurrentRule();
        configService.deleteConfigBean(convert, current);
        configService.updateConfigBean(convert, rule);
    }

}
