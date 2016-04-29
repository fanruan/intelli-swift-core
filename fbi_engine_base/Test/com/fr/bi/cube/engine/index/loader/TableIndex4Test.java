package com.fr.bi.cube.engine.index.loader;


import com.fr.bi.cube.engine.index.IDGroupValueIndex;
import com.fr.bi.cube.engine.memory.GroupValueInMemoryIndexManager;
import com.fr.bi.cube.engine.memory.writer.GroupValueInMemoryIndexWriterManager;
import com.fr.bi.cube.engine.store.BIBaseColumnKey;

import java.io.File;


/**
 * Created by Connery on 2014/12/10.
 */
public class TableIndex4Test implements ILinkIndexGeneratorWithCache {

    private TableLinkIndexManager tableLinkIndexManager;
    private int timeLast;
    private int amount;

    public TableIndex4Test(int timeLast, int amount) {
        this.timeLast = timeLast;
        this.amount = amount;
    }

    public void setTableLinkIndexManager(TableLinkIndexManager tableLinkIndexManager) {
        this.tableLinkIndexManager = tableLinkIndexManager;
    }

    @Override
    public void createInUseLinkedIndexMap(CubeTILoader cubeTILoader, BIBaseColumnKey biBaseColumnKey, File parentFile, long endTableCount, GroupValueInMemoryIndexManager groupValueInMemoryIndexManager) {
        GroupValueInMemoryIndexWriterManager groupValueInMemoryIndexWriterManager = null;
        if (groupValueInMemoryIndexManager != null) {
            groupValueInMemoryIndexWriterManager = groupValueInMemoryIndexManager.getGroupValueInMemoryIndexWriterManager();
            groupValueInMemoryIndexWriterManager.recordItemsAmount(amount);
        }
        int count = 1;
        double intervalTime = (double) this.timeLast / (double) this.amount;
        groupValueInMemoryIndexWriterManager.recordItemsAmount(this.amount);
        while (count++ <= amount) {
            Long timeRecord = System.currentTimeMillis();
            while (System.currentTimeMillis() - timeRecord < intervalTime) {
            }
            try {
                groupValueInMemoryIndexWriterManager.recordCurrentCount(count);
                groupValueInMemoryIndexWriterManager.write((long) count, (new IDGroupValueIndex(new int[1], (long) count)));
            } catch (Exception ex) {
            }

        }
    }
}