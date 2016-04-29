package com.finebi.cube.tools;

import com.finebi.cube.location.BICubeLocation;
import com.finebi.cube.location.ICubeResourceLocation;

import java.net.URISyntaxException;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BILocationBuildTestTool {
    public static ICubeResourceLocation buildWrite(String path) {
        try {
            return new BICubeLocation(path,"writer").setWriterSourceLocation();
        } catch (URISyntaxException ing) {
            throw new RuntimeException(ing.getMessage(), ing);
        }
    }
    public static ICubeResourceLocation buildWrite(String path,String name) {
        try {
            return new BICubeLocation(path,name).setWriterSourceLocation();
        } catch (URISyntaxException ing) {
            throw new RuntimeException(ing.getMessage(), ing);
        }
    }
}
