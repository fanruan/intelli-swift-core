import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kary on 2017/5/3.
 */
public class ReplaceLineKeyTest {
    private static final String OUTPUTDIR = "C:\\Users\\吴凯\\Desktop\\CamelKeys1.json";

    public static void main(String[] args) {
        //指定范围
        List<String> rootDirs = setRootDIrs();
        List<String> allFiles = getSelectedFiles(rootDirs);
        //输出存在的下划线命名
        saveKeysInfo(allFiles);
        //根据白名单和范围进行替换
//        String[] replaceKeys ={"data_label", "from_color", "color_condition", "x_view", "y_view", "formula_value"};
//        replaceAllKeys(allFiles, replaceKeys);
    }

    private static void saveKeysInfo(List<String> allFiles) {
        Map<String, Set<String>> allKeys = getAllKeysFromFiles(allFiles);
        JSONObject keysJson = new JSONObject(allKeys);
        BIFileUtils.writeFile(OUTPUTDIR, keysJson.toString());
    }

    private static void replaceAllKeys(List<String> allFiles, String[] replaceKeys) {
        for (String file : allFiles) {
            List<String> fileList = new ArrayList<String>();
            fileList.add(file);
            if (getAllKeysFromFiles(fileList).size() == 0) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            List<String> lines = readFile(file);

            for (int i = 0; i < lines.size(); i++) {
                sb.append(replaceKey(lines.get(i), replaceKeys));
                if (i < lines.size() - 1) {
                    sb.append("\n");
                }
            }
            BIFileUtils.writeFile(file, sb.toString());
        }
    }

    private static Map<String, Set<String>> getAllKeysFromFiles(List<String> allFiles) {
        Map<String, Set<String>> container = new HashMap<String, Set<String>>();
        for (String file : allFiles) {
            List<String> keys = new ArrayList<String>();
            keys.addAll(findKey(BIFileUtils.readFile(file)));
            for (String key : keys) {
                if (!container.containsKey(key)) {
                    container.put(key, new HashSet<String>());
                }
                    container.get(key).add(new File(file).getName());
            }
        }
        return container;
    }

    private static List<String> setRootDIrs() {
        List<String> rootDirs = new ArrayList<String>();
        rootDirs.add("C:\\code\\FineBI\\nuclear-conf");
        rootDirs.add("C:\\code\\FineBI\\nuclear-core");
        rootDirs.add("C:\\code\\FineBI\\nuclear-web");
        return rootDirs;
    }

    private static List<String> getSelectedFiles(List<String> rootDirs) {
        List<String> allFiles = new ArrayList<String>();
        for (String dir : rootDirs) {
            List<String> filesInDir = BIFileUtils.getListFiles(dir, "java", true);
            allFiles.addAll(filesInDir);
        }
        Iterator<String> iterator = allFiles.iterator();
        while (iterator.hasNext()){
            String name = iterator.next();
            if (name.endsWith("BICubeFieldSource.java")||name.contains("test")){
                iterator.remove();
            }
        }
        return allFiles;
    }

    private static List<String> findKey(String lineStr) {
        List<String> keys = new ArrayList<String>();
        String finalReg = createRegStr();
        Pattern linePattern = Pattern.compile(finalReg);
        Matcher m = linePattern.matcher(lineStr);
        boolean result = m.find();
        while (result) {
            keys.add(m.group(2));
            result = m.find();
        }
        return keys;
    }

    private static String replaceKey(String oriContent, String[] keys) {
        String finalReg = createRegStr();
        Pattern linePattern = Pattern.compile(finalReg);
        Matcher m = linePattern.matcher(oriContent);
        boolean result = m.find();
        while (result) {
            if (Arrays.asList(keys).contains(m.group())) {
                String camels = lineToCamels(m.group());
                oriContent = oriContent.replace(m.group(), camels);
            }
            result = m.find();
        }
        return oriContent;
    }

    private static String replaceKey(String oriContent) {
        String finalReg = createRegStr();
        Pattern linePattern = Pattern.compile(finalReg);
        Matcher m = linePattern.matcher(oriContent);
        boolean result = m.find();
        while (result) {
            String camels = lineToCamels(m.group());
            oriContent = oriContent.replace(m.group(), camels);
            result = m.find();
        }
        return oriContent;
    }

    private static String createRegStr() {
        String normalJsonGetStr = "get|getString|getInt|getLong|getDouble|getFloat|getBoolean|getJSONObject|getJsonArray";
        String optJsonGetStr = "opt|optString|optInt|optLong|optDouble|optFloat|optBoolean|optJSONObject|optJSONArray|opt";
        String otherJsonOpeStr = "put|putOpt|remove|has";
        String optionJsonStr = normalJsonGetStr + "|" + optJsonGetStr + "|" + otherJsonOpeStr;
        return "(" + optionJsonStr + ")\\(\"([a-z]{1,}_[\\w]{1,})\"";
    }

    private static String lineToCamels(String str) {
        Pattern linePattern = Pattern.compile("(?!^)_(\\w)");
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static List<String> readFile(String path) {
        File file = new File(path);
        List<String> lines = new ArrayList<String>();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                return lines;
            } finally {
                reader.close();
                fr.close();
            }

        } catch (IOException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }
}
