package com.fr.bi.stable.engine.index;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BICore;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;

/**
 * Created by 小灰灰 on 2016/1/22.
 */
public class CubeTILoaderAdapter implements ICubeDataLoader {
    private final RuntimeException NULL_EXCEPTION = new NullTableIndexException();
    /**
     * 根据业务包获取BITableIndex
     *
     * @param td
     * @return
     */
    @Override
    public ICubeTableService getTableIndex(Table td) {
        throw NULL_EXCEPTION;
    }

    @Override
    public ICubeTableService getTableIndex(BICore md5Core) {
        throw NULL_EXCEPTION;
    }

    @Override
    public ICubeTableService getTableIndex(BIField td) {
        throw NULL_EXCEPTION;
    }

    @Override
    public BIKey getFieldIndex(BIField column) {
        throw NULL_EXCEPTION;
    }

    @Override
    public ICubeTableService getTableIndex(BITableID id) {
        throw NULL_EXCEPTION;
    }

    /**
     * 这里的userId不能乱用，只能访问公共属性的时候可以用这个userId
     *
     * @return
     */
    @Override
    public long getUserId() {
        throw NULL_EXCEPTION;
    }

    @Override
    public boolean needReadTempValue() {
        throw NULL_EXCEPTION;
    }

    @Override
    public boolean needReadCurrentValue() {
        throw NULL_EXCEPTION;
    }

    @Override
    public SingleUserNIOReadManager getNIOReaderManager() {
        throw NULL_EXCEPTION;
    }

    @Override
    public void releaseCurrentThread() {

    }

    @Override
    public ICubeTableService getTableIndex(BICore core, int start, int end) {
        throw NULL_EXCEPTION;
    }

    /**
     * 释放资源
     */
    @Override
    public void clear() {

    }
}