package com.finebi.cube.conf;

import com.fr.bi.stable.data.source.CubeTableSource;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Created by kary on 2016/6/20.
 * cube生成前置条件检查
 */
public interface CubePreConditionsCheck {
    /*空间*/
    boolean HDSpaceCheck(File file);

    /*连接是否可用*/
    Map<CubeTableSource, Boolean> ConnectionCheck(Set<CubeTableSource> cubeTableSourceSet, long userId);

}
