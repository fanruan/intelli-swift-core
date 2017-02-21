package com.finebi.cube.tools;

import com.finebi.cube.data.disk.BIDiskWriterReaderTest;
import com.fr.bi.stable.utils.program.BIStringUtils;

import java.io.File;

/**
 * Created by Lucifer on 2017-2-21.
 *
 * @author Lucifer
 * @since 4.0
 */
public class BIProjectPathTool {

    private static String basePath = computeBasePath();

    public static String projectPath = computePath();

    public static String bigfilePath = computeBigfilePath();

    private static String computeBigfilePath() {
        if (basePath.endsWith(File.separator)) {
            return BIStringUtils.append(basePath, "testFolder", File.separator, "bigfiles", File.separator, "cube");
        }
        return basePath;
    }


    private static String computePath() {
        if (basePath.endsWith(File.separator)) {
            return BIStringUtils.append(basePath, "testFolder", File.separator, "cube");
        }
        return basePath;
    }

    private static String computeBasePath() {
        String classFileName = "classes";
        String libFileName = "lib";
        File directory = new File("");
        String classRootPath = BIDiskWriterReaderTest.class.getResource("/").getPath();
        classRootPath = classRootPath.replace("/", File.separator);
        if (classRootPath.endsWith(File.separator)) {
            classRootPath = cut(classRootPath, File.separator);
        }
        if (classRootPath.endsWith(classFileName)) {
            classRootPath = cut(classRootPath, classFileName);
        }
        if (classRootPath.endsWith(libFileName)) {
            classRootPath = cut(classRootPath, libFileName);
        }
        return classRootPath;
    }

    private static String cut(String path, String suffix) {
        return BIStringUtils.cutEndChar(path, suffix);
    }

    public static void main(String[] args) {
        System.out.println(computeBasePath());
        System.out.println(computePath());
        System.out.println(computeBigfilePath());
    }
}
