package com.fr.swift.config.convert.hibernate;

import com.fr.swift.config.json.ConfigBeanMapper;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.alloter.AllotRule;

/**
 * @author anchore
 * @date 2018/7/16
 */
public abstract class BaseAllotRuleConverter implements ConfigAttributeConverter<AllotRule, String> {
    private ConfigBeanMapper mapper;

    public BaseAllotRuleConverter(ConfigBeanMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String convertToDatabaseColumn(AllotRule allotRule) {
        try {
            return mapper.writeValueAsString(allotRule);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }

    @Override
    public AllotRule convertToEntityAttribute(String s) {
        try {
            return mapper.readValue(s, AllotRule.class);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }
}