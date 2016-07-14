package com.finebi.cube.conf;

import com.finebi.cube.ICubeConfiguration;
import com.fr.bi.util.BIConfigurePathUtils;
import com.finebi.cube.location.BICubeLocation;
import com.fr.bi.stable.utils.BIParameterUtils;
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
    private static String CUBE_FOLDER_NAME_DEFAULT = "Advanced";
    private static String CUBE_TEMP_FOLDER_NAME = "tCube";
    private static String RANGE_DEFAULT = "default";
    public BICubeConfiguration(String range, String cubeFolderName) {

        this.range = range;

        this.range = BIParameterUtils.pickValue(range, RANGE_DEFAULT);
        this.cubeFolderName = BIParameterUtils.pickValue(cubeFolderName, CUBE_FOLDER_NAME_DEFAULT);
    }

    public static BICubeConfiguration getTempConf(String range) {
        return new BICubeConfiguration(range, CUBE_TEMP_FOLDER_NAME);
    }

    public static BICubeConfiguration getConf(String range) {
        return new BICubeConfiguration(range, CUBE_FOLDER_NAME_DEFAULT);
    }

    @Override
    public URI getRootURI() {
        try {
            File file = new File(new BICubeLocation(BIConfigurePathUtils.createBasePath(), buildPath()).getAbsolutePath());
            return URI.create(file.toURI().getRawPath());
        } catch (URISyntaxException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    protected String buildPath() {
        return range + File.separator + cubeFolderName;
    }
}
