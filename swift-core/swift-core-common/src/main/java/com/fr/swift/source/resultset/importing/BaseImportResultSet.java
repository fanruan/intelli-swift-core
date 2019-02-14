package com.fr.swift.source.resultset.importing;

/**
 * @author yee
 * @date 2018-12-10
 */
public abstract class BaseImportResultSet implements SwiftImportResultSet {
    private ImportType type;

    public BaseImportResultSet(ImportType type) {
        this.type = type;
    }

    @Override
    public ImportType type() {
        return type;
    }
}
