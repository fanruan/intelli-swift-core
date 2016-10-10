package com.fr.bi.htmlwriter;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.file.BIFileUtils;

import java.io.*;
import java.util.*;

/**
 * Created by Young's on 2016/8/18.
 */
public class Font4IE8Writer {
    public static void main(String args []) {
        String path = System.getProperty("user.dir");
        File nuclear = new File(new File(path).getParentFile(), "nuclear");
        File utilsFont = new File(nuclear, "fbi_web/src/com/fr/bi/web/less/utils/font.less");
        File libFont = new File(nuclear, "fbi_web/src/com/fr/bi/web/less/lib/font.less");

        File cssFont = new File(nuclear, "fbi_web/src/com/fr/bi/web/css/utils/bi.ie8.font.css");

        //分别读两个文件来写文件
        Map<String, String> libMap = new HashMap<String, String>();
        Map<String, String> utilsMap = new HashMap<String, String>();

        try {
            BIFileUtils.createFile(cssFont);
            BufferedWriter writer = new BufferedWriter(new FileWriter(cssFont));

            FileReader libReader = new FileReader(libFont);
            FileReader utilsReader = new FileReader(utilsFont);

            BufferedReader libBr = new BufferedReader(libReader);
            String libLine;
            while((libLine = libBr.readLine()) != null) {
                //解析一下行
                if(libLine.contains("@")) {
                    String key = libLine.split(":")[0];
                    String value = libLine.split("\"")[1];
                    libMap.put(key, value);
                }
            }
            libBr.close();
            libReader.close();

            BufferedReader utilsBr = new BufferedReader(utilsReader);
            String utilsLine;
            while((utilsLine = utilsBr.readLine()) != null) {
                //解析一下行
                if(utilsLine.contains(".font")) {
                    String tempStr = utilsLine.substring(utilsLine.indexOf("(") + 1, utilsLine.indexOf(")"));
                    String key = tempStr.split(",")[0].trim();
                    String value = tempStr.split(",")[1].trim();
                    utilsMap.put(key, value);
                }
            }
            utilsBr.close();
            utilsReader.close();

            writer.write("@font-face { font-family: 'bi'; src: url('${servletURL}?op=resource&resource=/com/fr/bi/web/resources/fonts/iconfont.eot'); }");

            writer.newLine();

            Iterator<Map.Entry<String, String>> entryIterator = utilsMap.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<String, String> entry = entryIterator.next();
                String key = entry.getKey();
                String value = entry.getValue();
                String content = libMap.get(value).substring(1);
                writer.write("." + key + " i" + "{ *zoom: expression( this.runtimeStyle['zoom'] = '1',this.innerHTML = '&#x" + content + ";'); }");
                writer.newLine();
            }

            writer.flush();
            writer.close();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }

    }
}
