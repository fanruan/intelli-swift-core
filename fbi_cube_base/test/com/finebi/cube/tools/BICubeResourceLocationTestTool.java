package com.finebi.cube.tools;

import com.finebi.cube.data.disk.BIDiskWriterReaderTest;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.tools.BILocationBuildTestTool;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeResourceLocationTestTool {
    public static ICubeResourceLocation getBasic(String name) {
        return BILocationBuildTestTool.buildWrite(BIDiskWriterReaderTest.projectPath, name);

    }
}
