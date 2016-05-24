package com.fr.bi.stable.connection;

import com.fr.bi.base.FinalInt;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.engine.index.utils.TableIndexUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;

public class DirectTableConnection {
    private BIKey sIndex;
    private BIKey eIndex;
    private DirectTableConnection next;
    private DirectTableConnection last;
    private Table start;
    private Table end;
    private transient ICubeTableService sti;
    private transient ICubeTableService eti;


    public DirectTableConnection(Table sTable, BIKey sIndex, ICubeTableService sti,
                                 Table eTable, BIKey eIndex, ICubeTableService eti) {
        this.sIndex = sIndex;
        this.start = sTable;
        this.eIndex = eIndex;
        this.end = eTable;
        this.sti = sti;
        this.eti = eti;
    }

    private ICubeTableService getStartTI() {
        synchronized (this) {
            return sti;
        }
    }

    private ICubeTableService getEndTI() {
        synchronized (this) {
            return eti;
        }
    }

    public BIKey getStartIndex() {
        return sIndex;
    }

    public BIKey getEndIndex() {
        return eIndex;
    }

    public DirectTableConnection getNext() {
        return next;
    }

    public void setNext(DirectTableConnection next) {
        this.next = next;
        this.next.setLast(this);
    }

    public DirectTableConnection getLast() {
        return last;
    }

    private void setLast(DirectTableConnection last) {
        this.last = last;
    }

    public GroupValueIndex[] getConnectionIndices(int[] rows) {
        Object[] obs = getStartTI().getRow(sIndex, rows);
        return getEndTI().getIndexes(eIndex, obs);
    }

    /**
     * @param row
     * @return
     */
    public int getParentTableValue(int row) {
        GroupValueIndex gvi = getConnectionIndices(new int[]{row})[0];
        if (gvi == null) {
            return -1;
        }
        //parent表中 gvi肯定有且只有一个值，只支持一个值;
        final FinalInt r = new FinalInt();
        gvi.BrokenableTraversal(new BrokenTraversalAction() {
            @Override
            public boolean actionPerformed(int rowIndex) {
                r.value = rowIndex;
                return true;
            }
        });
        if (next != null && r.value != -1) {
            return next.getParentTableValue(r.value);
        }
        return r.value;
    }

    public long getFinalRowLength() {
        if (next != null) {
            return next.getFinalRowLength();
        } else {
            return getEndTI().getRowCount();
        }
    }

    /**
     * 获取主表的值
     *
     * @param value
     * @return
     */
    public Object[] getParentTableValues(Table table, Object value, BIKey startIndex, BIKey endIndex, ICubeDataLoader loader) {
        GroupValueIndex[] gvi = loader.getTableIndex(table).getIndexes(startIndex, new Object[]{value});
        return getParentTableValuesbyKeyValue(gvi, endIndex);
    }

    public Object[] getParentTableValues(GroupValueIndex gvi, BIKey endIndex) {
        return getParentTableValuesbyKeyValue(new GroupValueIndex[]{gvi}, endIndex);
    }

    /**
     * @return
     */
    private Object[] getParentTableValuesbyKeyValue(GroupValueIndex[] gvi, BIKey endIndex) {
        Object[] result = TableIndexUtils.getValueFromGvi(getStartTI(), sIndex, gvi);
        GroupValueIndex[] sgvi = getEndTI().getIndexes(eIndex, result);
        if (next != null) {
            return next.getParentTableValuesbyKeyValue(sgvi, endIndex);
        } else {
            return TableIndexUtils.getValueFromGvi(getEndTI(), endIndex, sgvi);
        }
    }

    /**
     * 释放
     */
    public void release() {
        sti = null;
        eti = null;
        if (next != null) {
            next.release();
        }
    }
}