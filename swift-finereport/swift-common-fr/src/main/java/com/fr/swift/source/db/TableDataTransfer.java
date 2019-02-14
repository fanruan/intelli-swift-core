package com.fr.swift.source.db;

import com.fr.base.TableData;
import com.fr.general.data.DataModel;
import com.fr.script.Calculator;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftSourceTransfer;

/**
 * Created by pony on 2017/12/5.
 */
public class TableDataTransfer implements SwiftSourceTransfer {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TableDataTransfer.class);

    private TableData tableData;
    private SwiftMetaData metaData;
    private SwiftMetaData outerMetaData;

    public TableDataTransfer(TableData tableData, SwiftMetaData metaData, SwiftMetaData outerMetadata) {
        this.tableData = tableData;
        this.metaData = metaData;
        this.outerMetaData = outerMetadata;
    }

    @Override
    public SwiftResultSet createResultSet() {
        DataModel dataModel = null;
        LOGGER.info("start extracting data from tabledata");
        try {
            dataModel =tableData.createDataModel(Calculator.createCalculator());
            return new DataModelResultSet(dataModel, metaData, outerMetaData);
        } catch (Exception e) {
            if (dataModel != null) {
                try {
                    dataModel.release();
                } catch (Exception e1) {
                    LOGGER.error(e1.getMessage(), e1);
                }
            }
            LOGGER.error(e.getMessage(), e);
        }
        return EMPTY;
    }
}
