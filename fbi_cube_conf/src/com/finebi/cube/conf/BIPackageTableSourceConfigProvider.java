package com.finebi.cube.conf;

import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;

import java.util.Set;

/**
 * Created by kary on 2016/6/8.
 */
public interface BIPackageTableSourceConfigProvider {

    Set<BIBusinessTable> getTable4Generate(long userId);
    Set<BusinessTable> getAllTables(long userId);
}
