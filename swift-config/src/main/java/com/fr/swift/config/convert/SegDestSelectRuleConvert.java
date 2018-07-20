package com.fr.swift.config.convert;

import com.fr.swift.config.bean.SegmentDestSelectRule;
import com.fr.swift.config.convert.base.AbstractObjectConfigConvert;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.third.org.hibernate.Session;

/**
 * @author yee
 * @date 2018/7/16
 */
public class SegDestSelectRuleConvert extends AbstractObjectConfigConvert<SegmentDestSelectRule> {

    private static final String NAMESPACE = "SEGMENT_DEST_SELECT_RULE";

    @Override
    public SegmentDestSelectRule toBean(SwiftConfigDao<SwiftConfigEntity> dao, Session session, Object... args) {
        try {
            return super.toBean(dao, session, args);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(String.format("find config Rule error with %s, use default rule", e.getMessage()));
            return SwiftContext.get().getBean("defaultSegmentDestSelectRule", SegmentDestSelectRule.class);
        }
    }

    @Override
    protected String getNameSpace() {
        return NAMESPACE;
    }
}
