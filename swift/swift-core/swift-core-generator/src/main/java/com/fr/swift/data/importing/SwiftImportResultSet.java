package com.fr.swift.data.importing;

import com.fr.swift.result.SwiftResultSet;

/**
 * @author yee
 * @date 2018-12-10
 */
public interface SwiftImportResultSet extends SwiftResultSet {
    ImportType type();
}
