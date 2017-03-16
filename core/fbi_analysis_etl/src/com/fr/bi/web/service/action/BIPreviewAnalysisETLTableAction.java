package com.fr.bi.web.service.action;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.data.AnalysisETLSourceFactory;
import com.fr.bi.etl.analysis.data.AnalysisETLSourceField;
import com.fr.bi.etl.analysis.data.UserCubeTableSource;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.List;

/**
 * Created by 小灰灰 on 2016/5/16.
 */
public class BIPreviewAnalysisETLTableAction extends AbstractAnalysisETLAction {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String itemArray = WebUtils.getHTTPRequestParameter(req, Constants.ITEMS);
        JSONArray items = new JSONArray(itemArray);

        AnalysisCubeTableSource source = AnalysisETLSourceFactory.createTableSource(items, userId);
        List<AnalysisETLSourceField> fields = source.getFieldsList();
        UserCubeTableSource userTableSource = source.createUserTableSource(userId);
        ICubeTableService service = PartCubeDataLoader.getInstance(userId, userTableSource).getTableIndex(userTableSource, 0, 20);
        JSONArray values = new JSONArray();
        for (int i = 0; i < Math.min(service.getRowCount(), 20); i++) {
            JSONArray ja = new JSONArray();
            for (AnalysisETLSourceField f : fields) {
                Object ob = service.getColumnDetailReader(new IndexKey(f.getFieldName())).getValue(i);
                JSONObject jo = new JSONObject();
                if (ComparatorUtils.equals(ob, Double.POSITIVE_INFINITY)) {
                    ob = "∞";
                } else if (ComparatorUtils.equals(ob, Double.NEGATIVE_INFINITY)) {
                    ob = "-∞";
                }
                jo.put("text", service.getColumns().get(new IndexKey(f.getFieldName())).getFieldType() == DBConstant.COLUMN.DATE ? getDateString(ob) : ob);
                ja.put(jo);
            }
            values.put(ja);
        }
        JSONObject result = new JSONObject();
        result.put(BIJSONConstant.JSON_KEYS.VALUE, values);
        WebUtils.printAsJSON(res, result);
    }

    private String getDateString(Object ob) {
        if (ob == null){
            return StringUtils.EMPTY;
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis((Long) ob);
        return c.get(Calendar.YEAR) + "/" + insertZero(c.get(Calendar.MONTH) + 1) + "/" + insertZero(c.get(Calendar.DAY_OF_MONTH)).toString();
    }

    private Object insertZero(int time) {
        if (time < 10) {
            return "0" + time;
        }
        return "" + time;
    }

    @Override
    public String getCMD() {
        return "preview_table";
    }
}
