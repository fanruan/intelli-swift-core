package com.fr.bi.cal.stable.loader;

import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.cube.memory.MemoryCubeFile;
import com.fr.bi.cal.stable.tableindex.index.BITableIndex;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.source.ITableSource;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;

/**
 * Created by 小灰灰 on 2016/1/21.
 */
public class MemoryTableIndexLoader implements ICubeDataLoader {
    private BIUser user;
    /**
     * 根据业务包获取BITableIndex
     *
     * @param td
     * @return
     */
    @Override
    public ICubeTableService getTableIndex(Table td) {
        return null;
    }

    @Override
    public ICubeTableService getTableIndex(BICore md5Core) {
        ITableSource source = BIModuleUtils.getSource(md5Core, user);
        final MemoryCubeFile cube = new MemoryCubeFile(source.getFieldsArray(null));
        cube.writeRowCount(source.read(new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue data) {
                cube.addDataValue(data);
            }
        }, source.getFieldsArray(null), this));
        return new BITableIndex(cube);
    }

    @Override
    public ICubeTableService getTableIndex(BIField td) {
        return null;
    }

    @Override
    public BIKey getFieldIndex(BIField column) {
        return null;
    }

    @Override
    public ICubeTableService getTableIndex(BITableID id) {
        return null;
    }

    /**
     * 这里的userId不能乱用，只能访问公共属性的时候可以用这个userId
     *
     * @return
     */
    @Override
    public long getUserId() {
        return 0;
    }

    @Override
    public boolean needReadTempValue() {
        return false;
    }

    @Override
    public boolean needReadCurrentValue() {
        return false;
    }

    @Override
    public SingleUserNIOReadManager getNIOReaderManager() {
        return null;
    }

    @Override
    public void releaseCurrentThread() {

    }

    @Override
    public ICubeTableService getTableIndex(BICore core, int start, int end) {
        ITableSource source = BIModuleUtils.getSource(core, user);
        final MemoryCubeFile cube = new MemoryCubeFile(source.getFieldsArray(null));
        cube.writeRowCount(source.read4Part(new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue data) {
                cube.addDataValue(data);
            }
        }, source.getFieldsArray(null), this, start, end));
        return new BITableIndex(cube);
    }

    /**
     * 释放资源
     */
    @Override
    public void clear() {

    }
}