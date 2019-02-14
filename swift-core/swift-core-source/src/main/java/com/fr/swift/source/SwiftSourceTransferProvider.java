package com.fr.swift.source;

import com.fr.swift.exception.SegmentAbsentException;

public interface SwiftSourceTransferProvider {
    SwiftSourceTransfer createSourceTransfer(DataSource dataSource) throws SegmentAbsentException;
}
