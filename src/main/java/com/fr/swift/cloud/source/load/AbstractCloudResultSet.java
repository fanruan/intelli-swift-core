package com.fr.swift.cloud.source.load;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2019/5/20
 *
 * @author Lucifer
 * @description
 */
public abstract class AbstractCloudResultSet implements CloudResultSet {

    protected LineParser parser;
    protected SwiftMetaData versionMetadata;
    protected SwiftMetaData dbMetadata;

    protected BufferedReader reader;
    protected List<File> files;
    protected List<String> charsetList;
    protected int currentFileIndex = 0;
    protected Row next;
    protected boolean skipFirstLine;

    public AbstractCloudResultSet(List<File> files, LineParser parser, SwiftMetaData versionMetadata, SwiftMetaData dbMetadata) throws Exception {
        this(files, parser, versionMetadata, dbMetadata, false);
    }

    public AbstractCloudResultSet(List<File> files, LineParser parser, SwiftMetaData versionMetadata, SwiftMetaData dbMetadata, boolean skipFirstLine) throws Exception {
        this.parser = parser;
        this.versionMetadata = versionMetadata;
        this.dbMetadata = dbMetadata;
        this.files = files;
        this.skipFirstLine = skipFirstLine;
        this.charsetList = new ArrayList<String>();
        for (File file : files) {
            String currentCharset = EncodeUtils.getEncode(file.getAbsolutePath());
            charsetList.add(currentCharset);
            SwiftLoggers.getLogger().info("file: {}, charset: {}", file.getAbsolutePath(), currentCharset);
        }
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.files.get(currentFileIndex)), charsetList.get(currentFileIndex)));
        if (this.skipFirstLine) {
            reader.readLine();
        }
        next = nextRow();
    }

    protected abstract Row nextRow();

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return dbMetadata;
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
}
