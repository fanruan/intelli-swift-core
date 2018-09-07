package com.fr.swift.source.db;

import com.fr.general.data.DataModel;
import com.fr.general.data.TableDataException;
import com.fr.swift.exception.meta.SwiftDataModelException;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/6.
 */
public class DataModelResultSet extends AbstractDataModelResultSet {
    private DataModel dataModel;
    private int index = 0;

    public DataModelResultSet(DataModel dataModel, SwiftMetaData metaData, SwiftMetaData outerMetaData) {
        super(metaData, outerMetaData);
        this.dataModel = dataModel;
    }

    @Override
    public void close() throws SQLException {
        try {
            dataModel.release();
        } catch (Exception e) {
            throw new SwiftDataModelException(e);
        }
    }

    @Override
    public boolean hasNext() throws SQLException {
        try {
            return dataModel.hasRow(index);
        } catch (TableDataException e) {
            throw new SwiftDataModelException(e);
        }
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getNextRow() throws SQLException {
        try {
            List list = new ArrayList();
            for (int i = 0; i < columnIndexes.length; i++) {
                list.add(getValue(dataModel.getValueAt(index, columnIndexes[i]), columnTypes[i]));
            }
            index++;
            return new ListBasedRow(list);
        } catch (Exception e) {
            throw new SwiftDataModelException(e);
        }
    }
}