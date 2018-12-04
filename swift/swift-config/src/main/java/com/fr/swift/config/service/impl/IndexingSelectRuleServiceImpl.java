package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.IndexingSelectRule;
import com.fr.swift.config.convert.IndexingSelectRuleConvert;
import com.fr.swift.config.service.IndexingSelectRuleService;

/**
 * @author yee
 * @date 2018/7/16
 */
@SwiftBean(name = "indexingSelectRuleService")
public class IndexingSelectRuleServiceImpl extends BasicRuleService<IndexingSelectRule> implements IndexingSelectRuleService {
    public IndexingSelectRuleServiceImpl() {
        super(new IndexingSelectRuleConvert());
    }
}
