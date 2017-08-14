package com.finebi.cube.utils;




import com.fr.stable.StringUtils;
import com.fr.stable.project.ProjectConstants;
import java.io.File;

/**
 * Created by wang on 2017/6/12.
 */
public class BILocationUtils {
    public static String CUBE_FOLDER_NAME = "/Advanced";
    public static String CUBE_TEMP_FOLDER_NAME = "/tCube";
    public static String LOCATION_SEPERATOR = "/";
    /**
     * 去掉Advanced（tCube）作为资源的logicPath
     *
     * @param absolutePath /-999[/Advanced]/5e705d22/26fc5472/
     * @return /-999/5e705d22/26fc5472
     */
    public static String getLogicPath(String absolutePath) {
        return absolutePath.replace(CUBE_FOLDER_NAME, StringUtils.EMPTY).replace(CUBE_TEMP_FOLDER_NAME, StringUtils.EMPTY);
    }


    public static String replaceSlash(String location) {
        if (location != null) {
            return location.replace('\\', '/');
        }
        return location;
    }

}
