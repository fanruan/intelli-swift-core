package com.fr.swift.source;

import com.fr.swift.config.bean.MetaDataColumnBean;
import com.fr.third.fasterxml.jackson.annotation.JsonSubTypes;
import com.fr.third.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by Handsome on 2017/12/23 0023 15:07
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = MetaDataColumnBean.class)
@JsonSubTypes(
        @JsonSubTypes.Type(MetaDataColumnBean.class)
)
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

    String getColumnId();
}

