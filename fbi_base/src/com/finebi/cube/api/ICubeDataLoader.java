package com.finebi.cube.api;

import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;

public interface ICubeDataLoader extends Release {

    /**
     * 根据业务包获取BITableIndex
     *
     * @param td
     * @return
     */

    ICubeTableService getTableIndex(CubeTableSource tableSource);


    BIKey getFieldIndex(BusinessField column);


    /**
     * 这里的userId不能乱用，只能访问公共属性的时候可以用这个userId
     *
     * @return
     */
    long getUserId();

    boolean needReadTempValue();

    boolean needReadCurrentValue();


    SingleUserNIOReadManager getNIOReaderManager();

    void releaseCurrentThread();

    ICubeTableService getTableIndex(CubeTableSource tableSource, int start, int end);
}