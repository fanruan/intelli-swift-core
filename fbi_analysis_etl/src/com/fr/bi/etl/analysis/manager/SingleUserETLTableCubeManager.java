/**
 *
 */
package com.fr.bi.etl.analysis.manager;

import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.common.inter.BrokenTraversal;
import com.fr.bi.common.inter.Release;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.etl.analysis.data.UserCubeTableSource;
import com.fr.bi.etl.analysis.tableobj.ETLTableObject;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.structure.queue.FixedQueueThread;
import com.fr.bi.stable.structure.queue.ThreadUnitedQueue;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.util.BIConfigurePathUtils;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;

import java.io.File;

/**
 * @author Daniel
 */
public class SingleUserETLTableCubeManager implements Release {

    private FixedQueueThread<UserETLUpdateTask> updateTask;

    private ThreadUnitedQueue<ETLTableObject> tq = new ThreadUnitedQueue<ETLTableObject>();

    private UserCubeTableSource source;

    private volatile boolean isError = false;

    public UserCubeTableSource getSource() {
        return source;
    }


    public int getThreadPoolCubeCount() {
        return tq.size();
    }


    private String getSavedPath() {
        UserETLCubeManagerProvider manager = BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider();
        if (manager != null) {
            return manager.getCubePath(source.fetchObjectCore().getID().getIdentityValue());
        }
        return null;
    }

    public ICubeTableService getTableIndex() {
        return tq.get().getTableIndex();
    }

    public void releaseCurrentThread() {
        tq.releaseObject();
    }

    public SingleUserETLTableCubeManager(UserCubeTableSource source) {
        this.source = source;
        String path = getSavedPath();
        if (path != null) {
            File file = new File(BIConfigurePathUtils.createUserETLCubePath(source.fetchObjectCore().getIDValue(), path));
            if (file.exists()) {
                try {
                    tq.add(new ETLTableObject(source, path));
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                }
            }
            removeOtherPath(file);
        } else {
            clearAllPath(source.fetchObjectCore().getIDValue());
        }

        if (!checkVersion()) {
            addTask();
        }

    }

    private void clearAllPath(String idValue) {
        BIFileUtils.delete(new File(BIConfigurePathUtils.createUserETLTableBasePath(idValue)));
    }


    public void removeOtherPath(File file) {
        File root = file.getParentFile().getParentFile();
        if (root.exists()) {
            File[] files = root.listFiles();
            for (File f : files) {
                if (f.exists()) {
                    if (!ComparatorUtils.equals(f.getAbsolutePath(), file.getParentFile().getAbsolutePath())) {
                        BIFileUtils.delete(f);
                    }
                }
            }
        }
    }

    public boolean isAvailable() {
        boolean isEmpty = tq.isEmpty();
        if (isEmpty) {
            addTask();
        }
        return !isEmpty;
    }

    public void resetErrorStatus() {
        isError = false;
    }

    public void addTask() {
        if (isError) {
            return;
        }
        if (updateTask == null) {
            synchronized (this) {
                if (updateTask == null) {
                    updateTask = new FixedQueueThread<UserETLUpdateTask>();
                    updateTask.setCheck(new BrokenTraversal<UserETLUpdateTask>() {
                        @Override
                        public boolean actionPerformed(UserETLUpdateTask data) {
                            return checkCubePath();
                        }
                    });
                    updateTask.setTraversal(new Traversal<UserETLUpdateTask>() {

                        @Override
                        public void actionPerformed(UserETLUpdateTask data) {
                            try {
                                long version = 0;
                                if (!tq.isEmpty()) {
                                    version = tq.get().getTableIndex().getTableVersion(new IndexKey(StringUtils.EMPTY));
                                    tq.releaseObject();
                                }
                                if (data.check(version)) {
                                    return;
                                }
                                data.start();
                                data.run();
                                data.end();
                                tq.add(new ETLTableObject(source, data.getPath()));
                                UserETLCubeManagerProvider manager = BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider();
                                manager.invokeUpdate(source.fetchObjectCore().getID().getIdentityValue(), source.getUserId());
                            } catch (Throwable e) {
                                BILoggerFactory.getLogger().error(e.getMessage(), e);
                                isError = true;
                                data.rollback();
                            } finally {
                            }
                        }
                    });
                }
            }
        }
        UserETLUpdateTask task = new UserETLUpdateTask(source);
        updateTask.add(task);
    }


    private boolean checkCubePath() {
        return BIFileUtils.checkDir(new File(BIConfigurePathUtils.createUserETLTableBasePath(source.fetchObjectCore().getID().getIdentityValue())));
    }

    protected boolean checkVersion() {
        if (tq.isEmpty()) {
            return false;
        } else {
            long version = tq.get().getTableIndex().getTableVersion(new IndexKey(StringUtils.EMPTY));
            tq.releaseObject();
            return new UserETLUpdateTask(source).check(version);
        }
    }

    public void forceReleaseCurrentThread() {
        tq.releaseObject();
    }

    /**
     *
     */
    @Override
    public void clear() {
        if (updateTask != null) {
            updateTask = null;
        }
        tq.clear();
    }

    public boolean isError() {
        return isError;
    }

}