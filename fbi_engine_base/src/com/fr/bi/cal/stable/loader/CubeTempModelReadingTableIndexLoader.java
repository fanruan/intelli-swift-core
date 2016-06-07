package com.fr.bi.cal.stable.loader;

import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.BICore;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.engine.TempCubeTask;
import com.fr.bi.cal.stable.engine.index.loader.CubeAbstractLoader;
import com.fr.bi.cal.stable.tableindex.index.BITableIndex;
import com.fr.bi.common.inter.Release;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.structure.collection.map.TimeDeleteHashMap;
import com.fr.bi.stable.utils.BIUserUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.fs.control.UserControl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by 小灰灰 on 2014/10/17.
 */
public class CubeTempModelReadingTableIndexLoader extends CubeAbstractLoader {


    /**
     *
     */
    private static final long serialVersionUID = -8522501840851579695L;
    private static Release releaseObject = null;//当超时时还需要做其他一下删除操作
    private static Map<TempCubeTask, CubeTempModelReadingTableIndexLoader> userMap = new HashMap<TempCubeTask, CubeTempModelReadingTableIndexLoader>();
    private final Object LOCK = new Object();
    private TempCubeTask task;
    private String cubePath;//保存文件路径
    private ICubeTableService latestIndex = null;//最新的tableindex标识
    //多人同时访问的path队列
    private Queue<String> pathQueue = new ConcurrentLinkedQueue<String>();
    private SingleUserNIOReadManager manager = null;
    /**
     * 线程对应的tableIndex
     */
    private Map<Long, ICubeTableService> storedTableIndex = new HashMap<Long, ICubeTableService>();
    private Map<String, Integer> storedCount = new HashMap<String, Integer>();
    /**
     * 线程对应的NIOReader
     */
    private Map<String, ICubeTableService> storedIndexes = new HashMap<String, ICubeTableService>();
    private Map<String, SingleUserNIOReadManager> storedNIOManager = new HashMap<String, SingleUserNIOReadManager>();
    private Map<String, String> storedPath = new HashMap<String, String>();
    private TimeDeleteHashMap<String, ICubeTableService> tableIndexTime = new TimeDeleteHashMap<String, ICubeTableService>(new Traversal<String>() {
        @Override
        public void actionPerformed(String id) {
            releaseTableIndex(getTableIndexById(id));
        }
    });

    public CubeTempModelReadingTableIndexLoader(TempCubeTask task, long userId) {
        super(userId);
        this.task = task;
        this.manager = new SingleUserNIOReadManager(userId);
    }

    @Override
    public long getUserId() {
        return biUser.getUserId();
    }

    public static void setReleaseObject(Release taskReleaseObject) {
        if (releaseObject == null) {
            releaseObject = taskReleaseObject;
        }
    }

    @Override
    public ICubeTableService getTableIndex(CubeTableSource tableSource) {
        return null;
    }

    @Override
    public BIKey getFieldIndex(BusinessField column) {
        return null;
    }

    public static ICubeDataLoader getInstance(TempCubeTask task) {
        synchronized (CubeTempModelReadingTableIndexLoader.class) {
            Long key = task.getUserId();
            boolean useAdministrator = BIUserUtils.isAdministrator(task.getUserId());
            if (useAdministrator) {
                key = UserControl.getInstance().getSuperManagerID();
            }
            task.setUserId(key);

            CubeTempModelReadingTableIndexLoader manager = userMap.get(task);
            if (manager == null) {
                manager = new CubeTempModelReadingTableIndexLoader(task, key);
                userMap.put(task, manager);
            }
            return manager;
        }
    }
//
//    @Override
//    public TableIndex getTableIndexByPath(BITableID td) {
//        return this.getTableIndexByPath(new BITable(td));
//    }


    public ICubeTableService getTableIndex(BITableID id) {
        return getTableIndex(new BITable(id));
    }

    public void addCubePath(String cubePath) {
        synchronized (LOCK) {
            pathQueue.offer(cubePath);
        }
    }

    public void registerTableIndex(long threadId, ICubeTableService tableindex) {
        synchronized (LOCK) {
            String id = tableindex.getId();
            storedTableIndex.put(threadId, tableindex);
            storedCount.put(id, storedCount.get(id) == null ? 1 : storedCount.get(id) + 1);
        }
    }

    public ICubeTableService getTableIndex(BIField td) {
        return getTableIndex(td.getTableBelongTo());
    }

    public void releaseTableIndex(long threadId) {
        synchronized (LOCK) {
            ICubeTableService tableIndex = storedTableIndex.get(threadId);
            if (tableIndex != null) {
                String id = tableIndex.getId();
                storedCount.put(id, storedCount.get(id) - 1 < 0 ? 0 : storedCount.get(id) - 1);
                if (!isTableIndexInUse(tableIndex) && latestIndex != null && tableIndex != latestIndex) {
                    releaseTableIndex(tableIndex);
                }
                storedTableIndex.remove(threadId);
            }
        }
    }

    private ICubeTableService getTableIndexById(String id) {
        return storedIndexes.get(id);
    }

    private void releaseTableIndex(ICubeTableService tableIndex) {
        synchronized (LOCK) {
            if (tableIndex != null) {
                String id = tableIndex.getId();

                try {
                    tableIndex.clear();
                } catch (Exception e) {
                    BILogger.getLogger().error("TableIndex Release Failed!", e);
                }
                storedIndexes.remove(id);
                if (storedNIOManager.get(id) != null) {
                    storedNIOManager.get(id).clear();
                    storedNIOManager.remove(id);
                }
                storedCount.remove(id);
                String path = storedPath.get(id);
                if (path != null) {
                    BIFileUtils.delete(new File(BIBaseConstant.CACHE.getCacheDirectory() + File.separator + task.getMd5() + File.separator + path));
                }
                storedPath.remove(id);
                if (storedIndexes.isEmpty()) {
                    latestIndex = null;
                }
            }
        }
    }

    private boolean isTableIndexInUse(ICubeTableService tableIndex) {
        if (tableIndex != null) {
            //看看有没有其他人在用这个tableIndex
            int cnt = storedCount.get(tableIndex.getId());
            if (cnt > 0) {//如果有其他人在用
                return true;
            }
        }
        return false;
    }

    public void updateTime() {
        //userMap.updateTime(task);
        if (latestIndex != null) {
            tableIndexTime.updateTime(latestIndex.getId());
        }
    }

    /**
     * 根据业务包获取BITableIndex
     *
     * @param td
     * @return
     */
    public ICubeTableService getTableIndex(final Table td) {
        long threadId = Thread.currentThread().getId();
        if (storedTableIndex.get(threadId) != null) {
            return storedTableIndex.get(threadId);
        }

        while (storedIndexes.isEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }

        synchronized (LOCK) {
            return latestIndex;
        }
    }

    public ICubeTableService getTableIndex(BICore md5Core) {
        return getTableIndex(new BITable(md5Core.getID().getIdentityValue()));
    }

    private ICubeTableService getTableIndexByCurrentThread() {
        return storedTableIndex.get(Thread.currentThread().getId());
    }

    /**
     * 根据表的存储路径获取BITableIndex
     *
     * @param
     * @return
     */
    private ICubeTableService getTableIndexByPath(String pathSuffix) {
        String md5 = task.getMd5();
        String path = BIBaseConstant.CACHE.getCacheDirectory() + BIPathUtils.tablePath(md5) + File.separator + pathSuffix;
        return new BITableIndex(path, getNIOReaderManager());
    }

    public void update() {
        synchronized (LOCK) {
            cubePath = pathQueue.poll();
            ICubeTableService tableIndex = getTableIndexByPath(cubePath);
            storedIndexes.put(tableIndex.getId(), tableIndex);
            storedPath.put(tableIndex.getId(), cubePath);
            tableIndexTime.put(tableIndex.getId(), tableIndex);
            storedNIOManager.put(tableIndex.getId(), new SingleUserNIOReadManager(biUser.getUserId()));
            this.latestIndex = tableIndex;
        }
    }

    private void release() {
        synchronized (LOCK) {
            manager.clear();
            for (Map.Entry<String, ICubeTableService> entry : storedIndexes.entrySet()) {
                entry.getValue().clear();
            }
            for (Map.Entry<String, SingleUserNIOReadManager> entry : storedNIOManager.entrySet()) {
                entry.getValue().clear();
            }
            for (Map.Entry<String, String> entry : storedPath.entrySet()) {
                BIFileUtils.delete(new File(BIBaseConstant.CACHE.getCacheDirectory() + File.separator + task.getMd5() + File.separator + entry.getValue()));
            }
            storedTableIndex.clear();
            storedNIOManager.clear();
            storedIndexes.clear();
            storedPath.clear();
            storedCount.clear();
            cubePath = null;
            latestIndex = null;
        }
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
        ICubeTableService tableIndex = getTableIndexByCurrentThread();
        if (tableIndex != null) {
            String id = tableIndex.getId();
            if (storedNIOManager.get(id) != null) {
                return storedNIOManager.get(id);
            }
        }
        return manager;
    }

    @Override
    public long getVersion() {
        return 0;
    }
}