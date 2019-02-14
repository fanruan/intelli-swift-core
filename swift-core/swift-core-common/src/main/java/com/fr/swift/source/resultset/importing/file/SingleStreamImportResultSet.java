package com.fr.swift.source.resultset.importing.file;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.IoUtil;
import com.fr.swift.util.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO 还没实现
 * @author yee
 * @date 2018-12-10
 */
public class SingleStreamImportResultSet extends BaseStreamImportResultSet<String> {
    private InputStream inputStream;
    private BufferedReader dataReader;
    private Row firstDataRow;
    private String currentLine;

    public SingleStreamImportResultSet(SwiftDatabase database, String tableName, InputStream inputStream, FileLineParser parser) {
        super(parser);
        try {
            parseMetaData(database, tableName, inputStream, parser);
        } catch (Exception e) {
            Crasher.crash(e);
        }
    }

    public SingleStreamImportResultSet(SwiftDatabase database, String tableName, String s, FileLineParser parser) {
        super(s, parser);
        try {
            parseMetaData(database, tableName, getInputStream(s), parser);
        } catch (Exception e) {
            Crasher.crash(e);
        }
    }

    public SingleStreamImportResultSet(SwiftMetaData metaData, FileLineParser parser, InputStream inputStream) {
        super(metaData, parser);
        try {
            initData(metaData, inputStream, parser);
        } catch (Exception e) {
            Crasher.crash(e);
        }
    }

    public SingleStreamImportResultSet(SwiftMetaData metaData, String s, FileLineParser parser) {
        super(metaData, s, parser);
        try {
            initData(metaData, getInputStream(s), parser);
        } catch (Exception e) {
            Crasher.crash(e);
        }
    }

    private void parseMetaData(SwiftDatabase database, String tableName, InputStream is, FileLineParser parser) throws Exception {
        SwiftMetaData metaData = SwiftContext.get().getBean(SwiftMetaDataService.class).getMetaDataByKey(tableName);
        if (metaData.getSwiftDatabase().equals(database)) {
            this.metaData = metaData;
            initData(metaData, is, parser);
            return;
        }
        inputStream = is;
        dataReader = new BufferedReader(new InputStreamReader(inputStream));
        String[] readLine = new String[2];
        readLine[0] = dataReader.readLine();
        if (Strings.isEmpty(readLine[0])) {
            return;
        }
        if (parser.isSkipFirstLine()) {
            readLine[1] = dataReader.readLine();
            if (Strings.isEmpty(readLine[1])) {
                readLine[1] = Strings.EMPTY;
            }
        } else {
            readLine[1] = readLine[0];
        }
        List<SwiftMetaDataColumn> columns = parser.parseColumns(readLine[0], readLine[1]);
        firstDataRow = parser.firstRow();
        initMetaData(database, tableName, columns);
    }

    private void initData(SwiftMetaData metaData, InputStream is, FileLineParser parser) throws IOException, SwiftMetaDataException {
        inputStream = is;
        dataReader = new BufferedReader(new InputStreamReader(inputStream));
        if (parser.isSkipFirstLine()) {
            // 跳过第一行head
            dataReader.readLine();
        }
        List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            columns.add(metaData.getColumn(i + 1));
        }
        parser.setColumns(columns);

    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public boolean hasNext() throws SQLException {
        try {
            return null != firstDataRow || null != (currentLine = dataReader.readLine());
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Row getNextRow() throws SQLException {
        if (null != firstDataRow) {
            Row row = firstDataRow;
            firstDataRow = null;
            return row;
        }
        return parser.parseLine(currentLine);
    }

    @Override
    public void close() throws SQLException {
        IoUtil.close(inputStream);
        IoUtil.close(dataReader);
    }
}
