package com.fr.swift.config.dao.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftSegmentBucketDao;
import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;

import java.sql.SQLException;

/**
 * @author lucifer
 * @date 2019-06-25
 * @description
 * @since advanced swift 1.0
 */
@SwiftBean
public class SwiftSegmentBucketDaoImpl extends BasicDao<SwiftSegmentBucketElement> implements SwiftSegmentBucketDao {

    public SwiftSegmentBucketDaoImpl() {
        super(SwiftSegmentBucketElement.class);
    }

    @Override
    public boolean deleteBySourceKey(ConfigSession session, String sourceKey) throws SQLException {
        try {
            for (SwiftSegmentBucketElement element : find(session, ConfigWhereImpl.eq("sourceKey", sourceKey))) {
                session.delete(element);
            }
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public boolean deleteBySegmentKey(ConfigSession session, String segmentKey) throws SQLException {
        try {
            for (SwiftSegmentBucketElement element : find(session, ConfigWhereImpl.eq("unionKey.realSegmentKey", segmentKey))) {
                session.delete(element);
            }
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
