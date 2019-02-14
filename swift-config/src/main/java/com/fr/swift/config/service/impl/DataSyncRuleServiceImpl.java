package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.DataSyncRule;
import com.fr.swift.config.convert.DataSyncRuleConvert;
import com.fr.swift.config.service.DataSyncRuleService;

/**
 * @author yee
 * @date 2018/7/16
 */
@SwiftBean(name = "dataSyncRuleService")
public class DataSyncRuleServiceImpl extends BasicRuleService<DataSyncRule> implements DataSyncRuleService {
    public DataSyncRuleServiceImpl() {
        super(new DataSyncRuleConvert());
    }
}
