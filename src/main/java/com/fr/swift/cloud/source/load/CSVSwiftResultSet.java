package com.fr.swift.cloud.source.load;

import com.fr.swift.cloud.source.CloudTableType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by lyon on 2019/3/7.
 */
@Deprecated
public class CSVSwiftResultSet implements CloudResultSet {

    private LineParser parser;
    private SwiftMetaData metaData;

    private BufferedReader reader;
    private Row next;

    public CSVSwiftResultSet(String file, LineParser parser, SwiftMetaData metaData) throws Exception {
        this(file, parser, metaData, true);
    }

    public CSVSwiftResultSet(String file, LineParser parser, SwiftMetaData metaData, boolean skipFirstLine) throws Exception {
        this.parser = parser;
        this.metaData = metaData;
//        String charsetName = ParseUtils.getFileEncode(file);
        String charsetName = null;
        charsetName = charsetName == null ? "utf8" : charsetName;
        SwiftLoggers.getLogger().info("file: {}, charset: {}", file, charsetName);
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
        if (skipFirstLine) {
            reader.readLine();
        }
        next = nextRow();
    }

    private Row nextRow() {
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                Map row = parser.parseToMap(line);
                if (row != null) {
                    return new ListBasedRow(new ArrayList(row.values()));
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
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
    public boolean hasNext() throws SQLException {
        return next != null;
    }

    @Override
    public Row getNextRow() throws SQLException {
        Row ret = next;
        next = nextRow();
        return ret;
    }

    @Override
    public void close() throws SQLException {
        try {
            reader.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public CloudTableType getTableType() {
        return CloudTableType.CSV;
    }
}
