package com.fr.swift.source.etl.datamining;

import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.List;

public interface AlgorithmMetaData {
    List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas);
}
