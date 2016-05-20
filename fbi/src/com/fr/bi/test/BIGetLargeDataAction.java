package com.fr.bi.test;

import com.fr.bi.stable.data.db.*;
import com.fr.bi.stable.utils.program.BIPhoneticismUtils;
import com.fr.bi.base.FinalLong;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Root on 2015/9/6.
 */
public class BIGetLargeDataAction extends ActionNoSessionCMD {
    /**
     * 方法
     *
     * @param req 参数
     * @param res 参数
     * @throws Exception
     */
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String timesString = WebUtils.getHTTPRequestParameter(req, "times");//第几次加载

        String selectedValuesString = WebUtils.getHTTPRequestParameter(req, "selected_values");
        String countString = WebUtils.getHTTPRequestParameter(req, "count");

        int times = timesString == null ? 1 : Integer.parseInt(timesString);
        int count = countString == null ? BIBaseConstant.PART_DATA_GROUP_MAX_LIMIT : Integer.parseInt(countString);

        IPersistentTable table = BIDBUtils.getDBTable("important", "items");
        List<PersistentField> columns = table.getFieldList();

        BICubeFieldSource[] fields = new BICubeFieldSource[1];

        fields[0] = new BICubeFieldSource(UUID.randomUUID().toString(), columns.get(0).getFieldName(),
                BIDBUtils.checkColumnClassTypeFromSQL(columns.get(0).getType(), columns.get(0).getColumnSize(), columns.get(0).getScale()),
                columns.get(0).getColumnSize());

        final List<String> list = new ArrayList<String>();
        final int start = (times - 1) * count;
        final int end = times * count;
        final FinalLong all = new FinalLong();
        BIDBUtils.runSQL(BIDBUtils.getSQLStatement("important", "items"), fields, new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue data) {
                if (data.getRow() >= start && data.getRow() < end) {
                    list.add((String) data.getValue());
                }
                all.value++;
            }
        });
        JSONArray ja = new JSONArray();
        for (String val : list) {
            JSONObject jo = new JSONObject();
            jo.put("value", val);
            jo.put("py", BIPhoneticismUtils.getPingYin(val));
            ja.put(jo);
        }

        JSONObject jo = new JSONObject();
        jo.put("all", all.value);
        jo.put("items", ja);
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "get_large_data";
    }
}