package com.fr.swift.adaptor.executor;

import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftDetailTableResult;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.adaptor.transformer.FieldFactory;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.excel.ExcelDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-3 10:01:05
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftEXCELEngineExcutor {

//    // TODO: 2018-1-3 预览加行数限制
//    public BIDetailTableResult getPreviewData(String path, String[] names, int[] types, List<String> appendedFileNames, int rowCount) throws Exception {
//        if (appendedFileNames == null) {
//            appendedFileNames = new ArrayList<String>();
//        }
//        ExcelDataSource dataSource = DataSourceFactory.transformExcelDataSource(path, names, types, appendedFileNames);
//        SwiftMetaData swiftMetaData = dataSource.getMetadata();
//        SwiftMetaData outerMetaData = dataSource.getOuterMetadata();
//        List<String> paths = new ArrayList<String>();
//        paths.add(path);
//        paths.addAll(appendedFileNames);
//        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createEXCELSourcePreviewTransfer(paths, swiftMetaData, outerMetaData);
//        SwiftResultSet swiftResultSet = transfer.createResultSet();
//        BIDetailTableResult detailTableResult = new SwiftDetailTableResult(swiftResultSet);
//        return detailTableResult;
//    }
//
//    public List<FineBusinessField> getFieldList(String path, String[] names, int[] types, List<String> appendedFileNames) throws Exception {
//        if (appendedFileNames == null) {
//            appendedFileNames = new ArrayList<String>();
//        }
//        DataSource dataSource = DataSourceFactory.transformExcelDataSource(path, names, types, appendedFileNames);
//        SwiftMetaData swiftMetaData = dataSource.getMetadata();
//        return FieldFactory.transformColumns2Fields(swiftMetaData);
//    }
}
