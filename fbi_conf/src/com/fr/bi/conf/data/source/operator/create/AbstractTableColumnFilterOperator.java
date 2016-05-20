package com.fr.bi.conf.data.source.operator.create;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BICore;
import com.fr.bi.base.FinalInt;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.engine.index.CubeTILoaderAdapter;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;

import java.util.List;

/**
 * Created by 小灰灰 on 2016/5/11.
 */
public abstract class AbstractTableColumnFilterOperator extends AbstractCreateTableETLOperator {

    private static final long serialVersionUID = 1177276899686204275L;

    public AbstractTableColumnFilterOperator(long userId) {
        super(userId);
    }

    public AbstractTableColumnFilterOperator() {

    }

    @Override
    public IPersistentTable getBITable(IPersistentTable[] tables) {
        IPersistentTable persistentTable = getBITable();
        for (int i = 0; i < tables.length; i++) {
            for (int j = 0; j < tables[i].getFieldSize(); j++) {
                persistentTable.addColumn(tables[i].getField(j));
            }
        }
        return persistentTable;
    }

    protected abstract GroupValueIndex createFilterIndex(List<? extends ICubeTableSource> parents, ICubeDataLoader loader);

    @Override
    public int writeSimpleIndex(final Traversal<BIDataValue> travel, List<? extends ICubeTableSource> parents, ICubeDataLoader loader) {
        final ICubeTableService ti = loader.getTableIndex(getSingleParentMD5(parents));
        IPersistentTable ptable = parents.get(0).getPersistentTable();
        final List<PersistentField> columns = ptable.getFieldList();
        GroupValueIndex fgvi = createFilterIndex(parents, loader);
        if (fgvi == null) {
            return 0;
        }
        fgvi.Traversal(new SingleRowTraversalAction() {
            int row = 0;

            @Override
            public void actionPerformed(int rowIndices) {
                for (int i = 0; i < columns.size(); i++) {
                    travel.actionPerformed(new BIDataValue(row, i, ti.getRow(new IndexKey(columns.get(i).getFieldName()), rowIndices)));
                }
                row++;
            }
        });
        return fgvi.getRowsCountWithData();
    }

    private static final int STEP = 100;

    @Override
    public int writePartIndex(final Traversal<BIDataValue> travel, List<? extends ICubeTableSource> parents, ICubeDataLoader loader, int startCol, final int start, final int end) {
        ICubeTableService ti = loader.getTableIndex(getSingleParentMD5(parents), 0, STEP);
        int index = 0;
        final FinalInt currentRow = new FinalInt();
        currentRow.value = -1;
        final FinalInt writeRow = new FinalInt();
        IPersistentTable ptable = parents.get(0).getPersistentTable();
        final List<PersistentField> columns = ptable.getFieldList();
        do {
            final ICubeTableService tableIndex = ti;
            GroupValueIndex fgvi = createFilterIndex(parents, new CubeTILoaderAdapter() {
                @Override
                public ICubeTableService getTableIndex(Table td) {
                    return tableIndex;
                }

                @Override
                public ICubeTableService getTableIndex(BIField td) {
                    return tableIndex;
                }

                @Override
                public ICubeTableService getTableIndex(BICore md5Core) {
                    return tableIndex;
                }

                @Override
                public ICubeTableService getTableIndex(BITableID id) {
                    return tableIndex;
                }

                public ICubeTableService getTableIndex(BICore core, int start, int end) {
                    return tableIndex;
                }

                public long getUserId() {
                    return user.getUserId();
                }
            });
            if (fgvi.BrokenableTraversal(new BrokenTraversalAction() {
                @Override
                public boolean actionPerformed(int row) {
                    currentRow.value++;
                    if (currentRow.value < start) {
                        return false;
                    }
                    if (currentRow.value > end) {
                        return true;
                    }
                    for (int i = 0; i < columns.size(); i++) {
                        travel.actionPerformed(new BIDataValue(writeRow.value, i, tableIndex.getRow(new IndexKey(columns.get(i).getFieldName()), row)));
                    }
                    writeRow.value++;
                    return false;
                }
            })) {
                break;
            }
            index++;
            ti = loader.getTableIndex(getSingleParentMD5(parents), index* STEP, (index + 1)* STEP);
        }
        while (ti.getRowCount() != 0 && !hasTopBottomFilter());
        return writeRow.value;
    }

    protected abstract boolean hasTopBottomFilter();

}