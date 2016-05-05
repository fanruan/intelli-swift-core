package com.fr.bi.cal.stable.loader;

import com.fr.bi.base.BIUser;
import com.fr.bi.cal.stable.tableindex.index.BIMultiTableIndex;
import com.fr.bi.cal.stable.tableindex.index.BITableIndex;
import com.fr.bi.common.inter.ValueCreator;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.source.SourceFile;
import com.fr.bi.stable.engine.index.AbstractTIPathLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.io.newio.NIOUtils;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.structure.collection.map.lru.LRUWithKHashMap;
import com.fr.bi.stable.utils.BIUserUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.fs.control.UserControl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 小灰灰 on 2015/12/16.
 */
public class BIReadingTableIndexLoader extends AbstractTIPathLoader {

    public static Map<Long, AbstractTIPathLoader> userMap = new ConcurrentHashMap<Long, AbstractTIPathLoader>();

    private static final int CACHE_SIZE = 128;

    private LRUWithKHashMap<Table, ICubeTableService> indexMap = new LRUWithKHashMap<Table, ICubeTableService>(CACHE_SIZE);

    public BIReadingTableIndexLoader(long userId) {
        super(userId);
        boolean useAdministrator = BIUserUtils.isAdministrator(userId);
        if (useAdministrator) {
            this.user = new BIUser((UserControl.getInstance().getSuperManagerID()));
        }
    }


    @Override
    public ICubeTableService getTableIndexByPath(final SourceFile file) {
        if (BIConfigureManagerCenter.getCubeManager().isReplacing(user.getUserId())) {
            return ICubeTableService.NULL_TABLE_INDEX;
        }
        final Table key = new BITable(file.getPath());
        return indexMap.get(key, new ValueCreator<ICubeTableService>() {

            @Override
            public ICubeTableService createNewObject() {
                if (BIConfigureManagerCenter.getCubeManager().isReplacing(user.getUserId())) {
                    return ICubeTableService.NULL_TABLE_INDEX;
                }
                return createTableIndex(file);
            }
        });
    }

    private ICubeTableService createTableIndex(SourceFile file) {
        if (file.isSource()) {
            return createTableIndex(new BITable(file.getPath()));
        }
        ICubeTableService[] tis = new ICubeTableService[file.getChildren().size()];
        for (int i = 0; i < tis.length; i++) {
            tis[i] = createTableIndex(file.getChildren().get(i));
        }
        return new BIMultiTableIndex(tis);
    }


    private ICubeTableService createTableIndex(final Table key) {
        return indexMap.get(key, new ValueCreator<ICubeTableService>() {

            @Override
            public ICubeTableService createNewObject() {
                if (BIConfigureManagerCenter.getCubeManager().isReplacing(user.getUserId())) {
                    return ICubeTableService.NULL_TABLE_INDEX;
                }
                try {
                    String path = BIPathUtils.createTablePath(key.getID().getIdentityValue(), user.getUserId());
                    return new BITableIndex(path, NIOUtils.getReadingManager(user.getUserId()));
                } catch (Exception e) {
                    BILogger.getLogger().error("Can`t Find Cube Of Table : " + key.toString() + ", Please Check Your Database Connection");
                }
                return null;
            }
        });

    }

    @Override
    public void releaseResource() {
        synchronized (this) {
            if(indexMap != null){
                indexMap.releaseResource();
            }
            SingleUserNIOReadManager manager = NIOUtils.getReadingManager(user.getUserId());
            if(manager != null){
                manager.releaseResource();
            }
        }
    }
}