package com.fr.swift.config.conf.service;

import com.fr.config.Configuration;
import com.fr.stable.query.QueryFactory;
import com.fr.swift.config.IMetaData;
import com.fr.swift.config.conf.MetaDataConfig;
import com.fr.swift.config.conf.MetaDataConvertUtil;
import com.fr.swift.config.conf.SegmentConfig;
import com.fr.swift.config.conf.SwiftPathConfig;
import com.fr.swift.config.conf.bean.SegmentBean;
import com.fr.swift.config.conf.bean.SwiftMetaDataBean;
import com.fr.swift.config.conf.context.SwiftConfigContext;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;
import com.fr.transaction.Configurations;
import com.fr.transaction.Worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/3/23
 */
public class SwiftConfigServiceImpl implements SwiftConfigService {

    private SwiftPathConfig swiftPathConfig = SwiftPathConfig.getInstance();
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftConfigServiceImpl.class);

    private ConcurrentHashMap<String, SwiftMetaDataBean> metaDataCache = new ConcurrentHashMap<String, SwiftMetaDataBean>();

    @Override
    public boolean addMetaData(final String sourceKey, final IMetaData metaData) {
        try {
            SwiftMetaDataBean bean = (SwiftMetaDataBean) metaData;
            bean.setId(sourceKey);
            SwiftConfigContext.getInstance().getMetaDataController().update(bean);
            metaDataCache.put(sourceKey, bean);
            return true;
        } catch (Exception e) {
            LOGGER.error("Add or update metadata error!", e);
            return false;
        }
    }

    @Override
    public boolean addMetaDatas(final Map<String, IMetaData> metaDatas) {
        Iterator<Map.Entry<String, IMetaData>> iterator = metaDatas.entrySet().iterator();
        try {
            while (iterator.hasNext()) {
                Map.Entry<String, IMetaData> entry = iterator.next();
                addMetaData(entry.getKey(), entry.getValue());
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Add metadata error!", e);
            return false;
        }
    }

    @Override
    public boolean removeMetaDatas(final String... sourceKeys) {

        try {
            for (String sourceKey : sourceKeys) {
                SwiftConfigContext.getInstance().getMetaDataController().remove(sourceKey);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Remove metadata error!", e);
            return false;
        }
    }

    @Override
    public boolean updateMetaData(final String sourceKey, final IMetaData metaData) {
        return addMetaData(sourceKey, metaData);
    }

    @Override
    public Map<String, IMetaData> getAllMetaData() {
        try {
            List<SwiftMetaDataBean> beans = SwiftConfigContext.getInstance().getMetaDataController().find(QueryFactory.create());
            Map<String, IMetaData> result = new HashMap<String, IMetaData>();
            for (SwiftMetaDataBean bean : beans) {
                result.put(bean.getId(), bean);
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("Select metadata error!", e);
            return new HashMap<String, IMetaData>();
        }
    }

    @Override
    public IMetaData getMetaDataByKey(String sourceKey) {
        IMetaData metaData = metaDataCache.get(sourceKey);
        if (null == metaData) {
            try {
                metaData = SwiftConfigContext.getInstance().getMetaDataController().findBySourceKey(sourceKey);
                metaDataCache.put(sourceKey, MetaDataConvertUtil.toSwiftMetadataPojo(metaData));
            } catch (Exception e) {
                LOGGER.error("Select metadata error!", e);
                return null;
            }
        }
        return metaData;

    }

    @Override
    public boolean containsMeta(SourceKey sourceKey) {
        try {
            return SwiftConfigContext.getInstance().getMetaDataController().findBySourceKey(sourceKey.getId()) != null;
        } catch (Exception e) {
            LOGGER.error("Select metadata error!", e);
            return false;
        }
    }

    @Override
    public boolean addSegments(final List<SegmentBean> segments) {
        try {
            for (SegmentBean bean : segments) {
                SwiftConfigContext.getInstance().getSegmentController().add(bean);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Add or update segments error!", e);
            return false;
        }
    }

    @Override
    public boolean removeSegments(final String... sourceKeys) {
        try {
            for (String sourceKey : sourceKeys) {
                SwiftConfigContext.getInstance().getSegmentController().removeBySourceKey(sourceKey);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Remove segments error!", e);
            return false;
        }
    }

    @Override
    public boolean updateSegments(final List<SegmentBean> segments) {
        return addSegments(segments);
    }

    @Override
    public Map<String, List<SegmentBean>> getAllSegments() {
        Map<String, List<SegmentBean>> result = new HashMap<String, List<SegmentBean>>();
        try {
            List<SegmentBean> beans = SwiftConfigContext.getInstance().getSegmentController().find(QueryFactory.create());
            for (SegmentBean bean : beans) {
                if (!result.containsKey(bean.getSourceKey())) {
                    result.put(bean.getSourceKey(), new ArrayList<SegmentBean>());
                }
                result.get(bean.getSourceKey()).add(bean);
            }
        } catch (Exception e) {
            LOGGER.error("Select segments error!", e);
        }
        return result;
    }

    @Override
    public List<SegmentBean> getSegmentByKey(String sourceKey) {
        try {
            return SwiftConfigContext.getInstance().getSegmentController().findBySourceKey(sourceKey);
        } catch (Exception e) {
            LOGGER.error("Select segments error!", e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean setSwiftPath(final String path) {
        return Configurations.update(new SwiftPathConfigWorker() {
            @Override
            public void run() {
                swiftPathConfig.setPath(path);
            }
        });
    }

    @Override
    public String getSwiftPath() {
        return this.swiftPathConfig.getPath();
    }

    private abstract class MetaDataConfigWorker implements Worker {

        @Override
        public Class<? extends Configuration>[] targets() {
            return new Class[]{MetaDataConfig.class};
        }
    }

    private abstract class SegmentConfigWorker implements Worker {

        @Override
        public Class<? extends Configuration>[] targets() {
            return new Class[]{SegmentConfig.class};
        }
    }

    private abstract class SwiftPathConfigWorker implements Worker {
        @Override
        public Class<? extends Configuration>[] targets() {
            return new Class[]{SwiftPathConfig.class};
        }
    }
}