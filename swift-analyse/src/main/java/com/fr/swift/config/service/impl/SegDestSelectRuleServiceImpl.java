package com.fr.swift.config.service.impl;

import com.fr.swift.config.bean.SegmentDestSelectRule;
import com.fr.swift.config.convert.SegDestSelectRuleConvert;
import com.fr.swift.config.service.BasicRuleService;
import com.fr.swift.config.service.SegmentDestSelectRuleService;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/7/16
 */
@Service("segmentDestSelectRuleService")
public class SegDestSelectRuleServiceImpl extends BasicRuleService<SegmentDestSelectRule> implements SegmentDestSelectRuleService {

    public SegDestSelectRuleServiceImpl() {
        super(new SegDestSelectRuleConvert());
    }
}
