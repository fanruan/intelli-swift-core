package com.fr.swift.config.convert.hibernate;

import com.fr.swift.base.json.mapper.BeanMapper;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.alloter.AllotRule;

/**
 * @author anchore
 * @date 2018/7/16
 */
public abstract class BaseAllotRuleConverter implements ConfigAttributeConverter<AllotRule, String> {
    private BeanMapper mapper;

    public BaseAllotRuleConverter(BeanMapper mapper) {
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