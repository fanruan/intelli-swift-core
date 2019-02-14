package com.fr.swift.util;

import com.fr.swift.reliance.SourceNode;
import com.fr.swift.reliance.SourceReliance;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.EtlDataSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    public static void calculateSourceNode(SourceReliance reliance) {
        Iterator<DataSource> dataSourceIterator = reliance.getReliances().iterator();
        while (dataSourceIterator.hasNext()) {
            DataSource dataSource = dataSourceIterator.next();
            if (dataSource instanceof EtlDataSource) {
                calculateBaeeNode((EtlDataSource) dataSource, reliance);
            } else {
                if (!reliance.containHeadNode(dataSource.getSourceKey())) {
                    SourceNode sourceNode = new SourceNode(dataSource);
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

    private static void calculateBaeeNode(EtlDataSource etlDataSource, SourceReliance reliance) {
        SourceNode sourceNode = reliance.getNode(etlDataSource.getSourceKey());
        if (sourceNode == null) {
            sourceNode = new SourceNode(etlDataSource);
            reliance.addNode(sourceNode);
        }
        List<DataSource> baseDataSources = etlDataSource.getBasedSources();
        for (DataSource baseDataSource : baseDataSources) {
            if (reliance.containReliance(baseDataSource.getSourceKey())) {

                SourceNode baseSourceNode = reliance.getNode(baseDataSource.getSourceKey());
                if (baseSourceNode == null) {
                    baseSourceNode = new SourceNode(baseDataSource);
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
}
