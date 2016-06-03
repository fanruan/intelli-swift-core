package com.finebi.cube.structure.column;

import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.ICubeRelationEntityGetterService;
import com.finebi.cube.structure.ICubeIndexDataGetterService;
import com.finebi.cube.structure.ICubeVersion;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * Cube中列的可读接口
 * This class created on 2016/3/2.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeColumnReaderService<T> extends ICubeIndexDataGetterService,ICubeVersion, Release {

    /**
     * 获得分组值对应的位置。
     *
     * @param groupValues
     * @return 分组值的位置
     */
    int getPositionOfGroup(T groupValues) throws BIResourceInvalidException;

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
     * 根据数据库中的行号来获得相应的索引值
     *
     * @param rowNumber 行号
     * @return 索引
     */
    GroupValueIndex getIndexByRow(int rowNumber) throws BIResourceInvalidException, BICubeIndexException;


    GroupValueIndex getIndexByGroupValue(T groupValues) throws BIResourceInvalidException, BICubeIndexException;

    T getGroupValue(int position);

    boolean existRelationPath(BICubeTablePath path);

    ICubeRelationEntityGetterService getRelationIndexGetter(BICubeTablePath path) throws BICubeRelationAbsentException, IllegalRelationPathException;

    int getClassType();
}
