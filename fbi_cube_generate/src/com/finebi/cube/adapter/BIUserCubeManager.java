package com.finebi.cube.adapter;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.Cube;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.fs.control.UserControl;

/**
 * This class created on 2016/4/15.
 *
 * @author Connery
 * @since 4.0
 */
public class BIUserCubeManager implements ICubeDataLoader {
    private BIUser user;
    private Cube cube;

    public BIUserCubeManager(long userID, Cube cube) {
        this.user = new BIUser(userID);
        this.cube = cube;
    }

    public BIUserCubeManager(BIUser user) {
        this.user = user;
        ICubeResourceDiscovery discovery = BIFactoryHelper.getObject(ICubeResourceDiscovery.class);
        ICubeResourceRetrievalService resourceRetrievalService = new BICubeResourceRetrieval(BICubeConfiguration.getConf(Long.toString(UserControl.getInstance().getSuperManagerID())));
        cube = new BICube(resourceRetrievalService, discovery);
    }


    public ICubeTableService getTableIndex(CubeTableSource tableSource) {
        return new BICubeTableAdapter(cube, tableSource);
    }


    @Override
    public BIKey getFieldIndex(BusinessField column) {
        return new IndexKey(column.getFieldName());

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
    public ICubeTableService getTableIndex(CubeTableSource tableSource, int start, int end) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public long getVersion() {
        return cube.getCubeVersion();
    }
}