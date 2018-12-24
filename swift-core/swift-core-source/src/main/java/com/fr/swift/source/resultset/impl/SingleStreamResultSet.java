package com.fr.swift.source.resultset.impl;

import com.fr.stable.StringUtils;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.resultset.BaseStreamResultSet;
import com.fr.swift.source.resultset.LineParser;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.IoUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-20
 */
public class SingleStreamResultSet extends BaseStreamResultSet<String> {
    private InputStream inputStream;
    private BufferedReader dataReader;
    private Row firstDataRow;
    private String currentLine;

    public SingleStreamResultSet(SwiftDatabase database, String tableName, InputStream inputStream, LineParser parser) {
        super(parser);
        try {
            parseMetaData(database, tableName, inputStream, parser);
        } catch (Exception e) {
            Crasher.crash(e);
        }
    }

    public SingleStreamResultSet(SwiftDatabase database, String tableName, String s, LineParser parser) {
        super(s, parser);
        try {
            parseMetaData(database, tableName, getInputStream(s), parser);
        } catch (Exception e) {
            Crasher.crash(e);
        }
    }

    public SingleStreamResultSet(SwiftMetaData metaData, LineParser parser, InputStream inputStream) {
        super(metaData, parser);
        try {
            initData(metaData, inputStream, parser);
        } catch (Exception e) {
            Crasher.crash(e);
        }
    }

    public SingleStreamResultSet(SwiftMetaData metaData, String s, LineParser parser) {
        super(metaData, s, parser);
        try {
            initData(metaData, getInputStream(s), parser);
        } catch (Exception e) {
            Crasher.crash(e);
        }
    }

    private void parseMetaData(SwiftDatabase database, String tableName, InputStream is, LineParser parser) throws Exception {
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
        if (StringUtils.isEmpty(readLine[0])) {
            return;
        }
        if (parser.isSkipFirstLine()) {
            readLine[1] = dataReader.readLine();
            if (StringUtils.isEmpty(readLine[1])) {
                readLine[1] = StringUtils.EMPTY;
            }
        } else {
            readLine[1] = readLine[0];
        }
        List<SwiftMetaDataColumn> columns = parser.parseColumns(readLine[0], readLine[1]);
        firstDataRow = parser.firstRow();
        initMetaData(database, tableName, columns);
    }

    private void initData(SwiftMetaData metaData, InputStream is, LineParser parser) throws IOException, SwiftMetaDataException {
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
