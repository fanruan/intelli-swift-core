package com.fr.bi.conf.fs.develop.enviroment;

import com.fr.bi.conf.fs.develop.DeveloperConfig;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.stable.StableUtils;

import java.io.*;


/**
 * Created by Connery on 2014/12/31.
 */
public class WidgetDataRecorder implements Recorder {
    private File currentFile;


    public WidgetDataRecorder(String path) {
        currentFile = new File(path);
        if (!currentFile.exists()) {
            StableUtils.mkdirs(currentFile);
        }
    }

    public WidgetDataRecorder() {
        this(DeveloperConfig.getTestBasicPath() + DeveloperConfig.FILESEPARATOR + "WidgetRecord");
    }

    public static String getRecorderPath() {
        return DeveloperConfig.getTestBasicPath() + DeveloperConfig.FILESEPARATOR + "WidgetRecord" + DeveloperConfig.FILESEPARATOR;
    }

    private static void writeFile(String data, String absolutePath) throws Exception {
        FileOutputStream out;
        out = new FileOutputStream(new File(absolutePath));

        try {
            out.write(data.getBytes());
        } catch (Exception ex) {

        } finally {
            out.close();
        }
    }

    private static String readFile(String absolutePath) {
        try {
            File file = new File(absolutePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file));//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                StringBuffer sb = new StringBuffer();
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    sb.append(lineTxt);
                }
                read.close();
                return sb.toString();

            } else {
//                System.out.println("找不到指定的文件");

            }
        } catch (Exception ex) {
//            System.out.println("读取文件内容出错");
             BILogger.getLogger().error(ex.getMessage(), ex);
        }
        return "";
    }

    @Override
    public void recordData(String name, String data) throws Exception {
//        JSONObject json = new JSONObject(data);
//        json.put("page", -1);
//        data = json.toString();
        String absolutePath = currentFile.getAbsolutePath() + DeveloperConfig.FILESEPARATOR + name;
        writeFile(data, absolutePath);

        WidgetExpanderRecorder.getInstance().save(name + ".xml");
    }

    public String readData(String name) throws Exception {
        String absolutePath = currentFile.getAbsolutePath() + DeveloperConfig.FILESEPARATOR + name;
        return readFile(absolutePath);
    }

}