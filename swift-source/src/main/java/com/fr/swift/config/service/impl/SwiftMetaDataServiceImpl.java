package com.fr.swift.config.service.impl;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.command.impl.SwiftHibernateConfigCommandBus;
import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.condition.impl.SwiftConfigConditionImpl;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.query.impl.SwiftHibernateConfigQueryBus;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/6/6
 */
@SwiftBean
public class SwiftMetaDataServiceImpl implements SwiftMetaDataService {
    private SwiftHibernateConfigCommandBus<SwiftMetaData> commandBus = new SwiftHibernateConfigCommandBus<SwiftMetaData>(SwiftMetaDataBean.class);
    private SwiftHibernateConfigQueryBus<SwiftMetaDataBean> queryBus = new SwiftHibernateConfigQueryBus<>(SwiftMetaDataBean.class);

    private final ConcurrentHashMap<String, SwiftMetaData> metaDataCache = new ConcurrentHashMap<String, SwiftMetaData>();

    @Override
    public boolean addMetaData(final String sourceKey, final SwiftMetaData metaData) {
        final SwiftMetaDataBean bean = (SwiftMetaDataBean) metaData;
        bean.setId(sourceKey);
        commandBus.merge(bean);
        return true;
    }

    @Override
    public boolean removeMetaDatas(final SourceKey... sourceKeys) {
        Set<String> ids = new HashSet<>(sourceKeys.length);
        for (SourceKey sourceKey : sourceKeys) {
            ids.add(sourceKey.getId());
        }
        if (ids.isEmpty()) {
            return false;
        }
        final boolean result = commandBus.deleteCascade(SwiftConfigConditionImpl.newInstance().addWhere(ConfigWhereImpl.in("id", ids))) >= 0;
        if (result) {
            cleanCache(ids.toArray(new String[0]));
        }
        return result;
    }

    @Override
    public boolean updateMetaData(final String sourceKey, final SwiftMetaData metaData) {
        return addMetaData(sourceKey, metaData);
    }

    @Override
    public Map<String, SwiftMetaData> getAllMetaData() {
        return queryBus.get(SwiftConfigConditionImpl.newInstance(), toMetaDataMap());
    }

    @Override
    public Map<String, SwiftMetaData> getFuzzyMetaData(final String fuzzyName) {
        final SwiftConfigCondition condition = SwiftConfigConditionImpl.newInstance().addWhere(ConfigWhereImpl.like("tableName", fuzzyName, ConfigWhere.MatchMode.ANY));
        return queryBus.get(condition, toMetaDataMap());
    }

    private Function<Collection<SwiftMetaDataBean>, Map<String, SwiftMetaData>> toMetaDataMap() {
        return new Function<Collection<SwiftMetaDataBean>, Map<String, SwiftMetaData>>() {
            @Override
            public Map<String, SwiftMetaData> apply(Collection<SwiftMetaDataBean> p) {
                final Map<String, SwiftMetaData> result = new HashMap<String, SwiftMetaData>(p.size());
                for (SwiftMetaDataBean swiftMetaDataBean : p) {
                    result.put(swiftMetaDataBean.getId(), swiftMetaDataBean);
                }
                metaDataCache.putAll(result);
                return result;
            }
        };
    }

    @Override
    public SwiftMetaData getMetaDataByKey(final String sourceKey) {
        SwiftMetaData metaData = metaDataCache.get(sourceKey);
        if (null == metaData) {
            return queryBus.select(sourceKey, new Function<SwiftMetaDataBean, SwiftMetaData>() {
                @Override
                public SwiftMetaData apply(SwiftMetaDataBean p) {
                    if (null != p) {
                        metaDataCache.put(sourceKey, p);
                    }
                    return p;
                }
            });
        }
        return metaData;
    }

    @Override
    public boolean containsMeta(final SourceKey sourceKey) {
        return null != getMetaDataByKey(sourceKey.getId());
    }

    @Override
    public void cleanCache(String[] sourceKeys) {
        synchronized (metaDataCache) {
            if (null != sourceKeys) {
                for (String sourceKey : sourceKeys) {
                    metaDataCache.remove(sourceKey);
                }
            }
        }
    }

    @Override
    public List<SwiftMetaData> find(final ConfigWhere... criterion) {
        final SwiftConfigCondition condition = SwiftConfigConditionImpl.newInstance();
        for (ConfigWhere configWhere : criterion) {
            condition.addWhere(configWhere);
        }
        return new ArrayList<SwiftMetaData>(queryBus.get(condition));
    }

    @Override
    public boolean saveOrUpdate(SwiftMetaData obj) {
        return addMetaData(obj.getId(), obj);
    }
}
