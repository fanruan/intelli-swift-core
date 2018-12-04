package com.fr.swift.config.convert;

import com.fr.swift.config.convert.hibernate.BaseAllotRuleConverter;
import com.fr.swift.config.json.FRBeanMapper;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.third.javax.persistence.AttributeConverter;

/**
 * @author yee
 * @date 2018-11-27
 */
public class FRAllotRuleConverter extends BaseAllotRuleConverter implements AttributeConverter<AllotRule, String> {
    public FRAllotRuleConverter() {
        super(FRBeanMapper.INSTANCE);
    }
}
