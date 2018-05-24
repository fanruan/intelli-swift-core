package com.fr.swift.config.meta;

import com.fr.config.ConfigContext;
import com.fr.config.DefaultConfiguration;
import com.fr.config.holder.factory.Holders;
import com.fr.config.holder.impl.ObjectMapConf;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 * todo 事务处理
 */
public class MetaDataConfig extends DefaultConfiguration {
    private final static String NAMESPACE = "metadata_config";

    private static MetaDataConfig config = null;

    private ObjectMapConf<Map<String, SwiftMetaData>> metaDataHolder =
            Holders.objMap(new HashMap<String, SwiftMetaData>(), String.class, SwiftMetaData.class);

    public static MetaDataConfig getInstance() {
        if (config == null) {
            config = ConfigContext.getConfigInstance(MetaDataConfig.class);
        }
        return config;
    }

    public Map<String, SwiftMetaData> getAllMetaData() throws SwiftMetaDataException {
        Map<String, SwiftMetaData> map = metaDataHolder.get(),
                realMap = new HashMap<String, SwiftMetaData>(map.size());
        for (Entry<String, SwiftMetaData> entry : map.entrySet()) {
            realMap.put(entry.getKey(), new SwiftMetaDataImpl(entry.getValue()));
        }
        return realMap;
    }

    public SwiftMetaData getMetaDataByKey(String key) throws SwiftMetaDataException {
        return new SwiftMetaDataImpl((SwiftMetaData) metaDataHolder.get(key));
    }

    /**
     * sourceKey -> metadata
     *
     * @param sourceKey
     * @param metaData
     */
    public void addMetaData(String sourceKey, SwiftMetaData metaData) throws SwiftMetaDataException {
        metaDataHolder.put(sourceKey, new SwiftMetaDataUnique(metaData));
    }

    public void removeMetaData(String key) {
        metaDataHolder.remove(key);
    }

    public void modifyMetaData(String sourceKey, SwiftMetaData metaData) throws SwiftMetaDataException {
        // 直接调添加方法好了，取出metaData再设置好像不能全覆盖
        addMetaData(sourceKey, metaData);
    }

    public boolean contains(SourceKey metaKey) {
        return metaDataHolder.containsKey(metaKey.getId());
    }

    @Override
    public String getNameSpace() {
        return NAMESPACE;
    }
}