package com.fr.bi.conf.report.filter.target;


import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.report.result.BITargetKey;

public interface BIKeyTarget {

    public BITargetKey createSummaryKey(ICubeDataLoader loader);

}