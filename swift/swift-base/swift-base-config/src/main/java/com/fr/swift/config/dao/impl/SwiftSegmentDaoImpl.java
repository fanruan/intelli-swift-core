package com.fr.swift.config.dao.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.converter.FindList;
import com.fr.swift.cube.io.Types;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.util.Strings;

import java.sql.SQLException;
import java.util.List;


/**
 * @author yee
 * @date 2018/5/24
 */
@SwiftBean
public class SwiftSegmentDaoImpl extends BasicDao<SegmentKey> implements SwiftSegmentDao {

    public SwiftSegmentDaoImpl() {
        super(SegmentKeyBean.TYPE);
    }

    @Override
    public boolean addOrUpdateSwiftSegment(ConfigSession session, SegmentKey bean) throws SQLException {
        return saveOrUpdate(session, bean);
    }

    @Override
    public List<SegmentKey> findBeanByStoreType(ConfigSession session, String sourceKey, Types.StoreType type) throws SQLException {
        if (Strings.isEmpty(sourceKey) || null == type) {
            throw new SQLException();
        }
        return find(session, ConfigWhereImpl.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, sourceKey),
                ConfigWhereImpl.eq(SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE, type)).list();
    }

    @Override
    public boolean deleteBySourceKey(final ConfigSession session, final String sourceKey) throws SQLException {
        try {
            find(session, ConfigWhereImpl.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, sourceKey)).justForEach(new FindList.ConvertEach() {
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
    public FindList<SegmentKey> findAll(ConfigSession session) {
        return find(session);
    }

}
