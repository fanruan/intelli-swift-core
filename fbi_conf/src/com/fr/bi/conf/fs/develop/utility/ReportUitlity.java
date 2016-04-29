package com.fr.bi.conf.fs.develop.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Connery on 2015/1/2.
 */
public class ReportUitlity {

    public static String splitSessionID(String htmlContent) {
        String reg = "(?<=sessionID:)\\d+(?=,)";
        Pattern p = Pattern.compile(reg);
        Matcher matcher = p.matcher(htmlContent);
        while (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    public static void writeExcel(byte[] content, String absolutePath) {
        File storeFile = new File(absolutePath);

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(storeFile);
            //得到网络资源的字节数组,并写入文件
            output.write(content);
            output.close();
        } catch (Exception ex) {
        }
    }
}