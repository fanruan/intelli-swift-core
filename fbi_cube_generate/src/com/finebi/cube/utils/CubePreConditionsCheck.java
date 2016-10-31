package com.finebi.cube.utils;

import com.finebi.cube.structure.Cube;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.data.impl.JDBCDatabaseConnection;

import java.io.File;

/**
 * Created by kary on 2016/6/20.
 * cube生成前置条件检查
 */
public interface CubePreConditionsCheck {
    /**
     * 空间
     * @param file
     * @return
     */
    boolean HDSpaceCheck(File file);

    /***
     * SQL是否正常
     * @param cube
     * @param source
     * @return
     */
    boolean SQLCheck(Cube cube, CubeTableSource source);

    /**
     * 连接是否可用
     *
     * @param jdbcDatabaseConnection
     * @return
     */
    boolean ConnectionCheck(JDBCDatabaseConnection jdbcDatabaseConnection);
}
