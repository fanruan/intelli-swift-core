package com.finebi.cube.security;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2016/7/31.
 *
 * @author Connery
 * @since 4.0
 */
public class CubeCRCCalculator {
    private Map<String, Long> result;

    public CubeCRCCalculator() {
        result = new HashMap<String, Long>();
    }

    public void scanDir(File currentFile) {
        String currentPath = currentFile.getAbsolutePath();
        if (currentFile.exists()) {
            if (currentFile.isFile()) {
                result.put(currentPath, SumCRC32.calculate(currentPath));
            } else {
                for (File file : currentFile.listFiles()) {
                    scanDir(file);
                }
            }
        }
    }

    public Map<String, Long> getResult() {
        return Collections.unmodifiableMap(result);
    }

    public void clear() {
        result.clear();
    }
//    public static void main(String[] args) {
//        CubeCRCCalculator cubeCRCCalculator = new CubeCRCCalculator();
//        File cube = new File("D:\\WebReport\\WebReport\\WEB-INF\\resources\\cubes");
//        cubeCRCCalculator.scanDir(cube);
//        Map<String, Long> result = cubeCRCCalculator.getResult();
//        for (Map.Entry<String, Long> entry : result.entrySet()) {
//            System.out.println(entry.getKey() + " :" + entry.getValue());
//        }
//    }


}
