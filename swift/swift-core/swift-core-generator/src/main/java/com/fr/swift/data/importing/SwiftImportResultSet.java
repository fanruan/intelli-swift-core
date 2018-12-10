package com.fr.swift.data.importing;

import com.fr.swift.source.SwiftResultSet;

/**
 * @author yee
 * @date 2018-12-10
 */
public interface SwiftImportResultSet extends SwiftResultSet {
    ImportType type();
}
