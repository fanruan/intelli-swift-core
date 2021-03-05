package com.fr.swift.cloud.source.resultset.importing;

import com.fr.swift.cloud.result.SwiftResultSet;

/**
 * @author yee
 * @date 2018-12-10
 */
public interface SwiftImportResultSet extends SwiftResultSet {
    ImportType type();
}
