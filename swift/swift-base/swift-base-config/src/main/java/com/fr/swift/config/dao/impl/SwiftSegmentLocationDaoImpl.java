package com.fr.swift.config.dao.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.SegLocationBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftSegmentLocationDao;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.RestrictionFactory;
import com.fr.swift.config.oper.impl.RestrictionFactoryImpl;
import com.fr.swift.converter.FindList;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/7/3
 */
@SwiftBean
public class SwiftSegmentLocationDaoImpl extends BasicDao<SegLocationBean> implements SwiftSegmentLocationDao {
    public SwiftSegmentLocationDaoImpl() {
        super(SegLocationBean.TYPE, RestrictionFactoryImpl.INSTANCE);
    }

    /**
     * for test
     *
     * @param factory
     */
    public SwiftSegmentLocationDaoImpl(RestrictionFactory factory) {
        super(SegLocationBean.TYPE, factory);
    }

    @Override
    public boolean deleteBySourceKey(final ConfigSession session, String sourceKey) throws SQLException {
        try {
            find(session, factory.eq("sourceKey", sourceKey)).justForEach(new FindList.ConvertEach() {
                @Override
                public Object forEach(int idx, Object item) {
                    session.delete(item);
                    return null;
                }
            });
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public FindList<SegLocationBean> findByClusterId(ConfigSession session, String clusterId) {
        return find(session, factory.eq("id.clusterId", clusterId));
    }

    @Override
    public FindList<SegLocationBean> findBySegmentId(ConfigSession session, String segmentId) {
        return find(session, factory.eq("id.segmentId", segmentId));
    }

    @Override
    public FindList<SegLocationBean> findAll(ConfigSession session) {
        return find(session);
    }

}
