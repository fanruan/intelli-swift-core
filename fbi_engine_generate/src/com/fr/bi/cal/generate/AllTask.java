/**
 *
 */
package com.fr.bi.cal.generate;

import com.fr.bi.cal.generate.index.IncreaseIndexGenerator;
import com.fr.bi.cal.generate.index.IndexGenerator;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessTable;
import com.fr.bi.conf.manager.singletable.data.SingleTableUpdateAction;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.utils.BIMapUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class AllTask extends AbstractCubeTask {


    public AllTask(long userId) {
        super(userId);
    }

    @Override
    protected Map<Integer, Set<ITableSource>> getGenerateTables() {
        Map<Integer, Set<ITableSource>> generateTable = new HashMap<Integer, Set<ITableSource>>();
        addOtherTables(generateTable);
        Set<BIBusinessPackage> packs = BIConfigureManagerCenter.getCubeManager().getGeneratingObject(biUser.getUserId()).getPacks();
        for (BIBusinessPackage pack : packs) {
            Set<BIBusinessTable> busiTable = pack.getBusinessTables();
            for (BIBusinessTable table : busiTable) {
                ITableSource source = table.getSource();
                if (source != null) {
                    BIMapUtils.mergeSetValueMap(generateTable, table.getSource().createGenerateTablesMap());
                }
            }
        }
        return generateTable;
    }

    @Override
    protected boolean checkCubeVersion(TableCubeFile cube) {
        return false;
    }

    private void addOtherTables(Map<Integer, Set<ITableSource>> generateTable) {
        HashSet<ITableSource> set = new HashSet<ITableSource>();
        Table key = BIConfigureManagerCenter.getCubeManager().getGeneratingObject(biUser.getUserId()).getUserInfo().getTableKey();
        if (key != null) {
            set.add(BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(key.getID(), biUser));
            generateTable.put(0, set);
        }
    }

    @Override
    public long getUserId() {
        return biUser.getUserId();
    }

    @Override
    protected IndexGenerator createGenerator(ITableSource source) {
        String md5 = source.fetchObjectCore().getID().getIdentityValue();
        TableCubeFile cube = new TableCubeFile(BIPathUtils.createTablePath(md5, biUser.getUserId()));
        if (!checkCubeVersion(cube)) {
            SingleTableUpdateAction action = BIConfigureManagerCenter.getPackageManager().getSingleTableUpdateManager(biUser.getUserId()).getSingleTableUpdateAction(new BITable(md5));
            switch (action.getUpdateType()) {
                case DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL:
                    return new IndexGenerator(source, biUser.getUserId(), cube.getTableVersion() + 1);
                case DBConstant.SINGLE_TABLE_UPDATE_TYPE.PART:
                    return new IncreaseIndexGenerator(source, biUser.getUserId(), cube.getTableVersion() + 1);
                case DBConstant.SINGLE_TABLE_UPDATE_TYPE.NEVER:
                    return null;
            }
        }
        return null;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }

    @Override
    public CubeTaskType getTaskType() {
        return CubeTaskType.ALL;
    }


}