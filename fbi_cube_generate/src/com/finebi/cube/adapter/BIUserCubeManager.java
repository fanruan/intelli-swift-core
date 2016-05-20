package com.finebi.cube.adapter;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.ICube;
import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.utils.BIIDUtils;

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
        ICubeResourceDiscovery discovery = BIFactoryHelper.getObject(ICubeResourceDiscovery.class);
        ICubeResourceRetrievalService resourceRetrievalService = new BICubeResourceRetrieval(BICubeConfiguration.getConf(Long.toString(user.getUserId())));
        cube = new BICube(resourceRetrievalService, discovery);
    }

    @Override
    public ICubeTableService getTableIndex(Table td) {
        if (BIIDUtils.isFakeTable(td.getID().getIdentityValue())) {
            return getTableIndex(BIBasicCore.generateValueCore(td.getID().getIdentityValue()));
        } else {
            return getTableIndex(td.getID());
        }
    }


    @Override
    public ICubeTableService getTableIndex(BICore core) {
        ICubeTableSource source = BIModuleUtils.getSourceByCore(core, user);
        return getTableIndex(source);
    }

    public ICubeTableService getTableIndex(ICubeTableSource tableSource) {

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
        ICubeTableSource source = BIModuleUtils.getSourceByID(id, user);
        return getTableIndex(source);
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