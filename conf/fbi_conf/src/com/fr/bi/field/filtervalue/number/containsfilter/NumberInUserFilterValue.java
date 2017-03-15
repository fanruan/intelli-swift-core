package com.fr.bi.field.filtervalue.number.containsfilter;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.json.JSONObject;

/**
 * Created by Young's on 2016/12/19.
 */
public class NumberInUserFilterValue extends NumberContainsFilterValue {

    @BICoreField
    protected String fieldId;

    private long userId;

    @BICoreField
    private String CLASS_TYPE = "NumberInUserFilterValue";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumberInUserFilterValue that = (NumberInUserFilterValue) o;

        if (userId != that.userId) return false;
        if (fieldId != null ? !fieldId.equals(that.fieldId) : that.fieldId != null) return false;
        return CLASS_TYPE != null ? CLASS_TYPE.equals(that.CLASS_TYPE) : that.CLASS_TYPE == null;
    }

    @Override
    public int hashCode() {
        int result = fieldId != null ? fieldId.hashCode() : 0;
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + (CLASS_TYPE != null ? CLASS_TYPE.hashCode() : 0);
        return result;
    }

    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        addLogUserInfo();
        return super.createFilterIndex(dimension, target, loader, userId);
    }

    private void addLogUserInfo() {
        BusinessField field = BIModuleUtils.getBusinessFieldById(new BIFieldID(fieldId));
        if (field != null && BIConfigureManagerCenter.getCubeConfManager().getLoginField() != null) {
            try {
                Object[] values = BIConfigureManagerCenter.getCubeConfManager().getLoginFieldValue(field, userId);
                if (values != null) {
                    for (int i = 0; i < values.length; i++) {
                        if (values[i] != null) {
                            valueSet.add(((Long) values[i]).doubleValue());
                        }
                    }
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        this.userId = userId;
        if (jo.has("filter_value")) {
            this.fieldId = jo.getString("filter_value");
        }
    }
}
