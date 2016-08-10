package com.finebi.cube.structure;

import com.finebi.cube.CubeVersion;
import com.fr.bi.common.inter.Release;

/**
 * 详细数据，主要用于列存储。
 * <p/>
 * This class created on 2016/3/3.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeDetailDataService<T> extends Release {
    /**
     * 记录数据源中的数据信息。
     *
     * @param rowNumber     数据源中的行号
     * @param originalValue 数据值
     */
    void addDetailDataValue(int rowNumber, T originalValue);


    T getOriginalObjectValueByRow(int rowNumber);

    int getClassType();

    CubeVersion getVersion();

    void recordVersion(CubeVersion version);


}
