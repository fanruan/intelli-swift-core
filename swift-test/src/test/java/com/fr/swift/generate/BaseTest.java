package com.fr.swift.generate;

import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.test.Preparer;

public abstract class BaseTest extends BaseConfigTest {
    protected static final SwiftLogger LOGGER = SwiftLoggers.getLogger(BaseTest.class);

    @Override
    public void setUp() throws Exception {
        Preparer.prepareFrEnv();
        Preparer.prepareContext();
        super.setUp();
        new LocalSwiftServerService().start();
        TestConnectionProvider.createConnection();
    }

    protected void putMetaAndSegment(DataSource dataSource, int segOrder, IResourceLocation location, Types.StoreType storeType) {
//        IMetaData metaData = MetaDataConvertUtil.convert2ConfigMetaData(dataSource.getMetadata());
//        MetaDataConfig.getInstance().addMetaData(dataSource.getSourceKey().getId(), dataSource.getMetadata());
//        IConfigSegment configSegment = new SegmentUnique();
//        configSegment.setSourceKey(dataSource.getSourceKey().getId());
//        configSegment.addSegment(new SegmentKeyUnique(dataSource.getSourceKey(), "", location.getUri(), segOrder, storeType));
//        SegmentConfig.getInstance().putSegment(configSegment);
    }
}
