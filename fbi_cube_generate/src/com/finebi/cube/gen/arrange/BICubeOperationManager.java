package com.finebi.cube.gen.arrange;

import com.finebi.cube.gen.mes.*;
import com.finebi.cube.gen.oper.*;
import com.finebi.cube.gen.oper.watcher.BICubeBuildFinishWatcher;
import com.finebi.cube.gen.oper.watcher.BIDataSourceBuildFinishWatcher;
import com.finebi.cube.gen.oper.watcher.BIPathBuildFinishWatcher;
import com.finebi.cube.gen.oper.watcher.BITableSourceBuildWatcher;
import com.finebi.cube.impl.operate.BIOperation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopicTag;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.ICube;
import com.finebi.cube.structure.ICubeTableEntityService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.utils.BICubePathUtils;
import com.finebi.cube.utils.BICubeRelationUtils;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.*;

/**
 * This class created on 2016/4/12.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeOperationManager {
    private ICube cube;
    private BIOperation<Object> cubeBuildFinishOperation;
    private BIOperation<Object> pathBuildFinishWatcher;
    private BIDataSourceBuildFinishWatcher dataSourceBuildFinishWatcher;
    private Set<CubeTableSource> registeredTransportTable;
    private Set<CubeTableSource> registeredFieldIndex;

    private Set<CubeTableSource> originalTableSet;
    private Map<CubeTableSource, BIOperation> tableSourceWatchers;

    public BICubeOperationManager(ICube cube, Set<CubeTableSource> originalTableSet) {
        this.cube = cube;
        registeredTransportTable = new HashSet<CubeTableSource>();
        registeredFieldIndex = new HashSet<CubeTableSource>();
        this.originalTableSet = originalTableSet;
        tableSourceWatchers = new HashMap<CubeTableSource, BIOperation>();
    }

    public void initialWatcher() {
        cubeBuildFinishOperation = generateCubeFinishOperation();
        pathBuildFinishWatcher = generatePathFinishOperation();
        dataSourceBuildFinishWatcher = getDataSourceBuildFinishWatcher();
    }

    public void generateDataSource(Set<List<Set<CubeTableSource>>> tableSourceSet) {
        registeredTransportTable.clear();
        registeredFieldIndex.clear();
        tableSourceWatchers.clear();

        generateTransportBuilder(tableSourceSet);
        generateFieldIndexBuilder(tableSourceSet);
        generateDataSourceFinishBuilder(tableSourceSet);
        subscribeDataSourceFinish();
    }

    private boolean isGenerated(CubeTableSource tableSource) {
        return registeredTransportTable.contains(tableSource);

    }

    private boolean isFieldIndexGenerated(CubeTableSource tableSource) {
        return registeredFieldIndex.contains(tableSource);

    }

    private void addGeneratedTable(CubeTableSource tableSource) {
        registeredTransportTable.add(tableSource);
    }

    private void addGeneratedFieldIndex(CubeTableSource tableSource) {
        registeredFieldIndex.add(tableSource);
    }

    /**
     * TODO 重构ITableSource结构，这种序列关系封装一下都行
     *
     * @param tableSourceSet list是指父类带有序列的集合。list内部的Set是指当前序列可能有多个
     *                       TableSource构成。最外层的Set是指多个List。
     */
    private void generateTransportBuilder(Set<List<Set<CubeTableSource>>> tableSourceSet) {
        Iterator<List<Set<CubeTableSource>>> it = tableSourceSet.iterator();
        while (it.hasNext()) {
            List<Set<CubeTableSource>> tableSourceList = it.next();
            generateSingleTransport(tableSourceList);
        }
    }

    private void generateSingleTransport(List<Set<CubeTableSource>> tableSourceSet) {
        Iterator<Set<CubeTableSource>> it = tableSourceSet.iterator();
        Set<CubeTableSource> parentTables = null;
        while (it.hasNext()) {
            Set<CubeTableSource> sameLevelTable = it.next();
            Iterator<CubeTableSource> sameLevelTableIt = sameLevelTable.iterator();
            while (sameLevelTableIt.hasNext()) {
                CubeTableSource tableSource = sameLevelTableIt.next();
                if (!isGenerated(tableSource)) {
                    BIOperation<Object> operation = new BIOperation<Object>(
                            tableSource.getSourceID(),
                            getDataTransportBuilder(cube, tableSource, originalTableSet, parentTables));
                    operation.setOperationTopicTag(BICubeBuildTopicTag.DATA_TRANSPORT_TOPIC);
                    operation.setOperationFragmentTag(BIFragmentUtils.generateFragment(BICubeBuildTopicTag.DATA_TRANSPORT_TOPIC, tableSource));
                    try {
                        if (parentTables == null) {
                            operation.subscribe(BICubeBuildTopicTag.START_BUILD_CUBE);
                        } else {
                            Iterator<CubeTableSource> parentTablesIt = parentTables.iterator();
                            while (parentTablesIt.hasNext()) {
                                CubeTableSource parentTable = parentTablesIt.next();
                                ITopicTag topicTag = BICubeBuildTopicTag.DATA_SOURCE_TOPIC;
                                operation.subscribe(BIStatusUtils.generateStatusFinish(topicTag
                                        , parentTable.getSourceID()));
                            }
                        }

                        watchTable(tableSource, BIStatusUtils.generateStatusFinish(BICubeBuildTopicTag.DATA_TRANSPORT_TOPIC
                                , tableSource.getSourceID()));
                        addGeneratedTable(tableSource);
                    } catch (Exception e) {
                        throw BINonValueUtils.beyondControl(e.getMessage(), e);
                    }
                }
            }
            parentTables = sameLevelTable;
        }
    }

    private BIOperation buildTableWatcher(CubeTableSource tableSource) {
        BIOperation<Object> operation = new BIOperation<Object>(
                tableSource.getSourceID(),
                getTableWatcherBuilder(cube.getCubeTableWriter(new BITableKey(tableSource))));
        ITopicTag topicTag = BICubeBuildTopicTag.DATA_SOURCE_TOPIC;
        operation.setOperationTopicTag(topicTag);
        operation.setOperationFragmentTag(BIFragmentUtils.generateFragment(topicTag, tableSource));
        return operation;
    }

    private BIOperation getTableSourceWatcher(CubeTableSource tableSource) {
        if (tableSourceWatchers.containsKey(tableSource)) {
            return tableSourceWatchers.get(tableSource);
        } else {
            BIOperation operation = buildTableWatcher(tableSource);
            tableSourceWatchers.put(tableSource, operation);
            return operation;
        }
    }


    private void generateFieldIndexBuilder(Set<List<Set<CubeTableSource>>> tableSourceSet) {
        Iterator<List<Set<CubeTableSource>>> it = tableSourceSet.iterator();
        while (it.hasNext()) {
            generateSingleFieldIndex(it.next());
        }
    }

    private void generateSingleFieldIndex(List<Set<CubeTableSource>> tableSourceSet) {
        Iterator<Set<CubeTableSource>> it = tableSourceSet.iterator();
        while (it.hasNext()) {
            Set<CubeTableSource> sameLevelTable = it.next();
            Iterator<CubeTableSource> sameLevelTableIt = sameLevelTable.iterator();
            while (sameLevelTableIt.hasNext()) {
                CubeTableSource tableSource = sameLevelTableIt.next();
                if (!isFieldIndexGenerated(tableSource)) {
                    ICubeFieldSource[] fields = tableSource.getFieldsArray(originalTableSet);
                    for (int i = 0; i < fields.length; i++) {
                        ICubeFieldSource field = fields[i];
                        Iterator<BIColumnKey> columnKeyIterator = BIColumnKey.generateColumnKey(field).iterator();
                        while (columnKeyIterator.hasNext()) {
                            BIColumnKey targetColumnKey = columnKeyIterator.next();
                            BIOperation<Object> operation = new BIOperation<Object>(
                                    tableSource.getSourceID() + "_" + targetColumnKey.getFullName(),
                                    getFieldIndexBuilder(cube, tableSource, field, targetColumnKey));
                            ITopicTag topicTag = BITopicUtils.generateTopicTag(tableSource);
                            operation.setOperationTopicTag(topicTag);
                            operation.setOperationFragmentTag(BIFragmentUtils.generateFragment(topicTag, targetColumnKey.getFullName()));
                            try {
                                operation.subscribe(BIStatusUtils.generateStatusFinish(BICubeBuildTopicTag.DATA_TRANSPORT_TOPIC, tableSource.getSourceID()));
                            } catch (Exception e) {
                                throw BINonValueUtils.beyondControl(e.getMessage(), e);
                            }
                            watchTable(tableSource, BIStatusUtils.generateStatusFinish(topicTag, targetColumnKey.getFullName()));
                            addGeneratedFieldIndex(tableSource);
                        }
                    }

                }
            }
        }
    }

    private void watchTable(CubeTableSource tableSource, IStatusTag tag) {
        BIOperation tableWatcher = getTableSourceWatcher(tableSource);
        try {
            tableWatcher.subscribe(tag);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }


    private void generateDataSourceFinishBuilder(Set<List<Set<CubeTableSource>>> tableSourceSet) {
        Iterator<List<Set<CubeTableSource>>> it = tableSourceSet.iterator();
        BIOperation<Object> operation = new BIOperation<Object>(
                BICubeBuildTopicTag.DATA_SOURCE_FINISH_TOPIC.getTopicName(),
                dataSourceBuildFinishWatcher);
        operation.setOperationTopicTag(BICubeBuildTopicTag.DATA_SOURCE_FINISH_TOPIC);
        operation.setOperationFragmentTag(BICubeBuildFragmentTag.getCubeOccupiedFragment(BICubeBuildTopicTag.DATA_SOURCE_FINISH_TOPIC));
        while (it.hasNext()) {
            generateSingleSourceFinish(it.next(), operation);
        }
    }

    private void generateSingleSourceFinish(List<Set<CubeTableSource>> tableSourceList, BIOperation<Object> operation) {
        Iterator<Set<CubeTableSource>> it = tableSourceList.iterator();
        while (it.hasNext()) {
            Iterator<CubeTableSource> sameLevelTable = it.next().iterator();
            while (sameLevelTable.hasNext()) {
                CubeTableSource tableSource = sameLevelTable.next();
                try {
                    IStatusTag tag = BIStatusUtils.generateStatusFinish(BICubeBuildTopicTag.DATA_SOURCE_TOPIC,
                            tableSource.getSourceID());
                    if (!operation.isSubscribed(tag)) {
                        operation.subscribe(tag);
                    }
                } catch (Exception e) {
                    throw BINonValueUtils.beyondControl(e);
                }
            }
        }
    }

    private void subscribeDataSourceFinish() {
        try {
            IStatusTag statusTag = BIStatusUtils.generateStatusFinish(BICubeBuildTopicTag.DATA_SOURCE_FINISH_TOPIC,
                    BICubeBuildFragmentTag.getCubeOccupiedFragment(BICubeBuildTopicTag.DATA_SOURCE_FINISH_TOPIC).getFragmentID().getIdentityValue());
            if (!cubeBuildFinishOperation.isSubscribed(statusTag)) {
                cubeBuildFinishOperation.subscribe(statusTag);
            }
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    private void subscribePathFinish() {
        try {
            IStatusTag statusTag = BIStatusUtils.generateStatusFinish(BICubeBuildTopicTag.PATH_FINISH_TOPIC,
                    BICubeBuildFragmentTag.getCubeOccupiedFragment(BICubeBuildTopicTag.PATH_FINISH_TOPIC).getFragmentID().getIdentityValue());
            if (!cubeBuildFinishOperation.isSubscribed(statusTag)) {
                cubeBuildFinishOperation.subscribe(statusTag);
            }
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }


    private BIOperation<Object> generateCubeFinishOperation() {
        BIOperation<Object> operation = new BIOperation<Object>(
                BICubeBuildTopicTag.FINISH_BUILD_CUBE.getTopicName(),
                getCubeBuildFinishWatcher());
        operation.setOperationTopicTag(BICubeBuildTopicTag.FINISH_BUILD_CUBE);
        operation.setOperationFragmentTag(BICubeBuildFragmentTag.getCubeOccupiedFragment(BICubeBuildTopicTag.FINISH_BUILD_CUBE));
        return operation;
    }

    private BIOperation<Object> generatePathFinishOperation() {
        BIOperation<Object> operation = new BIOperation<Object>(
                BICubeBuildTopicTag.PATH_FINISH_TOPIC.getTopicName(),
                getPathBuildFinishWatcher());
        operation.setOperationTopicTag(BICubeBuildTopicTag.PATH_FINISH_TOPIC);
        operation.setOperationFragmentTag(BICubeBuildFragmentTag.getCubeOccupiedFragment(BICubeBuildTopicTag.PATH_FINISH_TOPIC));

        return operation;
    }

    public void generateRelationBuilder(Set<BITableSourceRelation> relationSet) {
        if (relationSet != null && !relationSet.isEmpty()) {
            Iterator<BITableSourceRelation> it = relationSet.iterator();
            while (it.hasNext()) {
                BITableSourceRelation relation = it.next();
                try {
                    String sourceID = new BITableSourceRelationPath(relation).getSourceID();
                    BIOperation<Object> operation = new BIOperation<Object>(
                            sourceID,
                            getRelationBuilder(cube, relation));
                    operation.setOperationTopicTag(BICubeBuildTopicTag.PATH_TOPIC);
                    operation.setOperationFragmentTag(BIFragmentUtils.generateFragment(BICubeBuildTopicTag.PATH_TOPIC, sourceID));
                    operation.subscribe(BIStatusUtils.generateStatusFinish(BICubeBuildTopicTag.DATA_SOURCE_TOPIC, relation.getPrimaryTable().getSourceID()));
                    operation.subscribe(BIStatusUtils.generateStatusFinish(BICubeBuildTopicTag.DATA_SOURCE_TOPIC, relation.getForeignTable().getSourceID()));
                    pathFinishSubscribe(BIStatusUtils.generateStatusFinish(BICubeBuildTopicTag.PATH_TOPIC, sourceID));
                } catch (Exception e) {
                    throw BINonValueUtils.beyondControl(e.getMessage(), e);
                }
            }
            subscribePathFinish();
        }
    }

    private void pathFinishSubscribe(IStatusTag partFinish) {
        try {
            pathBuildFinishWatcher.subscribe(partFinish);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public void generateTableRelationPath(Set<BITableSourceRelationPath> relationPathSet) {
        if (relationPathSet != null && !relationPathSet.isEmpty()) {
            Iterator<BITableSourceRelationPath> it = relationPathSet.iterator();
            while (it.hasNext()) {
                BITableSourceRelationPath path = it.next();
                try {
                    String sourceID = path.getSourceID();
                    BIOperation<Object> operation = new BIOperation<Object>(
                            sourceID,
                            getTablePathBuilder(cube, path));
                    operation.setOperationTopicTag(BICubeBuildTopicTag.PATH_TOPIC);
                    operation.setOperationFragmentTag(BIFragmentUtils.generateFragment(BICubeBuildTopicTag.PATH_TOPIC, sourceID));
                    operation.subscribe(BIStatusUtils.generateStatusFinish(BICubeBuildTopicTag.PATH_TOPIC, new BITableSourceRelationPath(path.getLastRelation()).getSourceID()));
                    BITableSourceRelationPath frontPath = new BITableSourceRelationPath();
                    frontPath.copyFrom(path);
                    frontPath.removeLastRelation();
                    operation.subscribe(BIStatusUtils.generateStatusFinish(BICubeBuildTopicTag.PATH_TOPIC, frontPath.getSourceID()));

                    pathFinishSubscribe(BIStatusUtils.generateStatusFinish(BICubeBuildTopicTag.PATH_TOPIC, sourceID));

                } catch (Exception e) {
                    throw BINonValueUtils.beyondControl(e.getMessage(), e);
                }
            }
            subscribePathFinish();
        }
    }

    public void generateFieldRelationPath(Map<ICubeFieldSource, BITableSourceRelationPath> relationPathMap) {
        if (relationPathMap != null && !relationPathMap.isEmpty()) {
            Iterator<Map.Entry<ICubeFieldSource, BITableSourceRelationPath>> it = relationPathMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<ICubeFieldSource, BITableSourceRelationPath> entry = it.next();
                try {
                    ICubeFieldSource field = entry.getKey();
                    BITableSourceRelationPath path = entry.getValue();
                    String sourceID = BICubeBuildTopicManager.fieldPathFragmentID(path);
                    BIOperation<Object> operation = new BIOperation<Object>(
                            sourceID,
                            getFieldPathBuilder(cube, field, path));
                    /**
                     *
                     */
                    operation.setOperationTopicTag(BICubeBuildTopicTag.PATH_TOPIC);
                    operation.setOperationFragmentTag(BIFragmentUtils.generateFragment(BICubeBuildTopicTag.PATH_TOPIC, sourceID));
                    operation.subscribe(BIStatusUtils.generateStatusFinish(BITopicUtils.generateTopicTag(path.getFirstRelation().getPrimaryTable().getSourceID()), path.getFirstRelation().getPrimaryField().getFieldName()));
                    operation.subscribe(BIStatusUtils.generateStatusFinish(BICubeBuildTopicTag.DATA_SOURCE_TOPIC, path.getSourceID()));
                    pathFinishSubscribe(BIStatusUtils.generateStatusFinish(BICubeBuildTopicTag.PATH_TOPIC, sourceID));

                } catch (Exception e) {
                    throw BINonValueUtils.beyondControl(e.getMessage(), e);
                }
            }
            subscribePathFinish();
        }
    }

    protected BIRelationIndexGenerator getRelationBuilder(ICube cube, BITableSourceRelation relation) {
        return new BIRelationIndexGenerator(cube, BICubeRelationUtils.convert(relation));
    }

    protected BIFieldIndexGenerator getFieldIndexBuilder(ICube cube, CubeTableSource tableSource, ICubeFieldSource BICubeFieldSource, BIColumnKey targetColumnKey) {
        return new BIFieldIndexGenerator(cube, tableSource, BICubeFieldSource, targetColumnKey);
    }

    protected BITableSourceBuildWatcher getTableWatcherBuilder(ICubeTableEntityService tableEntityService) {
        return new BITableSourceBuildWatcher(tableEntityService);
    }

    protected BISourceDataTransport getDataTransportBuilder(ICube cube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parent) {
        return new BISourceDataTransport(cube, tableSource, allSources, parent);
    }

    protected BITablePathIndexBuilder getTablePathBuilder(ICube cube, BITableSourceRelationPath tablePath) {
        return new BITablePathIndexBuilder(cube, BICubePathUtils.convert(tablePath));
    }

    protected BIFieldPathIndexBuilder getFieldPathBuilder(ICube cube, ICubeFieldSource field, BITableSourceRelationPath tablePath) {
        return new BIFieldPathIndexBuilder(cube, field, BICubePathUtils.convert(tablePath));
    }

    protected BICubeBuildFinishWatcher getCubeBuildFinishWatcher() {
        return new BICubeBuildFinishWatcher();
    }

    protected BIPathBuildFinishWatcher getPathBuildFinishWatcher() {
        return new BIPathBuildFinishWatcher();
    }

    protected BIDataSourceBuildFinishWatcher getDataSourceBuildFinishWatcher() {
        return new BIDataSourceBuildFinishWatcher();
    }
}
