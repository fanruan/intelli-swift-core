package com.fr.swift.source.etl;

import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreService;

import java.util.List;

/**
 * Created by pony on 2017/12/7.
 */
public interface ETLOperator extends CoreService {
    /**
     * 返回etl后改变的列
     *
     * @param metaDatas 基础的metaDatas
     * @return
     */
    List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas);

    /**
     * 返回etl用到的基础表的列
     *
     * @param metaDatas 基础的metaDatas
     * @return
     */
    List<SwiftMetaDataColumn> getBaseColumns(SwiftMetaData[] metaDatas);

    /**
     * 获取etl的类型
     *
     * @return
     */
    OperatorType getOperatorType();

    /**
     * 获取新增列名
     *
     * @return
     */
    List<String> getNewAddedName();

}
