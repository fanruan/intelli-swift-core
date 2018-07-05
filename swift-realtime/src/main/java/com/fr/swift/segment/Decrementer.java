package com.fr.swift.segment;

import com.fr.swift.db.Where;
import com.fr.swift.segment.operator.delete.RowDeleter;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.util.List;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class Decrementer implements RowDeleter {

    @Override
    public boolean deleteData(List<Row> rowList) throws Exception {
        return false;
    }

    @Override
    public boolean deleteData(SwiftResultSet swiftResultSet) throws Exception {
        return false;
    }

    @Override
    public boolean delete(SourceKey sourceKey, Where where) throws Exception {
        return false;
    }
}
