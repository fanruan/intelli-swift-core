package com.finebi.cube.conf;

import com.finebi.cube.conf.table.BIBusinessTable;

import java.util.Set;

/**
 * Created by 49597 on 2016/6/8.
 */
public interface BIPackageTableSourceConfigProvider {

    Set<BIBusinessTable> getTableSources4Genrate(long userId);

}
