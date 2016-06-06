package com.finebi.cube.gen.oper;

import com.finebi.cube.adapter.BIUserCubeManager;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.impl.pubsub.BIProcessor;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.ICube;
import com.finebi.cube.structure.ICubeTableEntityService;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.cal.log.BILogManager;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.stable.bridge.StableFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2016/4/5.
 *
 * @author Connery
 * @since 4.0
 */
public class BISourceDataTransport extends BIProcessor {
    protected CubeTableSource tableSource;
    protected Set<CubeTableSource> allSources;
    protected ICubeTableEntityService tableEntityService;
    protected ICube cube;
    protected List<ITableKey> parents = new ArrayList<ITableKey>();
    protected long version=0;

    public BISourceDataTransport(ICube cube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parentTableSource,long version) {
        this.tableSource = tableSource;
        this.allSources = allSources;
        this.cube = cube;
        tableEntityService = cube.getCubeTableWriter(BITableKeyUtils.convert(tableSource));
        this.version = version;
        initialParents(parentTableSource);
    }
    


    private void initialParents(Set<CubeTableSource> parentTableSource) {
        if (parentTableSource != null) {
            for (CubeTableSource tableSource : parentTableSource) {
                parents.add(new BITableKey(tableSource));
            }
        }
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        BILogManager biLogManager = StableFactory.getMarkedObject(BILogManagerProvider.XML_TAG, BILogManager.class);
        long t=System.currentTimeMillis();

        try {
            recordTableInfo();
            /*column的log先暂时不分开录,一张表下所有column读取时间都一致*/
            long columnTime=System.currentTimeMillis();
            long count = transport();
            if (null!=tableSource.getPersistentTable()) {
                long columneCostTime=System.currentTimeMillis()-columnTime;
                for (ICubeFieldSource iCubeFieldSource : tableEntityService.getFieldInfo()) {
                    biLogManager.infoColumn(tableSource.getPersistentTable(),iCubeFieldSource.getFieldName(),columneCostTime, -999);
                }
            }
            if (count >= 0) {
                tableEntityService.recordRowCount(count);
                tableEntityService.addVersion(version);
            }
            long tableCostTime=System.currentTimeMillis()-t;
            if (null!=tableSource.getPersistentTable()) {
                biLogManager.infoTable(tableSource.getPersistentTable(), tableCostTime, -999);

            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            if (null!=tableSource.getPersistentTable()) {
                biLogManager.errorTable(tableSource.getPersistentTable(), e.getMessage(), -999);
            }
        } finally {
            return null;
        }
    }

    @Override
    public void release() {
        tableEntityService.clear();
    }

    private void recordTableInfo() {
        if (tableSource.getSourceID().equals("4b97fa8f")) {
            System.out.println("fine");
        }
        ICubeFieldSource[] columns = getFieldsArray();
        List<ICubeFieldSource> columnList = new ArrayList<ICubeFieldSource>();
        for (ICubeFieldSource col : columns) {
            columnList.add(convert(col));
        }
        tableEntityService.recordTableStructure(columnList);
        if (!tableSource.isIndependent()) {
            tableEntityService.recordParentsTable(parents);
            tableEntityService.recordFieldNamesFromParent(getParentFieldNames());
        }
    }

    private Set<String> getParentFieldNames() {
        Set<ICubeFieldSource> parentFields = tableSource.getParentFields(allSources);
        Set<ICubeFieldSource> facetFields = tableSource.getFacetFields(allSources);
        Set<ICubeFieldSource> selfFields = tableSource.getSelfFields(allSources);
        Set<String> fieldNames = new HashSet<String>();
        for (ICubeFieldSource field : parentFields) {
            if (!containSameName(selfFields, field.getFieldName()) && containSameName(facetFields, field.getFieldName())) {
                fieldNames.add(field.getFieldName());
            }
        }
        return fieldNames;
    }

    private boolean containSameName(Set<ICubeFieldSource> set, String fieldName) {
        for (ICubeFieldSource field : set) {
            if (ComparatorUtils.equals(field.getFieldName(), fieldName)) {
                return true;
            }
        }
        return false;
    }

    private long transport() {
        List<ICubeFieldSource> fieldList = tableEntityService.getFieldInfo();
        ICubeFieldSource[] cubeFieldSources = new ICubeFieldSource[fieldList.size()];
        for (int i = 0; i < fieldList.size(); i++) {
            fieldList.get(i).setTableBelongTo(tableSource);
            cubeFieldSources[i] = fieldList.get(i);
        }
        
        return this.tableSource.read(new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue v) {
                try {
                    tableEntityService.addDataValue(v);
                } catch (BICubeColumnAbsentException e) {
                    e.printStackTrace();
                }
            }
        }, cubeFieldSources, new BIUserCubeManager(UserControl.getInstance().getSuperManagerID(), cube));
    }

    private ICubeFieldSource convert(ICubeFieldSource column) {
        return new BICubeFieldSource(tableSource, column.getFieldName(), column.getClassType(), column.getFieldSize());
    }

    private ICubeFieldSource[] getFieldsArray() {
        return tableSource.getFieldsArray(allSources);
    }

}
