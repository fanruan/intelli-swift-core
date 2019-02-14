package com.fr.swift.util;

import com.fr.swift.annotation.SwiftService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.AbstractSwiftService;
import com.fr.swift.service.CollateService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
@SwiftService(name = "collate")
@SwiftBean(name = "collate")
public class TestCollateService extends AbstractSwiftService implements CollateService {
    @Override
    public void autoCollateRealtime(SourceKey tableKey) throws Exception {

    }

    @Override
    public void autoCollateHistory(SourceKey tableKey) throws Exception {

    }

    @Override
    public void appointCollate(SourceKey tableKey, List<SegmentKey> segmentKeyList) throws Exception {

    }

    @Override
    public void autoCollate(SourceKey tableKey) throws Exception {

    }

    @Override
    public ServiceType getServiceType() {
        return null;
    }
}
