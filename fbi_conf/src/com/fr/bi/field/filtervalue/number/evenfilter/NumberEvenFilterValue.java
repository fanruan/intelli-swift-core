/**
 *
 */
package com.fr.bi.field.filtervalue.number.evenfilter;

import com.fr.bi.base.BIUser;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.report.widget.BIDataColumn;
import com.fr.bi.conf.report.widget.field.filtervalue.number.NumberFilterValue;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.operation.group.data.number.NumberGroupInfo;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;


public abstract class NumberEvenFilterValue implements NumberFilterValue {
    private static final long serialVersionUID = 4162890826727140538L;
    protected BIDataColumn column = null;
    protected BIUser user;
    /**
     * default 0.0
     */
    protected double V = 0.0;


    /**
     * 创建索引
     *
     * @return 索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, Table target, ICubeDataLoader loader, long userId) {
        if (this.column != null && BIConfigureManagerCenter.getUserLoginInformationManager().getUserInfoManager(user.getUserId()).getAnylysisUserInfo() != null) {
            try {
                User frUser = UserControl.getInstance().getUser(user.getUserId());
                Object fieldValue = BIConfigureManagerCenter.getUserLoginInformationManager().getUserInfoManager(user.getUserId()).getAnylysisUserInfo().getFieldValue(frUser.getUsername(), column.createColumnKey(), loader);
                if (fieldValue != null) {
                    V = (Double) fieldValue;
                }
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        NumberGroupInfo gi = NumberGroupInfo.createGroupInfo(V, true, V, true);
        ICubeTableService ti = loader.getTableIndex(dimension.getField().getTableBelongTo());
        if (dimension.getRelationList() == null) {
            return ti.getAllShowIndex();
        }
        GroupValueIndex gvi = GVIFactory.createAllEmptyIndexGVI();
        Iterator it = dimension.createValueMapIterator(target, loader);
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Number v = (Number) entry.getKey();
            GroupValueIndex g = (GroupValueIndex) entry.getValue();
            if (isMatchValue(v.doubleValue())) {
                if (gvi == null) {
                    gvi = g;
                } else {
                    gvi = gvi.OR(g);
                }
            }
        }
        return gvi;
    }

    /**
     * 是否符合条件
     *
     * @param value 值
     * @return true或false
     */
    @Override
    public boolean isMatchValue(Number value) {
        if (value == null) {
            return false;
        }
        BigDecimal v1 = new BigDecimal(value.doubleValue());
        BigDecimal v2 = new BigDecimal(V);
        return v1.compareTo(v2) == 0;
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
        if (jo.get("filter_value") instanceof JSONArray) {
            JSONArray ja = jo.getJSONArray("filter_value");
            if (ja.length() > 0) {
                JSONObject jsonObject = ja.getJSONObject(0);
                BIDataColumn column = null;
                column = new BIDataColumn();
                column.parseJSON(jsonObject);
                this.column = column;
                this.user = new BIUser(userId);
            }
        } else {
            try {
                this.V = jo.getDouble("filter_value");
            } catch (Exception e) {
                this.V = 0L;
            }
        }
    }

    /**
     * 创建json对象
     *
     * @return json对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        return new JSONObject();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @param reader XML读取对象
     * @see com.fr.stable.xml.XMLableReader
     */
    @Override
    public void readXML(XMLableReader reader) {

    }

    /**
     * @param writer XML写入对象
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {

    }

    /**
     * hash值
     *
     * @return hash值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = this.getClass().getName().hashCode();
        result = prime * result + (int) V;
        return result;
    }

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
        NumberEvenFilterValue other = (NumberEvenFilterValue) obj;
        BigDecimal v1 = new BigDecimal(other.V);
        BigDecimal v2 = new BigDecimal(V);

        if (v1.compareTo(v2) != 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canCreateFilterIndex() {
        return true;
    }


}