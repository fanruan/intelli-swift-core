package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftDao;
import com.fr.swift.config.dao.SwiftDaoImpl;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.config.service.SwiftTableAllotRuleService;
import com.fr.swift.source.SourceKey;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * @author lucifer
 * @date 2019-06-25
 * @description
 * @since advanced swift 1.0
 */
@SwiftBean(name = "swiftTableAllotRuleService")
public class SwiftTableAllotRuleServiceImpl implements SwiftTableAllotRuleService {

    private SwiftDao<SwiftTableAllotRule> dao = new SwiftDaoImpl<>(SwiftTableAllotRule.class);

    @Override
    public SwiftTableAllotRule getByTale(SourceKey sourceKey) {
        final List<SwiftTableAllotRule> allotRules = (List<SwiftTableAllotRule>) dao.select(criteria -> criteria.add(Restrictions.eq("sourceKey", sourceKey.getId())));
        if (allotRules.isEmpty()) {
            return null;
        }
        return allotRules.get(0);
    }

    @Override
    public void save(SwiftTableAllotRule swiftTableAllotRule) {
        dao.insert(swiftTableAllotRule);
    }

}