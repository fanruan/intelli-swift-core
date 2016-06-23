package com.fr.bi.cal.stable.loader;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.adapter.BICubeTableAdapter;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.BICubeLocation;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.structure.BICube;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.engine.TempCubeTask;
import com.fr.bi.cal.stable.engine.index.loader.CubeAbstractLoader;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.inter.Release;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.structure.collection.map.TimeDeleteHashMap;
import com.fr.bi.stable.utils.BIUserUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.fs.control.UserControl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
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
    private String cubePath;//保存文件路径st
    private ICubeTableService latestIndex = null;//最新的tableindex标识
    //多人同时访问的path队列
    private Queue<String> pathQueue = new ConcurrentLinkedQueue<String>();
    private SingleUserNIOReadManager manager = null;
    /**
     * 线程对应的tableIndex
     */
    private Map<Long, ICubeTableService> storedTableIndex = new HashMap<Long, ICubeTableService>();
    private Map<Long, Integer> storedCount = new HashMap<Long, Integer>();
    /**
     * 线程对应的NIOReader
     */
    private Map<Long, ICubeTableService> storedIndexes = new HashMap<Long, ICubeTableService>();
    private Map<Long, SingleUserNIOReadManager> storedNIOManager = new HashMap<Long, SingleUserNIOReadManager>();
    private Map<Long, String> storedPath = new HashMap<Long, String>();
    private TimeDeleteHashMap<Long, ICubeTableService> tableIndexTime = new TimeDeleteHashMap<Long, ICubeTableService>(new Traversal<Long>() {
        @Override
        public void actionPerformed(Long id) {
            releaseTableIndex(getTableIndexByVersion(id));
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


    public void addCubePath(String cubePath) {
        synchronized (LOCK) {
            pathQueue.offer(cubePath);
        }
    }


    public void registerTableIndex(long threadId, ICubeTableService tableindex) {
        synchronized (LOCK) {
            Long version = tableindex.getTableVersion(null);
            storedTableIndex.put(threadId, tableindex);
            storedCount.put(version, storedCount.get(version) == null ? 1 : storedCount.get(version) + 1);
        }
    }


    public void releaseTableIndex(long threadId) {
        synchronized (LOCK) {
            ICubeTableService tableIndex = storedTableIndex.get(threadId);
            if (tableIndex != null) {
                Long version = tableIndex.getTableVersion(null);
                storedCount.put(version, storedCount.get(version) - 1 < 0 ? 0 : storedCount.get(version) - 1);
                if (!isTableIndexInUse(tableIndex) && latestIndex != null && tableIndex != latestIndex) {
                    releaseTableIndex(tableIndex);
                }
                storedTableIndex.remove(threadId);
            }
        }
    }

    private ICubeTableService getTableIndexByVersion(Long version) {
        return storedIndexes.get(version);
    }

    private void releaseTableIndex(ICubeTableService tableIndex) {
        synchronized (LOCK) {
            if (tableIndex != null) {
                Long version = tableIndex.getTableVersion(null);

                try {
                    tableIndex.clear();
                } catch (Exception e) {
                    BILogger.getLogger().error("TableIndex Release Failed!", e);
                }
                storedIndexes.remove(version);
                if (storedNIOManager.get(version) != null) {
                    storedNIOManager.get(version).clear();
                    storedNIOManager.remove(version);
                }
                storedCount.remove(version);
                String path = storedPath.get(version);
                if (path != null) {
                    BIFileUtils.delete(new File(BIBaseConstant.CACHE.getCacheDirectory() + File.separator + task.getMd5() + File.separator + path));
                }
                storedPath.remove(version);
                if (storedIndexes.isEmpty()) {
                    latestIndex = null;
                }
            }
        }
    }

    private boolean isTableIndexInUse(ICubeTableService tableIndex) {
        if (tableIndex != null) {
            //看看有没有其他人在用这个tableIndex
            int cnt = storedCount.get(tableIndex.getTableVersion(null));
            if (cnt > 0) {//如果有其他人在用
                return true;
            }
        }
        return false;
    }

    public void updateTime() {
        //userMap.updateTime(task);
        if (latestIndex != null) {
            tableIndexTime.updateTime(latestIndex.getTableVersion(null));
        }
    }

    /**
     * 根据业务包获取BITableIndex
     *
     * @param tableSource
     * @return
     */
    public ICubeTableService getTableIndex(final CubeTableSource tableSource) {
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
        BICubeLocation cubeLocation;
        try {
            cubeLocation = new BICubeLocation(BIBaseConstant.CACHE.getCacheDirectory() + BIPathUtils.tablePath(md5), File.separator + pathSuffix);
        } catch (URISyntaxException e) {
            throw BINonValueUtils.beyondControl(e);
        }
        final String path = cubeLocation.getAbsolutePath();
        ICubeConfiguration cubeConfiguration = new ICubeConfiguration() {
            @Override
            public URI getRootURI() {
                return URI.create(path);
            }
        };
        BICubeResourceRetrieval resourceRetrieval = new BICubeResourceRetrieval(cubeConfiguration);
        BICube tempCube = new BICube(resourceRetrieval, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        CubeTableSource tableSource = BIModuleUtils.getSourceByID(new BITableID(task.getTableId()), new BIUser(task.getUserId()));
        while (!tempCube.isVersionAvailable()) {
        }
        return new BICubeTableAdapter(tempCube, tableSource);

    }

    public void update() {
        synchronized (LOCK) {
            cubePath = pathQueue.poll();
            ICubeTableService tableIndex = getTableIndexByPath(cubePath);
            storedIndexes.put(tableIndex.getTableVersion(null), tableIndex);
            storedPath.put(tableIndex.getTableVersion(null), cubePath);
            tableIndexTime.put(tableIndex.getTableVersion(null), tableIndex);
            storedNIOManager.put(tableIndex.getTableVersion(null), new SingleUserNIOReadManager(biUser.getUserId()));
            this.latestIndex = tableIndex;
        }
    }


    private void release() {
        synchronized (LOCK) {
            manager.clear();
            for (Map.Entry<Long, ICubeTableService> entry : storedIndexes.entrySet()) {
                entry.getValue().clear();
            }
            for (Map.Entry<Long, SingleUserNIOReadManager> entry : storedNIOManager.entrySet()) {
                entry.getValue().clear();
            }
            for (Map.Entry<Long, String> entry : storedPath.entrySet()) {
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

    public boolean hasStoredIndexes() {
        return !storedIndexes.isEmpty();
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
            Long version = tableIndex.getTableVersion(null);
            if (storedNIOManager.get(version) != null) {
                return storedNIOManager.get(version);
            }
        }
        return manager;
    }

    @Override
    public long getVersion() {
        return 0;
    }
}