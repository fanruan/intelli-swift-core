package com.fr.swift.config.dao.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftTableAllotRuleDao;
import com.fr.swift.config.entity.SwiftTableAllotRule;

/**
 * @author lucifer
 * @date 2019-06-25
 * @description
 * @since advanced swift 1.0
 */
@SwiftBean
public class SwiftTableAllotRuleDaoImpl extends BasicDao<SwiftTableAllotRule> implements SwiftTableAllotRuleDao {

    public SwiftTableAllotRuleDaoImpl() {
        super(SwiftTableAllotRule.class);
    }
}
