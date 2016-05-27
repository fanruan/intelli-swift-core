package com.fr.bi.field.filtervalue.number.rangefilter;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.report.filter.NullFilterDealer;
import com.fr.bi.conf.report.widget.field.filtervalue.AbstractFilterValue;
import com.fr.bi.conf.report.widget.field.filtervalue.number.NumberFilterValue;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.operation.group.data.number.NumberGroupInfo;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.Iterator;
import java.util.Map;

public abstract class NumberRangeFilterValue extends AbstractFilterValue<Number> implements NumberFilterValue, NullFilterDealer {
    /**
     *
     */
    private static final long serialVersionUID = 2172074040987661868L;

    private static String XML_TAG = "NumberRangeFilterValue";

    @BICoreField
    protected double min;
    @BICoreField
    protected boolean closemin;
    @BICoreField
    protected double max;
    @BICoreField
    protected boolean closemax;
    @Override
    public boolean isTopOrBottomFilterValue() {
        return false;
    }

    /**
     * 解析json
     *
     * @param jo     json对象
     * @param userId 用户id
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        if (jo.has("filter_value")) {
            JSONObject value = jo.getJSONObject("filter_value");
            if (value.has("min")) {
                try {
                    min = value.getDouble("min");
                } catch (Exception e) {
                    min = Double.NEGATIVE_INFINITY;
                }
            } else {
                min = Double.NEGATIVE_INFINITY;
            }
            if (value.has("closemin")) {
                this.closemin = value.getBoolean("closemin");
            }
            if (value.has("max")) {
                try {
                    max = value.getDouble("max");
                } catch (Exception e) {
                    max = Double.POSITIVE_INFINITY;
                }

            } else {
                max = Double.POSITIVE_INFINITY;
            }
            if (value.has("closemax")) {
                this.closemax = value.getBoolean("closemax");
            }
        }
    }


    /**
     * 创建json
     *
     * @return json对象
     * @throws Exception 报错
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();

        if (min == Double.NEGATIVE_INFINITY) {
            jo.put("min", this.min);
            jo.put("closemin", this.closemin);
        }

        if (max == Double.POSITIVE_INFINITY) {
            jo.put("max", this.max);
            jo.put("closemax", this.closemax);
        }
        JSONObject resjo = new JSONObject();
        resjo.put("filter_value", jo);
        return resjo;
    }

    /**
     * @param reader XML读取对象
     * @see com.fr.stable.xml.XMLableReader
     */
    @Override
    public void readXML(XMLableReader reader) {
        if (reader.getTagName().equals("value")) {

            try {
                JSONObject jo = new JSONObject(reader.getAttrAsString("value", StringUtils.EMPTY));
                this.parseJSON(jo, UserControl.getInstance().getSuperManagerID());
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }

    /**
     * @param writer XML写入对象
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);

        try {
            writer.attr("value", createJSON().toString());
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        writer.end();
    }

    /**
     * 获取过滤后的索引
     *
     * @return 过滤索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, CubeTableSource target, ICubeDataLoader loader, long userId) {
        if (min > max) {
            return GVIFactory.createAllEmptyIndexGVI();
        }
        NumberGroupInfo gi = NumberGroupInfo.createGroupInfo(min, closemin, max, closemax);
        ICubeTableService ti = loader.getTableIndex(dimension.getField().getTableBelongTo());
        if (dimension.getRelationList() == null) {
            return ti.getAllShowIndex();
        }
        Iterator it = dimension.createValueMapIterator(target, loader);
        GroupValueIndex gvi = null;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Number v = (Number) entry.getKey();
            GroupValueIndex g = (GroupValueIndex) entry.getValue();
            if (gi.contains(v.doubleValue())) {
                if (gvi == null) {
                    gvi = g;
                } else {
                    gvi = gvi.OR(g);
                }
            }
        }
        if (gvi == null) {
            return GVIFactory.createAllEmptyIndexGVI();
        }
        return gvi;
    }


    @Override
    public boolean isMatchValue(Number value) {
        if (value == null) {
            return dealWithNullValue();
        }
        return matchValue(value.doubleValue());
    }
//    /**
//     * 是否符合条件
//     *
//     * @param value 值
//     * @return true或false
//     */
//    @Override
//    public boolean isMatchValue(double value) {
//        boolean flag1 = false, flag2 = false;
//        if (closemin) {
//            flag1 = value >= min;
//        } else {
//            flag1 = value > min;
//        }
//        if (closemax) {
//            flag2 = value <= max;
//        } else {
//            flag2 = value < max;
//        }
//        return flag1 && flag2;
//    }
//    

    /**
     * 是否显示记录
     *
     * @param node      节点
     * @param targetKey 指标信息
     * @return 是否显示
     */
    @Override
    public boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
        Number targetValue = node.getSummaryValue(targetKey);
        if (targetValue == null) {
            return dealWithNullValue();
        }
        return matchValue(targetValue.doubleValue());
    }


    protected abstract boolean matchValue(double v);

    /**
     * 重写code
     *
     * @return 数值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = this.getClass().getName().hashCode();
        result = prime * result + (closemax ? 1231 : 1237);
        result = prime * result + (closemin ? 1231 : 1237);
        long temp;
        temp = Double.doubleToLongBits(max);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(min);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        NumberRangeFilterValue other = (NumberRangeFilterValue) obj;
        if (closemax != other.closemax) {
            return false;
        }
        if (closemin != other.closemin) {
            return false;
        }
        if (Double.doubleToLongBits(max) != Double.doubleToLongBits(other.max)) {
            return false;
        }

        if (Double.doubleToLongBits(min) != Double.doubleToLongBits(other.min)) {
            return false;
        }
        return true;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean canCreateFilterIndex() {
        return true;
    }

}