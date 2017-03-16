package com.fr.bi.conf.data;

import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;


public class BIRoleAccessability implements XMLable {
    public static final int ROLE_TYPE_COMPANY = 0;
    public static final int ROLE_TYPE_CUSTOM = 1;
    /**
     *
     */
    private static final long serialVersionUID = -3495909311641613617L;
    private int roleType = ROLE_TYPE_COMPANY;
    private long roleId;
    private String columnFilters; // json格式的字符串

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((columnFilters == null) ? 0 : columnFilters.hashCode());
        result = prime * result + (int) (roleId ^ (roleId >>> 32));
        result = prime * result + roleType;
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BIRoleAccessability other = (BIRoleAccessability) obj;
        if (columnFilters == null) {
            if (other.columnFilters != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(columnFilters, other.columnFilters)) {
            return false;
        }
        if (roleId != other.roleId){
            return false;
        }
        if (roleType != other.roleType){
            return false;
        }
        return true;
    }

    public JSONObject asJson() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("roleType", this.roleType);
        jo.put("roleId", this.roleId);
        //TODO
        jo.put("condition_string_array", this.columnFilters);
        return jo;
    }

    /*
     * jo:{"roleType":0,"roleId":10,"conditions":{}}
     */
    public void parseJson(JSONObject jo) throws JSONException {
        this.roleType = jo.getInt("roleType");
        this.roleId = jo.getInt("roleId");
        if (jo.has("conditions")) {
            Object o = jo.get("conditions");
            if (o instanceof JSONArray) {
                //TODO & FIXME 兼容
            } else {
                this.columnFilters = o.toString();
            }
        } else {
            this.columnFilters = null;
        }
    }

    public int getType() {
        return roleType;
    }

    public long getRoleId() {
        return roleId;
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            this.roleType = reader.getAttrAsInt("roleType", ROLE_TYPE_COMPANY);
            this.roleId = reader.getAttrAsLong("roleId", 0L);
        } else if (reader.isChildNode()) {
            if ("FilterG".equals(reader.getTagName())) {
                this.columnFilters = reader.getElementValue();
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.attr("roleType", this.roleType).attr("roleId", this.roleId);
        if (this.columnFilters != null) {
            writer.startTAG("FilterG");
            writer.textNode(this.columnFilters);
            writer.end();
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BIRoleAccessability cloned = (BIRoleAccessability) super.clone();

        return cloned;
    }

    public JSONObject createFilterJSONCollection() throws JSONException {
        if (columnFilters != null) {
            return new JSONObject(columnFilters);
        } else {
            return new JSONObject();
        }
    }
}