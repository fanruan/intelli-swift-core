import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kary on 2017/5/3.
 */
public class findLineKeyTest {
    private static final String outputPath="C:\\code\\FineBI\\keys.json";
    private static final String rootDir="C:\\code\\FineBI";
    public static void main(String[] args) {
        List<String> rootDirs = setRootDIrs();
        List<String> allFiles = getSelectedFiles(rootDirs);
        Map<String, List<String>> allKeys = getAllKeysFromFiles(allFiles);
//        for (String s : allKeys.keySet()) {
//            System.out.println(s);
//        }
        JSONObject keysJson = new JSONObject(allKeys);
        BIFileUtils.writeFile(outputPath,keysJson.toString());

    }

    private static Map<String,List<String>> getAllKeysFromFiles(List<String> allFiles) {
        Map<String,List<String>> container=new HashMap<String, List<String>>();
        for (String file : allFiles) {
            List<String> keys = findKey(BIFileUtils.readFile(file));
            for (String key : keys) {
                if (!container.containsKey(key)){
                    container.put(key,new ArrayList<String>());
                }
                container.get(key).add(new File(file).getName());
            }
        }
        return container;
    }

    private static List<String> setRootDIrs() {
        List<String> rootDirs = new ArrayList<String>();
        rootDirs.add(rootDir);
        return rootDirs;
    }

    private static List<String> getSelectedFiles(List<String> rootDirs) {
        List<String> allFiles = new ArrayList<String>();
        for (String dir : rootDirs) {
            List<String> filesInDir = BIFileUtils.getListFiles(dir, "java", true);
            allFiles.addAll(filesInDir);
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

    private static String createRegStr() {
        String normalJsonGetStr="get|getString|getInt|getLong|getDouble|getFloat|getBoolean|getJSONObject|getJsonArray";
        String optJsonGetStr="opt|optString|optInt|optLong|optDouble|optFloat|optBoolean|optJSONObject|optJSONArray|opt";
        String otherJsonOpeStr="put|putOpt|remove";
        String optionJsonStr=normalJsonGetStr+"|"+optJsonGetStr+"|"+otherJsonOpeStr;
        return "("+optionJsonStr+")\\(\"([a-z]{1,}_[\\w]{1,})\"\\)";
    }
}
