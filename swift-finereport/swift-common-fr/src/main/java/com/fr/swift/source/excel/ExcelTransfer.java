package com.fr.swift.source.excel;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.db.DataModelResultSet;
import com.fr.swift.source.excel.data.IExcelDataModel;
import com.fr.swift.source.resultset.CoSwiftResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pony
 * @date 2017/12/5
 */
public class ExcelTransfer implements SwiftSourceTransfer {
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
        List<SwiftResultSet> resultSets = new ArrayList<SwiftResultSet>();
        SwiftLoggers.getLogger().info("start extracting data from table data {}", fileNames);
        try {
            for (String fileName : fileNames) {
                IExcelDataModel tableData = new ExcelTableData(fileName).createDataModel();
                resultSets.add(new DataModelResultSet(tableData, metaData, outerMetadata));
            }
            return new CoSwiftResultSet(resultSets);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            for (SwiftResultSet resultSet : resultSets) {
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                    SwiftLoggers.getLogger().error(e1);
                }
            }
        }
        return EMPTY;
    }
}