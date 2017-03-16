package com.finebi.cube.tools;

import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.provider.BICubeLocationProvider;
import com.finebi.cube.provider.BIProjectPathProvider;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeResourceLocationTestTool {
    public static ICubeResourceLocation getBasic(String name) {
        return BICubeLocationProvider.buildWrite(BIProjectPathProvider.projectPath, name);
    }
}
