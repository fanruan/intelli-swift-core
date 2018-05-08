package com.fr.swift.source.excel;

import com.fr.general.data.DataModel;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/5.
 */
public class ExcelTransfer implements SwiftSourceTransfer {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ExcelTransfer.class);

    private List<String> fileNames;
    private SwiftMetaData metaData;
    private SwiftMetaData outerMetadata;

    public ExcelTransfer(List<String> fileNames, SwiftMetaData metaData, SwiftMetaData outerMetadata) {
        this.metaData = metaData;
        this.fileNames = fileNames;
        this.outerMetadata = outerMetadata;
    }

    @Override
    public SwiftResultSet createResultSet() {
        List<DataModel> dataModelList = new ArrayList<DataModel>();
        LOGGER.info("start extracting data from tabledata");
        try {
            for (String fileName : fileNames) {
                DataModel tableData = new ExcelTableData(fileName).createDataModel();
                dataModelList.add(tableData);
            }
            return new DataModelsResultSet(dataModelList, metaData, outerMetadata);
        } catch (Exception e) {
            if (dataModelList != null) {
                try {
                    for (DataModel dataModel : dataModelList) {
                        dataModel.release();
                    }
                } catch (Exception e1) {
                    LOGGER.error(e1.getMessage(), e1);
                }
            }
            LOGGER.error(e.getMessage(), e);
        }
        return EMPTY;
    }
}
