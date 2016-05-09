package com.fr.bi.conf.data.source.operator.create;

import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.BICoreService;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.operation.group.BIGroupFactory;
import com.fr.bi.stable.operation.group.IGroup;
import com.fr.bi.stable.utils.BIIDUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

/**
 * Created by 小灰灰 on 2015/7/9.
 */
public class SumByGroupDimension implements JSONTransform, BICoreService {
    @BICoreField
    private String name;
    @BICoreField
    private IGroup group;
    @BICoreField
    private String nameText;

    @Override
    public JSONObject createJSON() throws Exception {
        return null;
    }

    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {
        if(jsonObject.has("name")){
            this.nameText = jsonObject.optString("name");
        }
        if(jsonObject.has("group")){
            this.group = BIGroupFactory.parseGroup(jsonObject.optJSONObject("group"));
        }
        if(jsonObject.has("_src")){
            JSONObject jo = jsonObject.optJSONObject("_src");
            if (jo.has("field_id")){
                this.name = BIIDUtils.getFieldNameFromFieldID(jo.getString("field_id"));
            } else if (jo.has("field_name")){
                this.name = jo.getString("field_name");
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getNameText() {
        return nameText;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IGroup getGroup() {
        return group;
    }

    public void setGroup(IGroup group) {
        this.group = group;
    }

    public Object getKeyValue(Object value) {
        return value == null ? null : getKeyValueByType(value);
    }

    private Object getKeyValueByType(Object value){
        Object obj;
        switch (group.getType()){
            case BIReportConstant.GROUP.M:
                obj = ((Integer) value).longValue() + 1;
                break;
            case BIReportConstant.GROUP.Y:
            case BIReportConstant.GROUP.S:
            case BIReportConstant.GROUP.W:
            case BIReportConstant.GROUP.MD:
                obj = ((Integer) value).longValue();
                break;
            default:
                obj = value;
                break;
        }
        return obj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SumByGroupDimension)) {
            return false;
        }

        SumByGroupDimension that = (SumByGroupDimension) o;

        if (group != null ? !ComparatorUtils.equals(group, that.group) : that.group != null) {
            return false;
        }
        if (name != null ? !ComparatorUtils.equals(name, that.name) : that.name != null) {
            return false;
        }
        if (nameText != null ? !ComparatorUtils.equals(nameText, that.nameText) : that.nameText != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (nameText != null ? nameText.hashCode() : 0);
        return result;
    }

    public BIKey createKey() {
        if (isDateGroup()){
            return new IndexTypeKey(getName(), getGroup().getType());
        }
        return new IndexKey(getName());
    }

    private boolean isDateGroup() {
        return group !=null && (group.getType() == BIReportConstant.GROUP.Y
                || group.getType() == BIReportConstant.GROUP.M
                || group.getType() == BIReportConstant.GROUP.S
                || group.getType() == BIReportConstant.GROUP.W
                || group.getType() == BIReportConstant.GROUP.YMD);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SumByGroupDimension{");
        sb.append("name='").append(name).append('\'');
        JSONObject gJson = null;
        try {
            gJson = group.createJSON();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage());
        }
        sb.append(", group=").append(gJson);
        sb.append(", nameText='").append(nameText).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public BICore fetchObjectCore() {

        try {
            return new BICoreGenerator(this).fetchObjectCore();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return BIBasicCore.EMPTY_CORE;
    }
}