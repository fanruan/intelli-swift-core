package com.fr.bi.stable.data.key;

import com.fr.json.JSONTransform;

import java.io.Serializable;

/**
 * 列信息接口，用于描述从数据库中取出的列特征
 */
public interface IPersistentField extends JSONTransform,Serializable {
    /**
     * 字段长度
     * @return 字段长度
     */
    int getColumnSize();

    /**
     * 字段类型
     * @return 字段类型
     */
    int getSqlType();

    /**
     * 字段名字
     * @return 字段名字
     */
    String getFieldName();

    /**
     * 该字段是否被使用
     * @return 如果在被使用则返回true，否则返回false
     */
    boolean isUsable();

    /**
     * 小数位数
     * @return 小数位数
     */
    int getScale();
}