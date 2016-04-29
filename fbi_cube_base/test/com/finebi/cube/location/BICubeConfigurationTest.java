package com.finebi.cube.location;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.data.disk.BIDiskWriterReaderTest;
import com.finebi.cube.tools.BILocationBuildTestTool;

import java.net.URI;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeConfigurationTest implements ICubeConfiguration {
    @Override
    public URI getRootURI() {
        return URI.create(BILocationBuildTestTool.buildWrite(BIDiskWriterReaderTest.projectPath, "table").getAbsolutePath());
    }
}
