package com.fr.swift.source.excel;

import com.fr.general.data.DataModel;
import com.fr.general.data.TableDataException;
import com.fr.swift.exception.meta.SwiftDataModelException;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.db.AbstractDataModelResultSet;
import com.fr.swift.util.Util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/6.
 */
public class DataModelsResultSet extends AbstractDataModelResultSet {
    private List<DataModel> dataModels;
    private DataModel currentModel;
    private int currentDataModelIndex = 0;
    private int index = -1;

    public DataModelsResultSet(List<DataModel> dataModels, SwiftMetaData metaData, SwiftMetaData outerMetadata) {
        super(metaData, outerMetadata);
        Util.requireNonNull(dataModels, metaData);
        this.dataModels = dataModels;
        this.metaData = metaData;
        if (!dataModels.isEmpty()) {
            currentModel = dataModels.get(currentDataModelIndex);
        }
    }

    @Override
    public void close() throws SQLException {
        try {
            for (DataModel dataModel : dataModels) {
                dataModel.release();
            }
        } catch (Exception e) {
            throw new SwiftDataModelException(e);
        }
    }

    @Override
    public boolean hasNext() throws SQLException {
        try {
            return moveNext();
        } catch (TableDataException e) {
            throw new SwiftDataModelException(e);
        }
    }

    private boolean moveNext() throws TableDataException {
        while (currentModel != null && !currentModel.hasRow(++index)) {
            if (dataModels.size() > ++currentDataModelIndex) {
                currentModel = dataModels.get(currentDataModelIndex);
                index = -1;
            } else {
                currentModel = null;
            }
        }
        return currentModel != null;
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
                list.add(getValue(currentModel.getValueAt(index, columnIndexes[i]), columnTypes[i]));
            }
            return new ListBasedRow(list);
        } catch (Exception e) {
            throw new SwiftDataModelException(e);
        }
    }

}
