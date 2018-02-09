package com.fr.swift.adaptor.executor;

/**
 * This class created on 2018-1-3 10:01:05
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftExcelEngineExcutor {

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
