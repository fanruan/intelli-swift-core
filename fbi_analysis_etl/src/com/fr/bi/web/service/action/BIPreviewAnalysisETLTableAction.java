package com.fr.bi.web.service.action;

import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cluster.utils.BIUserAuthUtils;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.data.AnalysisETLSourceFactory;
import com.fr.bi.etl.analysis.data.AnalysisETLSourceField;
import com.fr.bi.etl.analysis.data.UserCubeTableSource;
import com.fr.bi.exception.BIMemoryDataOutOfLimitException;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by 小灰灰 on 2016/5/16.
 */
public class BIPreviewAnalysisETLTableAction extends AbstractAnalysisETLAction {
    private static BILogger LOGGER = BILoggerFactory.getLogger(BIPreviewAnalysisETLTableAction.class);

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        long userId = BIUserAuthUtils.getCurrentUserID(req);
        String itemArray = WebUtils.getHTTPRequestParameter(req, Constants.ITEMS);
        JSONArray items = new JSONArray(itemArray);

        AnalysisCubeTableSource source = AnalysisETLSourceFactory.createTableSource(items, userId);
        LOGGER.info("the table source info is: " + source.createJSON());
        List<AnalysisETLSourceField> fields = source.getFieldsList();
        UserCubeTableSource userTableSource = source.createUserTableSource(userId);

        try {
            ICubeTableService service = PartCubeDataLoader.getInstance(userId, userTableSource).getTableIndex(userTableSource, 0, 20);
            JSONArray values = new JSONArray();
            for (int i = 0; i < Math.min(service.getRowCount(), 20); i++) {
                JSONArray ja = new JSONArray();
                for (AnalysisETLSourceField f : fields) {
                    Object ob = service.getColumnDetailReader(new IndexKey(f.getFieldName())).getValue(i);
                    JSONObject jo = new JSONObject();
                    ob = BICollectionUtils.cubeValueToWebDisplay(ob);
                    jo.put("text", ob);
                    ja.put(jo);
                }
                values.put(ja);
            }
            JSONObject result = new JSONObject();
            result.put(BIJSONConstant.JSON_KEYS.VALUE, values);
            WebUtils.printAsJSON(res, result);
        } catch (BIMemoryDataOutOfLimitException e) {
            User user = UserControl.getInstance().getUser(userId);
            LOGGER.info("user name " + user.getUsername() + " password " + user.getPassword() + " SPA detail data out of limit");
            JSONArray values = JSONArray.create();
            JSONArray ja = JSONArray.create();
            JSONObject jo = JSONObject.create();
            jo.put("text", Inter.getLocText("BI-SPA_Detail_Data_Out_Of_Limit"));
            ja.put(jo);
            values.put(ja);
            JSONObject result = new JSONObject();
            result.put(BIJSONConstant.JSON_KEYS.VALUE, values);
            WebUtils.printAsJSON(res, result);
        }
    }

    @Override
    public String getCMD() {
        return "preview_table";
    }
}
