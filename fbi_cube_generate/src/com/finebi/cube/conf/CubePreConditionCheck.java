package com.finebi.cube.conf;

/**
 * Created by kary on 2016/6/20.
 * cube生成前置条件检查
 */
public interface CubePreConditionCheck {
    /*空间*/
    boolean HDSpaceCheck();
    /*连接是否可用*/
    boolean ConnectionCheck();
}
