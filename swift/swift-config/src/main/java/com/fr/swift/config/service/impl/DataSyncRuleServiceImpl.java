package com.fr.swift.config.service.impl;

import com.fr.swift.config.bean.DataSyncRule;
import com.fr.swift.config.convert.DataSyncRuleConvert;
import com.fr.swift.config.service.DataSyncRuleService;
import com.fr.swift.config.service.base.BasicRuleService;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/7/16
 */
@Service("dataSyncRuleService")
public class DataSyncRuleServiceImpl extends BasicRuleService<DataSyncRule> implements DataSyncRuleService {
    public DataSyncRuleServiceImpl() {
        super(new DataSyncRuleConvert());
    }
}
