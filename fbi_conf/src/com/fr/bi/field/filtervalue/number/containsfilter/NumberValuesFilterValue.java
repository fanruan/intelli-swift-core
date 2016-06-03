/**
 *
 */
package com.fr.bi.field.filtervalue.number.containsfilter;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.report.widget.field.filtervalue.AbstractFilterValue;
import com.fr.bi.conf.report.widget.field.filtervalue.number.NumberFilterValue;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Daniel
 */
public abstract class NumberValuesFilterValue extends AbstractFilterValue<Number> implements NumberFilterValue {


    /**
     *
     */
    private static final long serialVersionUID = 2181210581260108345L;
    @BICoreField
    protected Set<Double> valueSet = new HashSet<Double>();


    @Override
    public boolean canCreateFilterIndex() {
        return true;
    }

    @Override
    public boolean isTopOrBottomFilterValue() {
        return false;
    }

    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader,
                                             long userId) {
        if (valueSet.isEmpty()) {
            return null;
        }
        return createFilterIndexByRelations(dimension.getRelationList(), dimension.createNoneSortGroupValueMapGetter(target, loader),
                loader.getTableIndex(dimension.getField().getTableBelongTo().getTableSource()), loader.getTableIndex(target.getTableSource()), dimension.createKey());
    }


    private GroupValueIndex createFilterIndexByRelations(List<BITableSourceRelation> relations, ICubeColumnIndexReader sgm, ICubeTableService cti, ICubeTableService eti, BIKey ckey) {
        if (relations == null) {
            return null;
        }
        Object[] value = copyValue(sgm);;
        GroupValueIndex sgvi = GVIFactory.createAllEmptyIndexGVI();
        Object[] indexs = sgm.getGroupIndex(value);
        boolean hasValue = value.length > 0;
        for (int i = 0, len = indexs.length; i < len; i++) {
            GroupValueIndex gvi = (GroupValueIndex) indexs[i];
            if (gvi != null) {
                sgvi.or(gvi);
            }
        }
        return hasValue ? sgvi : null;
    }

    //TODO 暂时没有更好的解决办法，先这样处理
    private Object[]  copyValue(ICubeColumnIndexReader sgm) {
        Object[] v = sgm.createKey(valueSet.size());
        Object firstKey = sgm.firstKey();
        if(firstKey instanceof  Long) {
            Iterator<Double> iter = valueSet.iterator();
            int i = 0;
            while (iter.hasNext()) {
                Double d = iter.next();
                if(Double.isNaN(d)) {
                    v[i++] = Long.MAX_VALUE;
                } else {
                    v[i++] = d.longValue();
                }
            }
            return v;
        } else {
            return  valueSet.toArray(new Double[valueSet.size()]);
        }
    }


    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        if (jo.has("filter_value")) {
            JSONArray ja = null;
            try {
                JSONObject valueOb = jo.getJSONObject("filter_value");
                ja = valueOb.getJSONArray("value");
            } catch (JSONException e) {
                ja = new JSONArray();
            }
            for (int i = 0, len = ja.length(); i < len; i++) {
                Object o = ja.get(i);

                if (o != null) {
                    if(StringUtils.isEmpty(o.toString())){
                        valueSet.add(Double.NaN);
                    } else {
                        valueSet.add(Double.valueOf(o.toString()));
                    }
                }
            }
        }
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        jo.put("value", ja);
        Iterator<Double> iter = valueSet.iterator();
        while (iter.hasNext()) {
            ja.put(iter.next());
        }
        return jo;
    }


    @Override
    public void readXML(XMLableReader reader) {

    }

    @Override
    public void writeXML(XMLPrintWriter writer) {

    }

    @Override
    //TODO数值类型结果过滤应该都是按照文本处理的
    public boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader) {

        return true;
    }


}