package com.finebi.cube.structure.column;

import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.CubeRelationEntityGetterService;
import com.finebi.cube.structure.ICubeIndexDataGetterService;
import com.finebi.cube.structure.ICubeVersion;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.structure.object.CubeValueEntry;

/**
 * Cube中列的可读接口
 * This class created on 2016/3/2.
 *
 * @author Connery
 * @since 4.0
 */
public interface CubeColumnReaderService<T> extends ICubeIndexDataGetterService,ICubeVersion, Release {

    /**
     * 获得分组值对应的位置。
     *
     * @param groupValues
     * @return 分组值的位置
     */
    int getPositionOfGroupByGroupValue(T groupValues) throws BIResourceInvalidException;

    /**
     * 获得原始行号对应的分组位置。
     *
     * @param row
     * @return 分组值的位置
     */
    Integer getPositionOfGroupByRow(int row) throws BIResourceInvalidException;

    /**
     * 分组数量
     *
     * @return 分组数量
     */
    int sizeOfGroup();

    /**
     * 根据行号获得对应的原始值。
     *
     * @param rowNumber 数据库中的行号
     * @return 原始值
     */
    T getOriginalValueByRow(int rowNumber);

    /**
     * 根据分组值来获得相应的索引值
     * @param groupValues 分组值
     * @return 索引值
     * @throws BIResourceInvalidException 资源不可以
     * @throws BICubeIndexException 索引异常
     */
    GroupValueIndex getIndexByGroupValue(T groupValues) throws BIResourceInvalidException, BICubeIndexException;

    T getGroupValue(int position);

    boolean existRelationPath(BICubeTablePath path);

    /**
     * 获取字段关联
     * @param path 关联路径
     * @return 字段关联
     * @throws BICubeRelationAbsentException 关联缺失
     * @throws IllegalRelationPathException 路径不合法
     */
    CubeRelationEntityGetterService getRelationIndexGetter(BICubeTablePath path) throws BICubeRelationAbsentException, IllegalRelationPathException;

    int getClassType();
}
