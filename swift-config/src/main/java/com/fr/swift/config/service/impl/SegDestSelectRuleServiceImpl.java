package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.SegmentDestSelectRule;
import com.fr.swift.config.convert.SegDestSelectRuleConvert;
import com.fr.swift.config.service.SegmentDestSelectRuleService;

/**
 * @author yee
 * @date 2018/7/16
 */
@SwiftBean(name = "segmentDestSelectRuleService")
public class SegDestSelectRuleServiceImpl extends BasicRuleService<SegmentDestSelectRule> implements SegmentDestSelectRuleService {

    public SegDestSelectRuleServiceImpl() {
        super(new SegDestSelectRuleConvert());
    }
}
