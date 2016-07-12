//package com.finebi.cube.gen.oper.watcher;
//
//import com.finebi.cube.adapter.BIUserCubeManager;
//import com.finebi.cube.conf.BICubeConfigureCenter;
//import com.finebi.cube.conf.singletable.TableUpdate;
//import com.finebi.cube.exception.BICubeColumnAbsentException;
//import com.finebi.cube.impl.pubsub.BIProcessor;
//import com.finebi.cube.message.IMessage;
//import com.finebi.cube.structure.BITableKey;
//import com.finebi.cube.structure.Cube;
//import com.finebi.cube.structure.CubeTableEntityService;
//import com.finebi.cube.structure.ITableKey;
//import com.finebi.cube.utils.BITableKeyUtils;
//import com.fr.bi.common.inter.Traversal;
//import com.fr.bi.conf.log.BILogManager;
//import com.fr.bi.conf.provider.BILogManagerProvider;
//import com.fr.bi.stable.constant.BIBaseConstant;
//import com.fr.bi.stable.data.db.BICubeFieldSource;
//import com.fr.bi.stable.data.db.BIDataValue;
//import com.fr.bi.stable.data.db.ICubeFieldSource;
//import com.fr.bi.stable.data.source.CubeTableSource;
//import com.fr.bi.stable.utils.code.BILogger;
//import com.fr.bi.stable.utils.file.BIPathUtils;
//import com.fr.fs.control.UserControl;
//import com.fr.general.ComparatorUtils;
//import com.fr.stable.bridge.StableFactory;
//
//import java.util.*;
//
///**
// * Created by wuk on 16/7/12.
// */
//public class BISourceIncreasDataTransport extends BIProcessor {
//    protected CubeTableSource tableSource;
//    protected Set<CubeTableSource> allSources;
//    protected CubeTableEntityService tableEntityService;
//    protected Cube cube;
//    protected List<ITableKey> parents = new ArrayList<ITableKey>();
//    protected long version = 0;
//
//    public BISourceIncreasDataTransport(Cube cube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parentTableSource, long version) {
//        this.tableSource = tableSource;
//        this.allSources = allSources;
//        this.cube = cube;
//        tableEntityService = cube.getCubeTableWriter(BITableKeyUtils.convert(tableSource));
//        this.version = version;
//        initialParents(parentTableSource);
//    }
//
//
//    private void initialParents(Set<CubeTableSource> parentTableSource) {
//        if (parentTableSource != null) {
//            for (CubeTableSource tableSource : parentTableSource) {
//                parents.add(new BITableKey(tableSource));
//            }
//        }
//    }
//
//    @Override
//    public Object mainTask(IMessage lastReceiveMessage) {
//        BILogManager biLogManager = StableFactory.getMarkedObject(BILogManagerProvider.XML_TAG, BILogManager.class);
//        long t = System.currentTimeMillis();
//        try {
//            recordTableInfo();
//            long count = transport();
//            if (count >= 0) {
//                tableEntityService.recordRowCount(count);
//            }
//            tableEntityService.addVersion(version);
//            long tableCostTime = System.currentTimeMillis() - t;
//            if (null != tableSource.getPersistentTable()) {
//                System.out.println("table usage:" + tableCostTime);
//                biLogManager.infoTable(tableSource.getPersistentTable(), tableCostTime, UserControl.getInstance().getSuperManagerID());
//            }
//        } catch (Exception e) {
//            BILogger.getLogger().error(e.getMessage(), e);
//            if (null != tableSource.getPersistentTable()) {
//                biLogManager.errorTable(tableSource.getPersistentTable(), e.getMessage(), UserControl.getInstance().getSuperManagerID());
//            }
//        } finally {
//            return null;
//        }
//    }
//
//    @Override
//    public void release() {
//        tableEntityService.clear();
//    }
//
//    private void recordTableInfo() {
//        ICubeFieldSource[] columns = getFieldsArray();
//        List<ICubeFieldSource> columnList = new ArrayList<ICubeFieldSource>();
//        for (ICubeFieldSource col : columns) {
//            columnList.add(convert(col));
//        }
//        tableEntityService.recordTableStructure(columnList);
//        if (!tableSource.isIndependent()) {
//            tableEntityService.recordParentsTable(parents);
//            tableEntityService.recordFieldNamesFromParent(getParentFieldNames());
//        }
//    }
//
//    private Set<String> getParentFieldNames() {
//        Set<ICubeFieldSource> parentFields = tableSource.getParentFields(allSources);
//        Set<ICubeFieldSource> facetFields = tableSource.getFacetFields(allSources);
//        Set<ICubeFieldSource> selfFields = tableSource.getSelfFields(allSources);
//        Set<String> fieldNames = new HashSet<String>();
//        for (ICubeFieldSource field : parentFields) {
//            if (!containSameName(selfFields, field.getFieldName()) && containSameName(facetFields, field.getFieldName())) {
//                fieldNames.add(field.getFieldName());
//            }
//        }
//        return fieldNames;
//    }
//
//    private boolean containSameName(Set<ICubeFieldSource> set, String fieldName) {
//        for (ICubeFieldSource field : set) {
//            if (ComparatorUtils.equals(field.getFieldName(), fieldName)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private long transport() {
//        List<ICubeFieldSource> fieldList = tableEntityService.getFieldInfo();
//        ICubeFieldSource[] cubeFieldSources = new ICubeFieldSource[fieldList.size()];
//        for (int i = 0; i < fieldList.size(); i++) {
//            fieldList.get(i).setTableBelongTo(tableSource);
//            cubeFieldSources[i] = fieldList.get(i);
//        }
//
//        return this.tableSource.read(new Traversal<BIDataValue>() {
//            @Override
//            public void actionPerformed(BIDataValue v) {
//                try {
//                    tableEntityService.addDataValue(v);
//                } catch (BICubeColumnAbsentException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, cubeFieldSources, new BIUserCubeManager(UserControl.getInstance().getSuperManagerID(), cube));
//    }
//
//    private ICubeFieldSource convert(ICubeFieldSource column) {
//        return new BICubeFieldSource(tableSource, column.getFieldName(), column.getClassType(), column.getFieldSize());
//    }
//
//    private ICubeFieldSource[] getFieldsArray() {
//        return tableSource.getFieldsArray(allSources);
//    }
//    private long writeSimpleIndex() {
//        int oldCount = loadOldValue();
//        TreeSet<Integer> sortRemovedList = new TreeSet<Integer>(BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
//        oldCount = writeData(sortRemovedList, oldCount);
//        return oldCount;
//    }
//
//    protected int loadOldValue() {
//        BILogger.getLogger().info("now loading：" + dataSource.fetchObjectCore() + " old cube");
//        new TableCubeFile(BIPathUtils.createTableTempPath(dataSource.fetchObjectCore().getID().getIdentityValue(), loader.getUserId())).copyDetailValue(cube, loader.getNIOReaderManager(), oldTi.getRowCount());
//        BILogger.getLogger().info("loading：" + dataSource.fetchObjectCore() + " old cube finished");
//        return oldTi.getRowCount();
//    }
//    private int writeData(TreeSet<Integer> sortRemovedList, int oldCount) {
//        TableUpdate action = BICubeConfigureCenter.getTableUpdateManager().getSingleTableUpdateAction(dataSource.getPersistentTable());
//        oldCount = dealWithInsert(action, oldCount);
//        oldCount = dealWithModify(action, oldCount, sortRemovedList);
//        dealWithRemove(action, sortRemovedList);
//        return oldCount;
//    }
//}
