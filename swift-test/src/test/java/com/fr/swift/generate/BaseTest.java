package com.fr.swift.generate;

import com.fr.swift.config.meta.MetaDataConfig;
import com.fr.swift.config.segment.IConfigSegment;
import com.fr.swift.config.segment.SegmentConfig;
import com.fr.swift.config.segment.SegmentKeyUnique;
import com.fr.swift.config.segment.SegmentUnique;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.TestConnectionProvider;

public abstract class BaseTest extends BaseConfigTest {
    protected static final SwiftLogger LOGGER = SwiftLoggers.getLogger(BaseTest.class);

    @Override
    public void setUp() throws Exception {
        super.setUp();
        new LocalSwiftServerService().start();
        TestConnectionProvider.createConnection();
    }

    protected void putMetaAndSegment(DataSource dataSource, int segOrder, IResourceLocation location, Types.StoreType storeType) throws Exception {
        MetaDataConfig.getInstance().addMetaData(dataSource.getSourceKey().getId(), dataSource.getMetadata());
        IConfigSegment configSegment = new SegmentUnique();
        configSegment.setSourceKey(dataSource.getSourceKey().getId());
        configSegment.addSegment(new SegmentKeyUnique(dataSource.getSourceKey(), "", location.getUri(), segOrder, storeType));
        SegmentConfig.getInstance().putSegment(configSegment);
    }
}
