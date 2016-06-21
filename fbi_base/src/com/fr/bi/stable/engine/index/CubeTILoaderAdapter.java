package com.fr.bi.stable.engine.index;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;

/**
 * Created by 小灰灰 on 2016/1/22.
 */
public class CubeTILoaderAdapter implements ICubeDataLoader {
    private final RuntimeException NULL_EXCEPTION = new NullTableIndexException();

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
    public BIKey getFieldIndex(BusinessField column) {
        return null;
    }

    @Override
    public void releaseCurrentThread() {

    }

    @Override
    public ICubeTableService getTableIndex(CubeTableSource tableSource, int start, int end) {
        throw NULL_EXCEPTION;
    }

    @Override
    public ICubeTableService getTableIndex(CubeTableSource tableSource) {
        return null;
    }

    /**
     * 释放资源
     */
    @Override
    public void clear() {

    }

    @Override
    public long getVersion() {
        return 0;
    }
}