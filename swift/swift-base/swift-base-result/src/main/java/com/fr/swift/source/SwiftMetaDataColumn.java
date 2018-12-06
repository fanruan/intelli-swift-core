package com.fr.swift.source;

/**
 * Created by Handsome on 2017/12/23 0023 15:07
 */
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = MetaDataColumnBean.class)
//@JsonSubTypes(
//        @JsonSubTypes.Type(MetaDataColumnBean.class)
//)
public interface SwiftMetaDataColumn {
    String getName();

    String getRemark();

    String getColumnId();

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