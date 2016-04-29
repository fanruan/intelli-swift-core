package com.fr.bi.stable.operation.group;


import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.BICoreService;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by GUY on 2015/2/14.
 */
public abstract class AbstractGroup implements IGroup, BICoreService {
    @BICoreField
    protected int type = BIReportConstant.GROUP.NO_GROUP;

    @Override
    public int getType() {
        return type;
    }


    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("type", this.type);
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("type")) {
            type = jo.optInt("type");
        }
    }

    @Override
    public void readXML(XMLableReader reader) {

    }

    @Override
    public void writeXML(XMLPrintWriter writer) {

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + type;
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
        AbstractGroup other = (AbstractGroup) obj;
        if (!ComparatorUtils.equals(type, other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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