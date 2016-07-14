/**
 *
 */
package com.fr.bi.cal.generate;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.singletable.TableUpdate;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.fr.bi.cal.generate.index.IncreaseIndexGenerator;
import com.fr.bi.cal.generate.index.IndexGenerator;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class AllTask extends AbstractCubeTask {


    public AllTask(long userId) {
        super(userId);
    }

    @Override
    protected Map<Integer, Set<CubeTableSource>> getGenerateTables() {
        Map<Integer, Set<CubeTableSource>> generateTable = new HashMap<Integer, Set<CubeTableSource>>();
        addOtherTables(generateTable);
       

        Set<IBusinessPackageGetterService> packs = BICubeConfigureCenter.getPackageManager().getAllPackages(biUser.getUserId());
        for (IBusinessPackageGetterService pack : packs) {
            Set<BIBusinessTable> busiTable = pack.getBusinessTables();
            for (BIBusinessTable table : busiTable) {
                CubeTableSource source = table.getTableSource();
                if (source != null) {
                    BICollectionUtils.mergeSetValueMap(generateTable, table.getTableSource().createGenerateTablesMap());
                }
            }
        }
        return generateTable;
    }

    @Override
    protected boolean checkCubeVersion(TableCubeFile cube) {
        return false;
    }

    private void addOtherTables(Map<Integer, Set<CubeTableSource>> generateTable) {
    }

    @Override
    public long getUserId() {
        return biUser.getUserId();
    }

    @Override
    protected IndexGenerator createGenerator(CubeTableSource source) {
        String md5 = source.fetchObjectCore().getID().getIdentityValue();
        TableCubeFile cube = new TableCubeFile(BIPathUtils.createTablePath(md5, biUser.getUserId()));
        if (!checkCubeVersion(cube)) {
            TableUpdate action = BICubeConfigureCenter.getTableUpdateManager().getSingleTableUpdateAction(source.getPersistentTable());
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
