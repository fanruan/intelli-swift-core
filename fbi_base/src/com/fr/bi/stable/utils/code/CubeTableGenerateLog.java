package com.fr.bi.stable.utils.code;

import com.fr.bi.stable.data.source.CubeTableSource;

/**
 * Created by wuk on 16/6/2.
 */
public class CubeTableGenerateLog {
    private long costTime;
    private CubeTableSource cubeTableSource;

    public CubeTableGenerateLog(CubeTableSource cubeTableSource) {
        this.cubeTableSource = cubeTableSource;
    }

    public long getCostTime() {
        return costTime;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }
    
}
