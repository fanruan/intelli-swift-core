package com.finebi.cube.conf;

import com.finebi.cube.conf.table.BIBusinessTable;

import java.util.Set;

/**
 * Created by kary on 2016/6/8.
 * 获取需要更新的业务表
 */
public interface BIPackageTableSourceConfigProvider {

    Set<BIBusinessTable> getTables4Generate(long userId);

}
