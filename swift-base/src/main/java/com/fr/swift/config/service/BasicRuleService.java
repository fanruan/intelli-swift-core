package com.fr.swift.config.service;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLoggers;

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

    @Override
    public void setCurrentRule(String className) {
        try {
            Class<? extends T> clazz = (Class<? extends T>) Class.forName(className);
            setCurrentRule(clazz);
        } catch (ClassNotFoundException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    public void setCurrentRule(Class<? extends T> clazz) {
        try {
            setCurrentRule(clazz.newInstance());
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}
