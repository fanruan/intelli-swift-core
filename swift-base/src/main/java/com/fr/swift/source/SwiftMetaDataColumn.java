package com.fr.swift.source;

import com.fr.swift.config.pojo.MetaDataColumnPojo;

/**
 * Created by Handsome on 2017/12/23 0023 15:07
 */
public interface SwiftMetaDataColumn {

    /**
     * @return 长度
     */
    int getPrecision();

    int getType();

    String getName();

    String getRemark();

    /**
     * @return 小数位数
     */
    int getScale();

    MetaDataColumnPojo getMetaDataColumnPojo();

    String getColumnId();
}

