import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * Created by Kary on 2017/5/16.
 */
public class BuilderTestUtils {
    public static Map<Integer, List<JSONObject>> createViewMap(String viewMapStr) throws JSONException {
        Map<Integer, List<JSONObject>> dimAndTar = new HashMap<Integer, List<JSONObject>>();
        Iterator keys = new JSONObject(viewMapStr).keys();
        while (keys.hasNext()) {
            String key = String.valueOf(keys.next());
            JSONArray array = new JSONObject(viewMapStr).getJSONArray(key);
            List dims = new ArrayList();
            for (int i = 0; i < array.length(); i++) {
                dims.add(array.get(i));
            }
            dimAndTar.put(Integer.valueOf(key), dims);
        }
        return dimAndTar;
    }
}
