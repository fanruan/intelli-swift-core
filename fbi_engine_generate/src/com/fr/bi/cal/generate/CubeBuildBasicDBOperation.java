package com.fr.bi.cal.generate;

import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Map;
import java.util.Set;

/**
 * Created by Connery on 2015/3/31.
 */
public class CubeBuildBasicDBOperation implements CubeBuildOperation {
    private long userId;
    private AbstractCubeTask cubeTask;

    public CubeBuildBasicDBOperation(String basePath, String tmpPath, long userId) {
        this.userId = userId;
    }

    @Override
    public Object getData() {
        return cubeTask.getGenerateTables();
    }

    @Override
    public Object process(Object tableKeys) {
        cubeTask.loadIndex((Map<Integer, Set<CubeTableSource>>) tableKeys);
        return new Object();
    }
}