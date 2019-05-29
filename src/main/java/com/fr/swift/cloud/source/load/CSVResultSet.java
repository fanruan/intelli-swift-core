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
 * This class created on 2019/5/10
 *
 * @author Lucifer
 * @description
 */
public class CSVResultSet extends AbstractCloudResultSet {

    public CSVResultSet(List<File> files, LineParser parser, SwiftMetaData versionMetadata, SwiftMetaData dbMetadata) throws Exception {
        this(files, parser, versionMetadata, dbMetadata, true);
    }

    public CSVResultSet(List<File> files, LineParser parser, SwiftMetaData versionMetadata, SwiftMetaData dbMetadata, boolean skipFirstLine) throws Exception {
        super(files, parser, versionMetadata, dbMetadata, skipFirstLine);
    }

    @Override
    protected Row nextRow() {
        String line;
        try {
            if ((line = reader.readLine()) != null) {
                Map<String, Object> row = parser.parseToMap(line);
                if (row != null) {
                    Object[] rowValue = new Object[dbMetadata.getColumnCount()];
                    for (Map.Entry<String, Object> rowEntry : row.entrySet()) {
                        String columnName = rowEntry.getKey();
                        try {
                            int dbIndex = dbMetadata.getColumnIndex(columnName);
                            Object value = rowEntry.getValue();
                            rowValue[dbIndex - 1] = value;
                        } catch (SwiftMetaDataException ignore) {
                        }
                    }
                    return new ListBasedRow(rowValue);
                }
            } else {
                if (++currentFileIndex < files.size()) {
                    close();
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.files.get(currentFileIndex)), charsetList.get(currentFileIndex)));
                    if (this.skipFirstLine) {
                        reader.readLine();
                    }
                    next = nextRow();
                    if (next != null) {
                        return next;
                    }
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }

    @Override
    public CloudTableType getTableType() {
        return CloudTableType.CSV;
    }
}
