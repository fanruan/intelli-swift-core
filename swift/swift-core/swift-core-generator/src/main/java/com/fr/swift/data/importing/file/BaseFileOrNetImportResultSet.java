package com.fr.swift.data.importing.file;

import com.fr.swift.data.importing.BaseImportResultSet;
import com.fr.swift.data.importing.ImportType;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

/**
 * @author yee
 * @date 2018-12-10
 */
public abstract class BaseFileOrNetImportResultSet<Paths> extends BaseImportResultSet {

    private Paths paths;
    private SwiftSourceAlloter alloter;
    private FileLineParser parser;

    public BaseFileOrNetImportResultSet(Paths paths, SwiftSourceAlloter alloter, FileLineParser parser) {
        super(ImportType.FILE_NET_BASE);
        this.paths = paths;
        this.alloter = alloter;
        this.parser = parser;
    }
}
