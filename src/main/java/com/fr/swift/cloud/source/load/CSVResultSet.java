package com.fr.swift.cloud.source.load;

import com.fr.swift.cloud.source.CloudTableType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/5/10
 *
 * @author Lucifer
 * @description
 */
public class CSVResultSet implements CloudResultSet {

    private LineParser parser;
    private SwiftMetaData metaData;

    private BufferedReader reader;
    private List<File> files;
    private int currentFileIndex = 0;
    private Row next;
    private boolean skipFirstLine;
    String charsetName;

    public CSVResultSet(List<File> files, LineParser parser, SwiftMetaData metaData) throws Exception {
        this(files, parser, metaData, true);
    }

    public CSVResultSet(List<File> files, LineParser parser, SwiftMetaData metaData, boolean skipFirstLine) throws Exception {
        this.parser = parser;
        this.metaData = metaData;
        charsetName = charsetName == null ? "utf8" : charsetName;
        SwiftLoggers.getLogger().info("file: {}, charset: {}", files.toString(), this.charsetName);
        this.files = files;
        this.skipFirstLine = skipFirstLine;

        reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.files.get(currentFileIndex)), this.charsetName));
        if (this.skipFirstLine) {
            reader.readLine();
        }
        next = nextRow();
    }

    private Row nextRow() {
        String line;
        try {
            if ((line = reader.readLine()) != null) {
                Map row = parser.parseToMap(line);
                if (row != null) {
                    return new ListBasedRow(new ArrayList(row.values()));
                }
            } else {
                if (++currentFileIndex < files.size()) {
                    close();
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.files.get(currentFileIndex)), this.charsetName));
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
        // TODO: 2019/5/10 by lucifer 兼容字段增加
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
