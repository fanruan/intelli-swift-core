package com.fr.bi.cal.stable.index;


import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.conf.log.BIRecord;
import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.CubeBaseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by GUY on 2015/3/11.
 */
public class GroupIndexGenerator extends AbstractSourceGenerator {

    private static final int MUITI_THREAD_LIMIT_ROW = 1 << 19;

    private ICubeDataLoader loader;

    public GroupIndexGenerator(TableCubeFile cube, CubeTableSource dataSource, ICubeDataLoader loader, BIRecord log) {
        super(cube, dataSource, log);
        this.loader = loader;
    }

    @Override
    public void generateCube() {
        if (!dataSource.needGenerateIndex()) {
            return;
        }
        BILoggerFactory.getLogger().info("table: " + dataSource.toString() + "start generate index:");
        final long rowCount = cube.getRowCount();
        int threadCount = Math.min(MUITI_THREAD_LIMIT_ROW * CubeBaseUtils.AVAILABLE_PROCESSORS / (int) (rowCount + 1) + 1, CubeBaseUtils.AVAILABLE_PROCESSORS);
        GroupIndexCreator[] gics = cube.createGroupValueIndexCreator();
        if (threadCount > 1) {
            multiThreadGenerate(threadCount, gics);
        } else {
            for (int i = 0, len = gics.length; i < len; i++) {
//                gics[i].setLog(log, dataSource.getDbTable());
                gics[i].generateCube();
                BILoggerFactory.getLogger().info("table: " + dataSource.toString() + "finish:" + Math.round(i / len * 100) + "%");
            }
        }
        cube.releaseGroupValueIndexCreator();
        BILoggerFactory.getLogger().info("table: " + dataSource.toString() + "indexing completed");
    }

    private void multiThreadGenerate(int threadCount, final GroupIndexCreator[] gics) {
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        List<Callable<Object>> threadList = new ArrayList<Callable<Object>>();
        for (int i = 0, len = gics.length; i < len; i++) {
            final int index = i;
            threadList.add(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    gics[index].setLog(log, dataSource.getPersistentTable());
                    gics[index].generateCube();
                    return null;
                }
            });
        }
        try {
            es.invokeAll(threadList);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        } finally {
            es.shutdown();
        }
        BILoggerFactory.getLogger().info("table: " + dataSource.toString() + "finish:" + CubeConstant.PERCENT_ROW_D + "%");
    }


}