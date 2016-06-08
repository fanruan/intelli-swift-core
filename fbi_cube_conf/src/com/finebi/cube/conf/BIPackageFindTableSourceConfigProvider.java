package com.finebi.cube.conf;

import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Set;

/**
 * Created by 49597 on 2016/6/8.
 */
public interface BIPackageFindTableSourceConfigProvider {

    Set<CubeTableSource> getPackages4Genrate(long userId);

}
