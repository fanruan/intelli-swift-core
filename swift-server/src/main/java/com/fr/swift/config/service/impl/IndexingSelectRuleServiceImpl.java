package com.fr.swift.config.service.impl;

import com.fr.swift.config.bean.IndexingSelectRule;
import com.fr.swift.config.convert.IndexingSelectRuleConvert;
import com.fr.swift.config.service.BasicRuleService;
import com.fr.swift.config.service.IndexingSelectRuleService;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/7/16
 */
@Service("indexingSelectRuleService")
public class IndexingSelectRuleServiceImpl extends BasicRuleService<IndexingSelectRule> implements IndexingSelectRuleService {
    public IndexingSelectRuleServiceImpl() {
        super(new IndexingSelectRuleConvert());
    }
}
