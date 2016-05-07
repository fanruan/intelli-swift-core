package com.finebi.cube.adapter;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.ICube;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;

/**
 * This class created on 2016/4/15.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(module = IModuleFactory.CUBE_BASE_MODULE, factory = BIMateFactory.CUBE_BASE
        , implement = ICubeDataLoader.class)
public class BIUserCubeManager implements ICubeDataLoader {
    private BIUser user;
    private ICube cube;

    public BIUserCubeManager(long userID, ICube cube) {
        this.user = new BIUser(userID);
        this.cube = cube;
    }

    public BIUserCubeManager(BIUser user) {
        this.user = user;
        cube = new BICube(new BICubeResourceRetrieval(new BICubeConfiguration()));
    }

    @Override
    public ICubeTableService getTableIndex(Table td) {
        return getTableIndex(td.getID());
    }

    @Override
    public ICubeTableService getTableIndex(BICore core) {
        return getTableIndex(BIConfigureManagerCenter.getDataSourceManager().getTableSourceByCore(core, user));
    }

    public ICubeTableService getTableIndex(ITableSource tableSource) {

        return new BICubeTableAdapter(cube, tableSource);
    }

    @Override
    public ICubeTableService getTableIndex(BIField td) {
        return getTableIndex(td.getTableBelongTo());
    }

    @Override
    public BIKey getFieldIndex(BIField column) {
        return new IndexKey(column.getFieldName());
    }

    @Override
    public ICubeTableService getTableIndex(BITableID id) {
        BICore core = BIConfigureManagerCenter.getDataSourceManager().getCoreByTableID(id, user);
        return getTableIndex(core);
    }

    @Override
    public long getUserId() {
        return user.getUserId();
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
        return null;
    }

    @Override
    public void clear() {

    }
}