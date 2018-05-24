package com.fr.swift.source;

/**
 * Created by Handsome on 2017/12/23 0023 15:07
 */
public interface SwiftMetaDataColumn {
    String getName();

    String getId();

    String getRemark();

    int getType();

    /**
     * @return 长度
     */
    int getPrecision();

    /**
     * @return 小数位数
     */
    int getScale();
}