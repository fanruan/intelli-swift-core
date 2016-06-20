package com.finebi.cube.conf;

import java.io.File;

/**
 * Created by kary on 2016/6/20.
 * cube生成前置条件检查
 */
public interface CubePreConditionsCheck {
    /*空间*/
    boolean HDSpaceCheck(File file);

    /*连接是否可用*/
    boolean ConnectionCheck();
}
