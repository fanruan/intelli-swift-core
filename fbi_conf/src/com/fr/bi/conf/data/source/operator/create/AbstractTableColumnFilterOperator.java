package com.fr.bi.conf.data.source.operator.create;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BICore;
import com.fr.bi.base.FinalInt;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.BIColumn;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBTable;
import com.fr.bi.stable.data.source.ITableSource;
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
    public DBTable getBITable(DBTable[] tables) {
        DBTable DBTable = getBITable();
        for (int i = 0; i < tables.length; i++) {
            for (int j = 0; j < tables[i].getBIColumnLength(); j++) {
                DBTable.addColumn(tables[i].getBIColumn(j));
            }
        }
        return DBTable;
    }

    protected abstract GroupValueIndex createFilterIndex(List<? extends ITableSource> parents, ICubeDataLoader loader);

    @Override
    public int writeSimpleIndex(final Traversal<BIDataValue> travel, List<? extends ITableSource> parents, ICubeDataLoader loader) {
        final ICubeTableService ti = loader.getTableIndex(getSingleParentMD5(parents));
        DBTable ptable = parents.get(0).getDbTable();
        final BIColumn[] columns = ptable.getColumnArray();
        GroupValueIndex fgvi =createFilterIndex(parents ,loader);
        if (fgvi == null){
            return 0;
        }
        fgvi.Traversal(new SingleRowTraversalAction() {
            int row = 0;

            @Override
            public void actionPerformed(int rowIndices) {
                for (int i = 0; i < columns.length; i++) {
                    travel.actionPerformed(new BIDataValue(row, i, ti.getRow(new IndexKey(columns[i].getFieldName()), rowIndices)));
                }
                row++;
            }
        });
        return fgvi.getRowsCountWithData();
    }
    private static final int STEP = 1000;
    @Override
    public int writePartIndex(final Traversal<BIDataValue> travel, List<? extends ITableSource> parents, ICubeDataLoader loader, int startCol, final int start, final int end) {
        ICubeTableService ti = loader.getTableIndex(getSingleParentMD5(parents), 0 , STEP);
        int index = 0;
        final FinalInt currentRow = new FinalInt();
        currentRow.i = -1;
        final FinalInt writeRow = new FinalInt();
        DBTable ptable = parents.get(0).getDbTable();
        final BIColumn[] columns = ptable.getColumnArray();
        while (ti.getRowCount() > 0){
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
            });
            fgvi.BrokenableTraversal(new BrokenTraversalAction() {
                @Override
                public boolean actionPerformed(int row) {
                    currentRow.i ++;
                    if (currentRow.i < start ){
                        return true;
                    }
                    if (currentRow.i > end ){
                        return false;
                    }
                    for (int i = 0; i < columns.length; i++) {
                        travel.actionPerformed(new BIDataValue(writeRow.i, i, tableIndex.getRowValue(new IndexKey(columns[i].getFieldName()), row)));
                    }
                    writeRow.i++;
                    return true;
                }
            });
            index++;
            ti = loader.getTableIndex(getSingleParentMD5(parents), (index - 1) * STEP , index * STEP);
        }
        return writeRow.i;
    }

}