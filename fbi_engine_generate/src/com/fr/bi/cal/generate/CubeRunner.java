package com.fr.bi.cal.generate;

import com.finebi.cube.api.UserAnalysisCubeDataLoaderCreator;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BISystemConfigHelper;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.impl.conf.CubeBuildStuffComplete;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.utils.CubeUpdateUtils;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.stable.loader.CubeReadingTableIndexLoader;
import com.fr.bi.common.inter.BrokenTraversal;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.conf.records.BICubeTaskRecord;
import com.fr.bi.stable.constant.Status;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.structure.queue.QueueThread;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.time.BIDateUtils;
import com.fr.bi.util.BIConfigurePathUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import org.slf4j.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by GUY on 2015/3/16.
 */
public class CubeRunner {

    /**
     *
     */
    private static final long serialVersionUID = -249303913165576913L;

    protected volatile Status statue = Status.NULL;
    protected BIUser biUser;
    QueueThread<CubeTask> cubeThread = new QueueThread<CubeTask>();
    private CubeBuildStuffComplete object;
    private static Set<String> cubeGeneratingTableSourceIDs;
    private static final Logger LOGGER = BILoggerFactory.getLogger(CubeRunner.class);
    private CubeTask updatingCubeTask = null;

    public CubeRunner(long userId) {
        biUser = new BIUser(userId);
        init();
    }

    public void envChanged() {
        synchronized (cubeThread) {
            cubeThread.clear();
        }
    }

    private void init() {
        //设置回调函数
        cubeThread.setTraversal(new Traversal<CubeTask>() {
            @Override
            public void actionPerformed(CubeTask cubeTask) {
                updatingCubeTask = cubeTask;
                cubeGeneratingTableSourceIDs = cubeTask.getTaskTableSourceIds();
                long start = System.currentTimeMillis();
                setStatue(Status.WAITING);
                start();
                try {
                    setStatue(Status.START);
                    cubeTask.start();
                    setStatue(Status.LOADING);
                    cubeTask.run();
                    setStatue(Status.LOADED);
                    setStatue(Status.REPLACING);
                    cubeTask.end();
                    setStatue(Status.END);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    setStatue(Status.WRONG);
                } finally {
                    finish(cubeTask);
                    setStatue(Status.NULL);
                    LOGGER.info(BIDateUtils.getCurrentDateTime() + " Build OLAP database Cost:" + DateUtils.timeCostFrom(start));
                    cubeGeneratingTableSourceIDs.clear();
                    BIConfigureManagerCenter.getCubeConfManager().updatePackageLastModify();
                    updatingCubeTask = null;
                }
            }
        });
        //设置检查任务
        cubeThread.setCheck(new BrokenTraversal<CubeTask>() {
            @Override
            public boolean actionPerformed(CubeTask obj) {
                return checkCubePath();
            }
        });
        //执行线程队列
        cubeThread.start();
    }

    public boolean hasTask(CubeTask t) {
        Iterator<CubeTask> iterator = cubeThread.iterator();
        while (iterator.hasNext()) {
            CubeTask task = iterator.next();
            if (ComparatorUtils.equals(task.getTaskId(), t.getTaskId())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTask() {
        return !cubeThread.isEmpty();
    }


    public boolean hasWaitingCheckTask() {
        Iterator<CubeTask> iter = cubeThread.iterator();
        while (iter.hasNext()) {
            CubeTask ct = iter.next();
            if (ct.getTaskType() != CubeTaskType.SINGLE) {
                return true;
            }
        }
        return false;
    }

    public void addTask(CubeTask task) {
        cubeThread.add(task);
        setStatue(Status.PREPARED);
    }

    public void removeTask(String taskId) {
        Iterator<CubeTask> it = cubeThread.iterator();
        while (it.hasNext()) {
            CubeTask task = it.next();
            if (ComparatorUtils.equals(task.getTaskId(), taskId)) {
                it.remove();
            }
        }
    }

    public Iterator<CubeTask> getWaitingList() {
        return cubeThread.iterator();
    }

    private void start() {
        BICubeConfigureCenter.getPackageManager().startBuildingCube(biUser.getUserId());
        BIConfigureManagerCenter.getLogManager().clearLog(biUser.getUserId());
        BIConfigureManagerCenter.getLogManager().logStart(biUser.getUserId());
    }

    private void finish(final CubeTask cubeTask) {
        long t = System.currentTimeMillis();
        try {
            if (!cubeTask.getTaskType().equals(CubeTaskType.INSTANT)) {
                LOGGER.info("start to persist meta data!");
                BICubeConfigureCenter.getTableRelationManager().persistData(biUser.getUserId());
                BICubeConfigureCenter.getPackageManager().persistData(biUser.getUserId());
                BICubeConfigureCenter.getDataSourceManager().persistData(biUser.getUserId());
            }
            LOGGER.info("meta data finished! time cost: " + DateUtils.timeCostFrom(t));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            BICubeConfigureCenter.getPackageManager().endBuildingCube(biUser.getUserId(), CubeUpdateUtils.getBusinessCubeAbsentTables(biUser.getUserId()));
        }
        UserAnalysisCubeDataLoaderCreator.getInstance().fetchCubeLoader(biUser.getUserId()).clear();
        /* 前台进度条完成进度最多到90%，当cube文件替换完成后传入调用logEnd，进度条直接到100%*/

        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    //持久化log
                    recordLogs(cubeTask, BIConfigureManagerCenter.getLogManager());
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                //生成完cube预读一次
                for (BusinessTable table : BICubeConfigureCenter.getPackageManager().getAnalysisAllTables(biUser.getUserId())) {
                    CubeReadingTableIndexLoader.getInstance(biUser.getUserId()).getTableIndex(table.getTableSource());
                }
            }
        });
        BIConfigureManagerCenter.getLogManager().logEnd(biUser.getUserId());
    }


    public CubeBuildStuffComplete getCubeGeneratingObjects() {
        if (object == null) {
            BISystemConfigHelper configHelper = new BISystemConfigHelper();
            Set<CubeTableSource> allTableSources = configHelper.extractTableSource(configHelper.getSystemBusinessTables());
            Set<BITableSourceRelation> allRelations = configHelper.convertRelations(configHelper.getSystemTableRelations());
            Set<BITableSourceRelationPath> allPaths = configHelper.convertPaths(configHelper.getSystemTablePaths());
            object = new CubeBuildStuffComplete(biUser, allTableSources, allRelations, allPaths);
        }
        return object;
    }

    public Status getStatue() {
        return statue;
    }

    public void setStatue(Status statue) {
        LOGGER.info("previous FineIndex status :" + getStatue());
        LOGGER.info("change FineIndex status to :" + statue.name());
        this.statue = statue;
    }

    public CubeTask getGeneratingTask() {
        return cubeThread.getGenerating();
    }

    public CubeTask getGeneratedTask() {
        return cubeThread.getGenerated();
    }

    /**
     * 检查cube路径
     *
     * @return true或false
     */
    private boolean checkCubePath() {
        return BIFileUtils.checkDir(new File(BIConfigurePathUtils.createBasePath()));
    }

    private void recordLogs(CubeTask cubeTask, BILogManagerProvider logManager) {
        LOGGER.info("start persist FineIndex task logs……");
        BICubeTaskRecord record = new BICubeTaskRecord(cubeTask.getTaskType(), logManager.getStart(biUser.getUserId()), logManager.getEndTime(biUser.getUserId()), getStatue());
        record.setErrorTableLogs(logManager.getErrorTables(biUser.getUserId()));
        Set<BITableSourceRelationPath> allRelationPathSet = logManager.getAllRelationPathSet(biUser.getUserId());
        record.setAllRelationPaths(allRelationPathSet);
        record.setErrorPathLogs(logManager.getErrorPaths(biUser.getUserId()));
        Set<CubeTableSource> allTableSourceSet = logManager.getAllTableSourceSet(biUser.getUserId());
        record.setAllSingleSourceLayers(allTableSourceSet);
        BIConfigureManagerCenter.getCubeTaskRecordManager().saveCubeTaskRecord(biUser.getUserId(), record);
        new Thread() {
            @Override
            public void run() {
                BIConfigureManagerCenter.getCubeTaskRecordManager().persistData(biUser.getUserId());
            }
        }.start();

    }

    public Set<String> getCubeGeneratingTableSourceIds() {
        if (cubeGeneratingTableSourceIDs != null) {
            return cubeGeneratingTableSourceIDs;
        } else {
            return new HashSet<String>();
        }
    }

    public Set<String> getCubeWaiting2GenerateTableSourceIds() {
        Set<String> tableSourceIdsSet = new HashSet<String>();
        Iterator<CubeTask> taskIterator = cubeThread.iterator();
        int i = 0;
        while (taskIterator.hasNext()) {
            CubeTask task = taskIterator.next();
            if (i != 0) {
                tableSourceIdsSet.addAll(task.getTaskTableSourceIds());
            }
            i++;
        }
        return tableSourceIdsSet;
    }

    public CubeTask getUpdatingTask() {
        return updatingCubeTask;
    }
}
