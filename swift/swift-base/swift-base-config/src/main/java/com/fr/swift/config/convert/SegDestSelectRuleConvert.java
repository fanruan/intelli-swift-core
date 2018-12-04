package com.fr.swift.config.convert;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.SegmentDestSelectRule;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.convert.base.AbstractObjectConfigConvert;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.log.SwiftLoggers;

/**
 * @author yee
 * @date 2018/7/16
 */
public class SegDestSelectRuleConvert extends AbstractObjectConfigConvert<SegmentDestSelectRule> {

    private static final String NAMESPACE = "SEGMENT_DEST_SELECT_RULE";

    public SegDestSelectRuleConvert() {
        super(SegmentDestSelectRule.class);
    }

    @Override
    public SegmentDestSelectRule toBean(SwiftConfigDao<SwiftConfigBean> dao, ConfigSession session, Object... args) {
        try {
            return super.toBean(dao, session, args);
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn(String.format("find config Rule error with %s, use default rule", e.getMessage()));
            return SwiftContext.get().getBean("defaultSegmentDestSelectRule", SegmentDestSelectRule.class);
        }
    }

    @Override
    protected String transferClassName(String className) {
        return className;
    }

    @Override
    protected String getNameSpace() {
        return NAMESPACE;
    }
}
