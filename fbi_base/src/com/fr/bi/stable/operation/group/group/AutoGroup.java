package com.fr.bi.stable.operation.group.group;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.operation.group.AbstractGroup;
import com.fr.bi.stable.structure.collection.map.CubeLinkedHashMap;
import com.fr.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by GUY on 2015/4/9.
 */
public class AutoGroup extends AbstractGroup {

    private static DecimalFormat nFormat = new DecimalFormat("#.##");
    private int grouplen;
    private double start;
    @BICoreField
    private double interval;
    private transient double max;
    private transient double min;
    @BICoreField
    private boolean hasInterval;

    @Override
    public ICubeColumnIndexReader createGroupedMap(ICubeColumnIndexReader baseMap) {
        double tiMax = ((Number) baseMap.lastKey()).doubleValue();
        double tiMin = ((Number) baseMap.firstKey()).doubleValue();
        if (!hasInterval) {
            max = tiMax;
            min = tiMin;
            interval = (max - min) / 5;
            initGroup();
        }
        CubeLinkedHashMap resultMap = new CubeLinkedHashMap();
        Iterator<Map.Entry<Number, GroupValueIndex>> it = baseMap.iterator();
        while (it.hasNext()) {
            Map.Entry<Number, GroupValueIndex> entry = it.next();
            double key = entry.getKey().doubleValue();
            GroupValueIndex gvi = entry.getValue();
            String groupName = getAutoGroupName(key);
            GroupValueIndex g = (GroupValueIndex) resultMap.get(groupName);
            resultMap.put(groupName, gvi.OR(g));
        }
        return resultMap;
    }

    private String getAutoGroupName(double value) {
        int index = (int) ((value - start) / interval);
        return nFormat.format(start + interval * index) + "-" + nFormat.format(start + interval * (index + 1));
    }

    @Override
    public boolean isNullGroup() {
        return false;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
        if(jo.has("group_value")){
            JSONObject valueJson = jo.optJSONObject("group_value");
            if (valueJson.has("max")) {
                max = valueJson.getDouble("max");
            }
            if (valueJson.has("min")) {
                min = valueJson.getDouble("min");
            }
            if (valueJson.has("group_interval")) {
                hasInterval = true;
                interval = valueJson.getDouble("group_interval");
            } else {
                hasInterval = false;
                interval = (max - min) / 5;
            }
            initGroup();
        }
    }

    private int checkMagnifyCount(double number) {
        String numText = number + "";
        String dotText = numText.split("\\.")[0];
        return dotText.length();
    }

    private void initGroup() {
        int magnify = 1;
        int minCount = this.checkMagnifyCount(this.min);
        int maxCount = this.checkMagnifyCount(this.max);
        int count = minCount > maxCount ? minCount : maxCount;
        StringBuilder minBuilder = new StringBuilder();
        minBuilder.append("0.");
        while (count - minCount > 0) {
            minBuilder.append("0");
            minCount++;
        }
        String min = String.valueOf(this.min);
        min = min.substring(min.indexOf(".")).matches("\\.0+$") ? min.substring(0, min.indexOf(".")) : min.replace(".", "");
        min = minBuilder.append(min).toString();

        StringBuilder maxBuilder = new StringBuilder("0.");
        while (count - maxCount > 0) {
            maxBuilder.append("0");
            maxCount++;
        }
        String max = String.valueOf(this.max);
        max = max.substring(max.indexOf(".")).matches("\\.0+$") ? max.substring(0, max.indexOf(".")) : max.replace(".", "");
        max = maxBuilder.append(max).toString();

        int i = max.length() - 1;
        while (min.charAt(i) == '0' && max.charAt(i) == '0') {
            i--;
        }
        min = min.substring(0, i);
        max = this.min == 0 ? max.substring(0, max.length() - 1) : max.substring(0, i);
        int length = max.length() - 2;
        StringBuilder add = new StringBuilder("0.");
        while (--length > 0) {
            add.append("0");
        }
        add.append("1");
        while (count-- > 0) {
            magnify *= 10;
        }
        double genMin = Double.parseDouble(min) * magnify;
        BigDecimal b1 = new BigDecimal(max);
        BigDecimal b2 = new BigDecimal(add.toString());
        double genMax = b1.add(b2).doubleValue() * magnify;
        if(!hasInterval){
            this.interval = (genMax - genMin) / 5;
        }
        setAutoGroupValue(genMin, genMax, this.interval, this.hasInterval, genMin);
    }

    /**
     * @param min         起始值
     * @param max         最大值
     * @param interval    间隔
     * @param hasInterval 是否有间隔
     * @param start       开始值
     */
    protected void setAutoGroupValue(double min, double max, double interval, boolean hasInterval, double start) {
        int ilen = (int) ((max - min) / interval);
        ilen = ((max - min) == interval * ilen && start == min) ? ilen : ilen + 1;
        this.grouplen = hasInterval ? ilen : 5;
        this.interval = interval;
        this.start = start;
    }
}