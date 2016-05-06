package com.finebi.cube.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.location.BICubeLocation;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class created on 2016/4/20.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeConfiguration implements ICubeConfiguration {
    private String range;
    private String cubeFolderName;

    public BICubeConfiguration(String range, String cubeFolderName) {

        this.range = range;
        if (range == null) {
            this.range = "root";
        } else {
            this.range = range;
        }
        if (cubeFolderName == null) {
            this.cubeFolderName = "Advanced";
        } else {
            this.cubeFolderName = cubeFolderName;
        }
    }

    public static BICubeConfiguration getTempConf(String range) {
        return new BICubeConfiguration(range, ".temp");
    }

    public static BICubeConfiguration getConf(String range) {
        return new BICubeConfiguration(range, "Advanced");
    }

    @Override
    public URI getRootURI() {
        try {
            return URI.create(new BICubeLocation(BIPathUtils.createBasePath(), buildPath()).getAbsolutePath());
        } catch (URISyntaxException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    protected String buildPath() {
        return range + File.separator + cubeFolderName;
    }
}
