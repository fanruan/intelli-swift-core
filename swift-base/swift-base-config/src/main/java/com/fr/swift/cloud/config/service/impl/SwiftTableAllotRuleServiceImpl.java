package com.fr.swift.cloud.config.service.impl;

import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.config.dao.SwiftDao;
import com.fr.swift.cloud.config.dao.SwiftDaoImpl;
import com.fr.swift.cloud.config.entity.SwiftTableAllotRule;
import com.fr.swift.cloud.config.service.SwiftTableAllotRuleService;
import com.fr.swift.cloud.source.SourceKey;

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
        final List<SwiftTableAllotRule> allotRules = (List<SwiftTableAllotRule>) dao.selectQuery((query, builder, from) ->
                query.select(from).where(builder.equal(from.get("sourceKey"), sourceKey.getId())));
        if (allotRules.isEmpty()) {
            return null;
        }
        return allotRules.get(0);
    }

    @Override
    public void save(SwiftTableAllotRule swiftTableAllotRule) {
        dao.insert(swiftTableAllotRule);
    }

    @Override
    public void update(SwiftTableAllotRule SwiftTableAllotRule) {
        dao.update(SwiftTableAllotRule);
    }

}