package com.fr.bi.cal.stable.loader;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeDataLoaderCreator;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.cube.memory.MemoryCubeFile;
import com.fr.bi.cal.stable.tableindex.index.BITableIndex;
import com.fr.bi.conf.utils.BIModuleManager;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.module.BIModule;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.io.newio.NIOUtils;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.general.GeneralContext;
import com.fr.stable.EnvChangedListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CubeReadingTableIndexLoader implements ICubeDataLoader {

    /**
     *
     */
    private static final long serialVersionUID = -1387444792028060642L;
    private static Map<Long, ICubeDataLoader> userMap = new ConcurrentHashMap<Long, ICubeDataLoader>();
    private Map<String, ICubeDataLoader> childLoaderMap = new ConcurrentHashMap<String, ICubeDataLoader>();
    private BIUser user;

    public CubeReadingTableIndexLoader(long userId) {
        user = new BIUser(userId);
        for (BIModule module : BIModuleManager.getModules()) {
            ICubeDataLoaderCreator provider = module.getCubeDataLoaderCreator();
            if(provider == null) {
                continue;
            }
            try {
                childLoaderMap.put(module.getModuleName(), provider.fetchCubeLoader(user));
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }

    public static ICubeDataLoader getInstance(long userId) {
        return BIConstructorUtils.constructObject(userId, CubeReadingTableIndexLoader.class, userMap, false);
    }


    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {

            @Override
            public void envChanged() {
                CubeReadingTableIndexLoader.envChanged();
            }
        });
    }

    public static void envChanged() {
        synchronized (CubeReadingTableIndexLoader.class) {
            for (Map.Entry<Long, ICubeDataLoader> entry : userMap.entrySet()) {
                ICubeDataLoader loader = entry.getValue();
                if (loader != null) {
                    loader.clear();
                }
            }
            userMap.clear();
        }
    }

    @Override
    public ICubeTableService getTableIndex(CubeTableSource tableSource) {
        return BIModuleUtils.getTableIndex(tableSource, childLoaderMap);
    }

    @Override
    public BIKey getFieldIndex(BusinessField column) {
        return new IndexKey(column.getFieldName());
    }

    @Override
    public boolean needReadTempValue() {
        return false;
    }

    @Override
    public long getUserId() {
        return user.getUserId();
    }

    @Override
    public boolean needReadCurrentValue() {

        return true;
    }


    @Override
    public SingleUserNIOReadManager getNIOReaderManager() {
        return NIOUtils.getReadingManager(user.getUserId());
    }

    @Override
    public void releaseCurrentThread() {
        for (ICubeDataLoader loader : childLoaderMap.values()) {
            loader.releaseCurrentThread();
        }
    }

    @Override
    public ICubeTableService getTableIndex(CubeTableSource tableSource, int start, int end) {
        ICubeTableService ti = getTableIndex(tableSource);
        MemoryCubeFile cube = new MemoryCubeFile(ti.getColumns().values().toArray(new ICubeFieldSource[ti.getColumns().size()]));
        int row = ti.getRowCount();
        if (row >= start) {
            int count = Math.min(row, end) - start;
            int col = 0;
            for (BIKey key : ti.getColumns().keySet()) {
                ICubeColumnDetailGetter getter = ti.getColumnDetailReader(key);
                for (int i = 0; i < count; i++) {
                    cube.addDataValue(new BIDataValue(i, col,getter.getValue(i + count)));
                }
                col++;
            }
            cube.writeRowCount(count);
        }
        return new BITableIndex(cube);
    }

    /**
     * 释放资源
     */
    @Override
    public void clear() {
        synchronized (CubeReadingTableIndexLoader.class) {
            for (Map.Entry<String, ICubeDataLoader> entry : childLoaderMap.entrySet()) {
                ICubeDataLoader loader = entry.getValue();
                if (loader != null) {
                    loader.clear();
                }
            }
            userMap.clear();
            childLoaderMap.clear();
        }
    }

    @Override
    public long getVersion() {
        return 0;
    }
}