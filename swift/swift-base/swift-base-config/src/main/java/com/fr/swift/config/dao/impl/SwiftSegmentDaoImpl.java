package com.fr.swift.config.dao.impl;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.FindList;
import com.fr.swift.config.oper.impl.ConfigOrder;
import com.fr.swift.config.oper.impl.RestrictionFactoryImpl;
import com.fr.swift.cube.io.Types;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.util.Strings;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author yee
 * @date 2018/5/24
 */
public class SwiftSegmentDaoImpl extends BasicDao<SegmentKeyBean> implements SwiftSegmentDao {

    public SwiftSegmentDaoImpl() {
        super(SegmentKeyBean.TYPE, RestrictionFactoryImpl.INSTANCE);
    }

    @Override
    public boolean addOrUpdateSwiftSegment(ConfigSession session, SegmentKey bean) throws SQLException {
        return saveOrUpdate(session, new SegmentKeyBean(bean));
    }

    @Override
    public List<SegmentKey> findBeanByStoreType(ConfigSession session, String sourceKey, Types.StoreType type) throws SQLException {
        if (Strings.isEmpty(sourceKey) || null == type) {
            throw new SQLException();
        }
        return new ArrayList<SegmentKey>(find(session, factory.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, sourceKey),
                factory.eq(SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE, type)).list());
    }

    @Override
    public List<SegmentKey> selectSelective(ConfigSession session, SegmentKey segmentKey) {
        List criterionList = new ArrayList();
        try {
            if (null != segmentKey.getTable()) {
                criterionList.add(factory.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, segmentKey.getTable().getId()));
            }
            if (null != segmentKey.getUri()) {
                criterionList.add(factory.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_URI, segmentKey.getUri()));
            }
            if (null != segmentKey.getStoreType()) {
                criterionList.add(factory.eq(SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE, segmentKey.getStoreType()));
            }
            if (null != segmentKey.getOrder()) {
                criterionList.add(factory.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_ORDER, segmentKey.getOrder()));
            }
            return new ArrayList<SegmentKey>(find(session, criterionList.toArray()).list());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean deleteBySourceKey(final ConfigSession session, final String sourceKey) throws SQLException {
        try {
            find(session, factory.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, sourceKey)).justForEach(new FindList.Each() {
                @Override
                public void each(int idx, Object item) {
                    session.delete(item);
                }
            });
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public boolean deleteByStoreType(final ConfigSession session, final Types.StoreType storeType) throws SQLException {
        try {
            find(session, factory.eq(SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE, storeType)).justForEach(new FindList.Each() {
                @Override
                public void each(int idx, Object item) {
                    session.delete(item);
                }
            });
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public FindList<SegmentKeyBean> findAll(ConfigSession session) {
        return find(session);
    }

    @Override
    public List<SegmentKey> findBySourceKey(ConfigSession session, String sourceKey) {
        List<SegmentKey> list = new ArrayList<SegmentKey>(find(session, new Object[]{ConfigOrder.asc(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_ORDER)},
                factory.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, sourceKey)).list());
        return Collections.unmodifiableList(list);
    }
}
