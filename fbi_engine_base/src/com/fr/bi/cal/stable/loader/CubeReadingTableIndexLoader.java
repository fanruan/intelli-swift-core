package com.fr.bi.cal.stable.loader;

import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.cube.memory.MemoryCubeFile;
import com.fr.bi.cal.stable.tableindex.index.BITableIndex;
import com.fr.bi.conf.utils.BIModuleManager;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.engine.index.AbstractTIPathLoader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
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
    private Map<String, AbstractTIPathLoader> childLoaderMap = new ConcurrentHashMap<String, AbstractTIPathLoader>();
    private BIUser user;

    public CubeReadingTableIndexLoader(long userId) {
        user = new BIUser(userId);
        for (Map.Entry<String, Class<? extends AbstractTIPathLoader>> entry : BIModuleManager.getLoaderClassMap().entrySet()) {
            try {
                childLoaderMap.put(entry.getKey(), BIConstructorUtils.constructObject(userId, entry.getValue(), (Map<Long, AbstractTIPathLoader>)entry.getValue().getDeclaredField("userMap").get(null), BIModuleManager.isModuleAllAdmin(entry.getKey())));
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
    public ICubeTableService getTableIndex(BITableID id) {
        return getTableIndex(new BITable(id));
    }

    @Override
    public ICubeTableService getTableIndex(final Table td) {
        return BIModuleUtils.getTableIndex(td, user, childLoaderMap);
    }

    @Override
    public ICubeTableService getTableIndex(BICore md5Core) {
        return BIModuleUtils.getTableIndex(md5Core, user, childLoaderMap);
    }

    @Override

    public ICubeTableService getTableIndex(BIField td) {
        return getTableIndex(td.getTableBelongTo());
    }

    @Override
    public BIKey getFieldIndex(BIField column) {
        return BIModuleUtils.getFieldIndex(column, user, childLoaderMap);
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
        for (AbstractTIPathLoader loader : childLoaderMap.values()) {
            loader.releaseCurrentThread();
        }
    }

    @Override
    public ICubeTableService getTableIndex(BICore core, int start, int end) {
        ICubeTableService ti = getTableIndex(core);
        MemoryCubeFile cube = new MemoryCubeFile(ti.getColumns().values().toArray(new DBField[ti.getColumns().size()]));
        int row = ti.getRowCount();
        if (row >= start){
            int count = Math.min(row, end) - start;
            int col = 0;
            for (BIKey key : ti.getColumns().keySet()){
                for (int i = 0; i < count; i ++){
                    cube.addDataValue(new BIDataValue(i, col, ti.getRow(key, i + count)));
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
            for (Map.Entry<String, AbstractTIPathLoader> entry : childLoaderMap.entrySet()) {
                AbstractTIPathLoader loader = entry.getValue();
                if (loader != null) {
                    loader.clear();
                }
            }
            userMap.clear();
        }
    }
}