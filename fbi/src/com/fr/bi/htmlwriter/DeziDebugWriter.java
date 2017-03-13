package com.fr.bi.htmlwriter;

import com.fr.bi.resource.ResourceHelper;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.file.BIFileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Young's on 2016/8/18.
 * 写入所有js文件路径到bi_dezi_debug.html中用于IE8下的调试（太多，卡的问题）
 */
public class DeziDebugWriter {
    public static void main(String args []) {
        String [] bases = ResourceHelper.getBaseJs();
        String [] modules = ResourceHelper.getCommonJs();

        String path = System.getProperty("user.dir");
        File nuclear = new File(new File(path).getParentFile(), "nuclear");
        File file = new File(nuclear, "fbi_web/src/com/fr/bi/web/html/bi_dezi_debug.html");
        try {
            BIFileUtils.createFile(file);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            //读模板里的内容
            FileReader reader = new FileReader(new File(nuclear, "fbi/src/com/fr/bi/htmlwriter/bi_dezi_debug.html"));
            BufferedReader br = new BufferedReader(reader);
            String line;
            int baseIndex = 0, moduleIndex = 0;
            List<String> templateContent = new ArrayList<String>();
            while((line = br.readLine()) != null) {
                templateContent.add(line);
                //记一下插入的位置
                if(line.contains("base.js")) {
                    baseIndex = templateContent.size();
                }
                if(line.contains("modules.js")) {
                    moduleIndex = templateContent.size();
                }
            }
            br.close();
            reader.close();

            for(int i = 0; i < baseIndex; i++) {
                writer.write(templateContent.get(i));
                writer.newLine();
            }

            for(String s : bases) {
                writer.write("\t<script type=\"text/javascript\" src=\"${servletURL}?op=resource&resource=/" + s + "\"></script>");
                writer.newLine();
            }

            for(int i = baseIndex + 1; i < moduleIndex; i++) {
                writer.write(templateContent.get(i));
                writer.newLine();
            }

            for(String s : modules) {
                writer.write("\t<script type=\"text/javascript\" src=\"${servletURL}?op=resource&resource=/" + s + "\"></script>");
                writer.newLine();
            }

            for(int i = moduleIndex + 1; i < templateContent.size(); i++) {
                writer.write(templateContent.get(i));
                writer.newLine();
            }

            writer.flush();
            writer.close();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(),e);
        }

    }
}
