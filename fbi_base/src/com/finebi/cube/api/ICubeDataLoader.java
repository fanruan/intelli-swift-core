package com.finebi.cube.api;

import com.fr.bi.base.BICore;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;

public interface ICubeDataLoader extends Release {

    /**
     * 根据业务包获取BITableIndex
     *
     * @param td
     * @return
     */
    ICubeTableService getTableIndex(Table td);

    ICubeTableService getTableIndex(BICore core);

    ICubeTableService getTableIndex(BIField td);

    BIKey getFieldIndex(BIField column);

//    TableIndex getTableIndexByPath(BITableID td);

    ICubeTableService getTableIndex(BITableID id);

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

    ICubeTableService getTableIndex(BICore core, int start, int end);
}