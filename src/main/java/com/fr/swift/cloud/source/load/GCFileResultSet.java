package com.fr.swift.cloud.source.load;

import com.fr.swift.annotation.Compatible;
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
    @Compatible
    protected Row nextRow() {
        String line;
        try {
            StringBuilder currentBuilder = null;
            while ((line = reader.readLine()) != null) {
                //新gc格式
                if (line.contains("timestamp") && line.contains("Times")) {
                    Map<String, Object> result = this.parser.parseToMap(line);
                    return parseRow(result);
                }

                //兼容旧gc格式
                if (line.contains("minor GC") || line.contains("major GC")) {
                    currentBuilder = new StringBuilder();
                }
                if (currentBuilder == null) {
                    continue;
                }
                currentBuilder.append(line);
                if (line.contains("duration")) {
                    Map<String, Object> result = this.parser.parseToMap(currentBuilder.toString());
                    return parseRow(result);
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

    private Row parseRow(Map<String, Object> rowMap) throws SwiftMetaDataException {
        Row row = null;
        if (rowMap != null) {
            Object[] rowValue = new Object[dbMetadata.getColumnCount()];
            for (Map.Entry<String, Object> rowEntry : rowMap.entrySet()) {
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
