package com.fr.bi.web.conf.services;

import com.finebi.cube.api.BICubeManager;
import com.fr.base.TableData;
import com.fr.bi.conf.data.source.TableSourceFactory;
import com.fr.bi.conf.data.source.operator.add.selfrelation.OneFieldIsometricUnionRelationOperator;
import com.fr.bi.conf.data.source.operator.add.selfrelation.OneFieldUnionRelationOperator;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.data.impl.RecursionDataModel;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.data.DataModel;
import com.fr.general.data.TableDataException;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.script.Calculator;
import com.fr.stable.Primitive;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class BICreateFieldsUnionAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "create_fields_union";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        ITableSource source = TableSourceFactory.createTableSource(new JSONObject(WebUtils.getHTTPRequestParameter(req, BIJSONConstant.JSON_KEYS.TABLE)), userId);
        String idFieldName = WebUtils.getHTTPRequestParameter(req, "id_field_name");
        String parentIdFieldName = WebUtils.getHTTPRequestParameter(req, "parentid_field_name");
        String divideLength = WebUtils.getHTTPRequestParameter(req, "divide_length");
        boolean isFetchUnionLength = !StringUtils.isEmpty(WebUtils.getHTTPRequestParameter(req, "fetch_union_length"));
        try {
            JSONObject output = null;
            if (StringUtils.isEmpty(parentIdFieldName)){
                output = createIDJSON(source, idFieldName, divideLength, isFetchUnionLength, userId);
            }else {
                output = createIDPIDJSON(source, idFieldName, parentIdFieldName, userId);
            }
            WebUtils.printAsJSON(res, output);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    private JSONObject createIDJSON(ITableSource source, String idFieldName, String divideLength, boolean isFetchUnionLength, long userId) throws Exception{
        JSONObject jo = new JSONObject();
        Set ids = source.getFieldDistinctNewestValues(idFieldName, BICubeManager.getInstance().fetchCubeLoader(userId), userId);
        Set lengthSet = getLengthSetFromID(ids);
        if (isFetchUnionLength) {
            jo.put("size", lengthSet.size() == 1);
            return jo;
        }
        JSONArray lengthJa = new JSONArray();
        if (lengthSet.size() == 1 && !StringUtils.isEmpty(divideLength)) {
            int[] lengthArray = createLengthArrayBySameLengthIDUnion(lengthSet, divideLength);
            for (int i = 0; i < lengthArray.length; i++) {
                lengthJa.put(lengthArray[i]);
            }
        } else {
            Iterator iter = lengthSet.iterator();
            while (iter.hasNext()) {
                lengthJa.put(iter.next());
            }
        }
        if (lengthSet.size() == 1 && !StringUtils.isEmpty(divideLength)) {
            jo.put("field_length", ((Integer) lengthSet.iterator().next()).intValue());
        }
        jo.put("length", lengthJa);
        if (lengthSet.size() > 1) {
            Integer[] len = (Integer[]) lengthSet.toArray(new Integer[lengthSet.size()]);
            int[] lenArray = new int[len.length];
            for (int i = 0; i < len.length; i++) {
                lenArray[i] = len[i].intValue();
            }
            jo.put("floors", parseDataMode(ids, new OneFieldUnionRelationOperator(), lenArray));
        } else if (lengthSet.size() == 1 && !StringUtils.isEmpty(divideLength)) {
            int[] lenArray = createLengthArrayBySameLengthIDUnion(lengthSet, divideLength);
            int totalLength = ((Integer) lengthSet.iterator().next()).intValue();
            jo.put("floors", parseDataMode(ids, new OneFieldIsometricUnionRelationOperator(totalLength), lenArray));
        }
        return jo;
    }



    private int[] createLengthArrayBySameLengthIDUnion(Set lengthSet, String divideLength) {
        int totalLength = ((Integer) lengthSet.iterator().next()).intValue();
        int divide = Integer.valueOf(divideLength).intValue();
        int len = totalLength / divide;
        int tail = totalLength % divide;
        int[] lenArray = new int[len];
        for (int i = 0; i < lenArray.length; i++) {
            lenArray[i] = (i + 1) * divide + tail;
        }
        return lenArray;
    }

    private Set<Integer> getLengthSetFromID(Set set){
        Set<Integer> intSet = new TreeSet<Integer>();
        for (Object ob : set){
            if(ob != null){
                String v = ob.toString();
                int len = v.length();
                Integer key = new Integer(len);
                if(!intSet.contains(key)){
                    intSet.add(key);
                }
            }
        }

        return intSet;
    }

    private JSONArray parseDataMode(Set ids, OneFieldUnionRelationOperator operator, int[] len) throws TableDataException {
        int columnSize = len.length;
        int rowSize =  Math.min(BIBaseConstant.PREVIEW_COUNT, ids.size());
        JSONArray ja = new JSONArray();
        for (int i = 0; i < columnSize; i++) {
            JSONArray column = new JSONArray();
            Set set = new HashSet();
            Iterator it = ids.iterator();
            int j = 0;
            while (it.hasNext() && j++ < rowSize){
                Object obj = it.next();
                if (obj == null || obj == Primitive.NULL) {
                    continue;
                }
                String s = operator.dealWithLayerValue(obj.toString(), len);
                if(s.length() >= len[i]){
                    String result = s.substring(0, len[i]);
                    set.add(operator.dealWithValue(result));
                }
            }
            Iterator iter = set.iterator();
            while (iter.hasNext()) {
                column.put(iter.next());
            }
            ja.put(column);
        }
        return ja;
    }

    private JSONObject createIDPIDJSON(ITableSource source, String idFieldName, String pidName, long userId) throws Exception{
        JSONObject jo = new JSONObject();
        List<String> fields = new ArrayList<String>();
        fields.add(idFieldName);
        fields.add(pidName);
        TableData td = source.createTableData(fields, BICubeManager.getInstance().fetchCubeLoader(userId), userId);
        DataModel dm =  new RecursionDataModel(td.createDataModel(Calculator.createCalculator()), 0, 1);
        int columnSize = dm.getColumnCount();
        int rowSize = dm.getRowCount();
        JSONArray ja = new JSONArray();
        for (int i = 2 ; i < columnSize; i++) {
            JSONArray column = new JSONArray();
            Set set = new HashSet();
            for (int j = 0; j < rowSize; j++) {
                Object obj = dm.getValueAt(j, i);
                if (obj == null || obj == Primitive.NULL) {
                    continue;
                }
                set.add(obj);
            }
            Iterator iter = set.iterator();
            while (iter.hasNext()) {
                column.put(iter.next());
            }
            ja.put(column);
        }
        return jo.put("floors", ja);
    }


}