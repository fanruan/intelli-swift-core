package com.fr.swift.cloud.source.load;

import com.fr.swift.cloud.source.CloudTableType;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/4/26
 *
 * @author Lucifer
 * @description
 */
public class GCFileResultSet extends AbstractCloudResultSet {

    public GCFileResultSet(List<File> files, LineParser parser, SwiftMetaData versionMetadata, SwiftMetaData dbMetadata) throws Exception {
        super(files, parser, versionMetadata, dbMetadata);
    }

    @Override
    protected Row nextRow() {
        String line;
        try {
            StringBuilder currentBuilder = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains("minor GC") || line.contains("major GC")) {
                    currentBuilder = new StringBuilder();
                }
                if (currentBuilder == null) {
                    continue;
                }
                currentBuilder.append(line);
                if (line.contains("duration")) {
                    Map<String, Object> result = this.parser.parseToMap(currentBuilder.toString());
                    Row row = null;
                    if (result != null) {
                        Object[] rowValue = new Object[dbMetadata.getColumnCount()];
                        for (Map.Entry<String, Object> rowEntry : result.entrySet()) {
                            String columnName = rowEntry.getKey();
                            try {
                                int dbIndex = dbMetadata.getColumnIndex(columnName);
                                Object value = rowEntry.getValue();
                                rowValue[dbIndex - 1] = value;
                            } catch (SwiftMetaDataException ignore) {
                            }
                        }
                        row = new ListBasedRow(rowValue);
                    }
                    return row;
                }
            }
            if (++currentFileIndex < files.size()) {
                close();
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.files.get(currentFileIndex)), charsetList.get(currentFileIndex)));
                return nextRow();
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }

    @Override
    public CloudTableType getTableType() {
        return CloudTableType.GC;
    }

//    public static void main(String[] args) throws Exception {
//        File[] files = new File[]{
//                new File("D:\\swift-new\\analyseSourceData\\treas201905\\fanruan.gc.log.2019-05-16")
//                , new File("D:\\swift-new\\analyseSourceData\\treas201905\\fanruan.gc.log.2019-05-15")
//                , new File("D:\\swift-new\\analyseSourceData\\treas201905\\fanruan.gc.log.2019-05-14")};
//        Map<String, SwiftMetaData> map = CloudVersionProperty.getProperty().getMetadataMapByVersion("2.0");
//        SwiftMetaData swiftMetaData = map.get("fanruan.gc.log");
//
//        List<SwiftMetaDataColumn> selfBaseFields = new ArrayList<SwiftMetaDataColumn>();
//        for (SwiftMetaDataColumn field : ((SwiftMetaDataBean) swiftMetaData).getFields()) {
//            if (field.getName().equals("appId") || field.getName().equals("yearMonth")) {
//                continue;
//            }
//            selfBaseFields.add(field);
//        }
//        GCFileResultSet resultSet = new GCFileResultSet(Arrays.asList(files), new GeneralLineParser("gc_record", "a", "201905", new RawGCParser(selfBaseFields), LineAdapter.DUMMY), swiftMetaData, swiftMetaData);
//        while (resultSet.hasNext()) {
//            Row row = resultSet.getNextRow();
//            System.out.println(row);
//        }
//    }
}
