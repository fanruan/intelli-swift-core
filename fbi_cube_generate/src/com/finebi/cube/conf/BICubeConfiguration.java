package com.finebi.cube.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.location.BICubeLocation;
import com.finebi.cube.location.provider.BILocationProvider;
import com.fr.bi.conf.manager.location.BILocationManager;
import com.fr.bi.conf.manager.location.BISingleLocationManager;
import com.fr.bi.stable.utils.BIParameterUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.stable.StringUtils;

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
    /*替换cube时先重命名Advanced*/
    private static String CUBE_FOLDER_ADVANCED_TEMP = "AdvancedTemp";
    private BILocationProvider locationProxy;

    public BICubeConfiguration(String range, BILocationProvider locationProxy, String cubeFolderName) {
        this.range = BIParameterUtils.pickValue(range, RANGE_DEFAULT);
        this.locationProxy = locationProxy;
        this.cubeFolderName = BIParameterUtils.pickValue(cubeFolderName, cubeFolderName);
    }

    public static BICubeConfiguration getTempConf(String range) {
        BILocationProvider locationProxy = new BISingleLocationManager(CUBE_TEMP_FOLDER_NAME);
        return new BICubeConfiguration(range, locationProxy, CUBE_TEMP_FOLDER_NAME);
    }

    public static BICubeConfiguration getTempConf(String range,BILocationProvider locationProxy ) {
        return new BICubeConfiguration(range, locationProxy, CUBE_TEMP_FOLDER_NAME);
    }

    public static BICubeConfiguration getAdvancedTempConf(String range) {
        BILocationProvider locationProxy = new BISingleLocationManager(CUBE_FOLDER_ADVANCED_TEMP);
        return new BICubeConfiguration(range, locationProxy, CUBE_FOLDER_ADVANCED_TEMP);
    }

    public static BICubeConfiguration getConf(String range) {
        BILocationProvider locationProxy = BILocationManager.getInstance().getAccessLocationProvider();
        return new BICubeConfiguration(range, locationProxy, CUBE_FOLDER_NAME_DEFAULT);
    }

    @Override
    public URI getRootURI() {
        try {
//            File file = new File(new BICubeLocation(BIConfigurePathUtils.createBasePath(), buildPath(), locationProxy).getAbsolutePath());
//            return URI.create(file.toURI().getRawPath());
            return URI.create(new BICubeLocation(range, StringUtils.EMPTY, locationProxy).getAbsolutePath());

        } catch (URISyntaxException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public BILocationProvider getLocationProvider() {
        return locationProxy;
    }

    protected String buildPath() {
        return range + File.separator + cubeFolderName;
    }
}
