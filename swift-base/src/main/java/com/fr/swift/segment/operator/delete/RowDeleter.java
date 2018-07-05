package com.fr.swift.segment.operator.delete;

import com.fr.swift.db.Where;
import com.fr.swift.segment.operator.Deleter;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.util.List;

/**
 * @author anchore
 * @date 2018/6/19
 * <p>
 * 按明细值删
 */
public interface RowDeleter extends Deleter {
    boolean deleteData(List<Row> rowList) throws Exception;

    boolean deleteData(SwiftResultSet swiftResultSet) throws Exception;

    boolean delete(SourceKey sourceKey, Where where) throws Exception;
}