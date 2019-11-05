package com.fr.swift.config.command.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.SwiftConfig;
import com.fr.swift.config.command.SwiftConfigCommand;
import com.fr.swift.config.command.SwiftSegmentCommandBus;
import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.condition.impl.SwiftConfigConditionImpl;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.Order;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.container.SegmentContainer;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yee
 * @date 2019-08-09
 */
public class SwiftSegmentCommandBusImpl extends SwiftHibernateConfigCommandBus<SegmentKey> implements SwiftSegmentCommandBus {
    public SwiftSegmentCommandBusImpl() {
        super(SwiftSegmentEntity.class);
    }

    @Override
    public List<SegmentKey> save(Collection<SegmentKey> objs) throws SQLException {
        final List<SegmentKey> save = super.save(objs);
        cacheSegment(save);
        return save;
    }

    @Override
    public SegmentKey save(SegmentKey obj) throws SQLException {
        final SegmentKey save = super.save(obj);
        cacheSegment(Collections.singleton(save));
        return save;
    }

    @Override
    public List<SegmentKey> merge(Collection<SegmentKey> objs) {
        final List<SegmentKey> merge = super.merge(objs);
        cacheSegment(merge);
        return merge;
    }

    @Override
    public SegmentKey merge(SegmentKey obj) {
        final SegmentKey merge = super.merge(obj);
        cacheSegment(Collections.singleton(merge));
        return merge;
    }

    @Override
    public int delete(final SwiftConfigCondition condition) {
        try {
            return transaction(new SwiftConfigCommand<Integer>() {
                @Override
                public Integer apply(ConfigSession p) {
                    final ConfigQuery<? extends SegmentKey> entityQuery = p.createEntityQuery(tClass);
                    entityQuery.where(condition.getWheres().toArray(new ConfigWhere[0]));
                    entityQuery.orderBy(condition.getSort().toArray(new Order[0]));
                    final List<? extends SegmentKey> swiftSegmentEntities = entityQuery.executeQuery();
                    for (SegmentKey swiftSegmentEntity : swiftSegmentEntities) {
                        p.delete(swiftSegmentEntity);
                        for (SegmentContainer value : SegmentContainer.values()) {
                            value.remove(swiftSegmentEntity);
                        }
                    }
                    return null;
                }
            });
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public int deleteCascade(SwiftConfigCondition condition) {
        final List<? extends SegmentKey> swiftSegmentEntities = SwiftContext.get().getBean(SwiftConfig.class).query(tClass).get(condition);
        for (SegmentKey swiftSegmentEntity : swiftSegmentEntities) {
            for (SegmentContainer value : SegmentContainer.values()) {
                value.remove(swiftSegmentEntity);
            }
        }
        return super.deleteCascade(condition);
    }

    @Override
    public boolean removeSegments(List<SegmentKey> segmentKeys) {
        Set<String> ids = new HashSet<>();
        for (SegmentKey segmentKey : segmentKeys) {
            ids.add(segmentKey.getId());
        }
        final SwiftConfigCondition condition = SwiftConfigConditionImpl.newInstance()
                .addWhere(ConfigWhereImpl.in("id", ids));
        return deleteCascade(condition) >= 0;
    }

    private void cacheSegment(Collection<? extends SegmentKey> segments) {
        for (SegmentKey segment : segments) {
            for (SegmentContainer value : SegmentContainer.values()) {
                value.register(segment);
            }
        }
    }
}
