package com.finebi.cube.tools;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.provider.BICubeLocationProvider;
import com.finebi.cube.provider.BIProjectPathProvider;

import java.net.URI;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeConfigurationTool implements ICubeConfiguration {
    @Override
    public URI getRootURI() {
        return URI.create(BICubeLocationProvider.buildWrite(BIProjectPathProvider.projectPath, "table").getAbsolutePath());
    }
}
