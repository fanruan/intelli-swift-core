package com.fr.swift.utils;

import com.fr.swift.reliance.SourceReliance;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.DataSourceUtils;
import com.fr.swift.util.SourceNodeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/4/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SourceRelianceFactory {

    public static SourceReliance generateSourceReliance(List<DataSource> baseDataSources, List<DataSource> allDataSourceList) {
        List<SourceKey> baseSourceKeys = new ArrayList<SourceKey>();
        for (DataSource baseDataSource : baseDataSources) {
            baseSourceKeys.add(baseDataSource.getSourceKey());
        }
        List<DataSource> relianceSources = DataSourceUtils.calculateReliances(baseSourceKeys, allDataSourceList);
        SourceReliance sourceReliance = new SourceReliance(baseDataSources, relianceSources);
        SourceNodeUtils.calculateSourceNode(sourceReliance);
        return sourceReliance;
    }
}
