package com.fr.swift.config.conf;

import com.fr.swift.config.conf.service.SwiftConfigServiceProvider;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataImpl;

/**
 * @author yee
 * @date 2018/3/16
 */
public class MetaDataConvertUtil {
    public static SwiftMetaData getSwiftMetaDataBySourceKey(String sourceKey) throws SwiftMetaDataException {
        SwiftMetaData iMetaData = SwiftConfigServiceProvider.getInstance().getMetaDataByKey(sourceKey);
        return new SwiftMetaDataImpl(iMetaData);
    }
}