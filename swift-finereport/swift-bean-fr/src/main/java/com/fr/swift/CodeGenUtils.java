package com.fr.swift;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyon on 2018/12/3.
 */
public class CodeGenUtils {

    public static void main(String[] args) throws Exception {
        // 执行生成依赖fr-third的代码
        String srcDir = "/Users/lyon/finereport/intelli/intelli-swift/swift-public/swift-bean/src/main/java/com/fr/swift/query/info/bean";
        String destDir = "/Users/lyon/finereport/intelli/intelli-swift/swift/swift-analyse-base/src/main/java/com/fr/swift/query/info/bean";
//        String srcDir = "<absolute_path_2_module_swift-public>/swift-bean/src/main/java/com/fr/swift";
//        String destDir = "<absolute_path_2_module_swift-finereport>/swift-bean-fr/src/main/java/com/fr/swift";
        File source = new File(srcDir);
        File dest = new File(destDir);
        copy(source, dest);
    }

    private static void copy(File source, File dest) throws Exception {
        if (source.isFile()) {
            write(source.getAbsolutePath(), dest.getAbsolutePath());
            return;
        }
        for (File file : source.listFiles()) {
            File df = new File(dest.getAbsolutePath() + File.separator + file.getName());
            if (file.isDirectory() && !df.exists()) {
                df.mkdir();
            }
            copy(file, df);
        }
    }

    private static void write(String src, String dest) throws Exception {
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(src));
        String line = null;
        while ((line = reader.readLine()) != null) {
            line = line.replaceAll("com\\.fasterxml\\.jackson", "com.fr.swift.base.json");
            lines.add(line);
        }
        reader.close();
        PrintWriter writer = new PrintWriter(new File(dest));
        for (String str : lines) {
            writer.println(str);
        }
        writer.flush();
        writer.close();
    }
}
