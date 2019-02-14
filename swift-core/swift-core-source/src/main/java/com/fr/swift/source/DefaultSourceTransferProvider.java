package com.fr.swift.source;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.SegmentAbsentException;

/**
 * @author pony
 * @date 2017/11/22
 */
@SwiftBean
public class DefaultSourceTransferProvider implements SwiftSourceTransferProvider{

    public SwiftSourceTransfer createSourceTransfer(DataSource dataSource) throws SegmentAbsentException {
        return null;
    }
}
