import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
    public static void main(String[] args) {
        List<String> rootDirs = setRootDIrs();
        List<String> allFiles = getSelectedFiles(rootDirs);
        Map<String, List<String>> allKeys = getAllKeysFromFiles(allFiles);
        JSONObject keysJson = new JSONObject(allKeys);
        BIFileUtils.writeFile(outputPath,keysJson.toString());

    }

    private static Map<String,List<String>> getAllKeysFromFiles(List<String> allFiles) {
        Map<String,List<String>> container=new HashMap<String, List<String>>();
        for (String file : allFiles) {
            List<String> keys=new ArrayList<String>();
            List<String> lines = readFile(file);
            for (String line : lines) {
               keys.addAll(findKey(line));
            }
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
        String otherJsonOpeStr="put|putOpt|remove|has";
        String optionJsonStr=normalJsonGetStr+"|"+optJsonGetStr+"|"+otherJsonOpeStr;
        return "("+optionJsonStr+")\\(\"([a-z]{1,}_[\\w]{1,})\"";
    }

    public static List<String> readFile(String path) {
        File file = new File(path);
        List<String> lines=new ArrayList<String>();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line;
            try {
                while ((line = reader.readLine()) != null)
                    lines.add(line);
                return lines;
            } finally {
                reader.close();
                fr.close();
            }

        } catch (IOException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }}
