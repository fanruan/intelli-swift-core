package com.fr.swift.util;

import com.fr.swift.increment.Increment;
import com.fr.swift.reliance.SourceNode;
import com.fr.swift.reliance.SourceReliance;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.EtlDataSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/4/11
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SourceNodeUtils {

    /**
     * 基础表:直接加到头节点
     * etl表:取在reliance依赖中的父表，加为头节点，并实时清除map中不是头节点的节点。
     *
     * @param reliance
     */
    public static void calculateSourceNode(SourceReliance reliance, Map<String, List<Increment>> incrementMap) {
        Iterator<DataSource> dataSourceIterator = reliance.getReliances().iterator();
        while (dataSourceIterator.hasNext()) {
            DataSource dataSource = dataSourceIterator.next();
            if (dataSource instanceof EtlDataSource) {
                calculateBaeeNode((EtlDataSource) dataSource, reliance, incrementMap);
            } else {
                if (!reliance.containHeadNode(dataSource.getSourceKey())) {
                    SourceNode sourceNode = new SourceNode(dataSource, getIncrement(incrementMap, dataSource));
                    reliance.addHeadNode(sourceNode);
                    reliance.addNode(sourceNode);
                }
            }
        }

        for (SourceNode headSourceNode : new ArrayList<SourceNode>(reliance.getHeadNodes().values())) {
            if (headSourceNode.hasPrev()) {
                reliance.removeHeadNode(headSourceNode);
            }
        }
    }

    private static void calculateBaeeNode(EtlDataSource etlDataSource, SourceReliance reliance, Map<String, List<Increment>> incrementMap) {
        SourceNode sourceNode = reliance.getNode(etlDataSource.getSourceKey());
        if (sourceNode == null) {
            sourceNode = new SourceNode(etlDataSource, getIncrement(incrementMap, etlDataSource));
            reliance.addNode(sourceNode);
        }
        List<DataSource> baseDataSources = etlDataSource.getBasedSources();
        for (DataSource baseDataSource : baseDataSources) {
            if (reliance.containReliance(baseDataSource.getSourceKey())) {

                SourceNode baseSourceNode = reliance.getNode(baseDataSource.getSourceKey());
                if (baseSourceNode == null) {
                    baseSourceNode = new SourceNode(baseDataSource, getIncrement(incrementMap, baseDataSource));
                    reliance.addNode(baseSourceNode);
                }
                baseSourceNode.addNext(sourceNode);
                sourceNode.setHasPrev();
                reliance.addHeadNode(baseSourceNode);
            }
        }
        if (sourceNode.hasPrev()) {
            reliance.removeHeadNode(sourceNode);
        }
    }

    //暂时只有1个increment，默认取0
    private static Increment getIncrement(Map<String, List<Increment>> incrementMap, DataSource dataSource) {
        List<Increment> incrementList = incrementMap.get(dataSource.getSourceKey().getId());
        if (incrementList != null && !incrementList.isEmpty()) {
            return incrementList.get(0);
        }
        return null;
    }
}
