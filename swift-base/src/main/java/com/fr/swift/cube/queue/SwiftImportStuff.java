package com.fr.swift.cube.queue;

import com.fr.swift.source.DataSource;

import java.io.Serializable;
import java.util.List;

/**
 * @author anchore
 * @date 2018/6/28
 */
public class SwiftImportStuff implements ImportStuff, Serializable {
    private List<DataSource> tables;

    public SwiftImportStuff(List<DataSource> tables) {
        this.tables = tables;
    }

    @Override
    public List<DataSource> getTables() {
        return tables;
    }
}