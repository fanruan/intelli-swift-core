package com.fr.bi.base;

import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by GUY on 2015/3/4.
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML)
public class BIUser implements XMLable, Cloneable {

    public static BIUser DEFALUT = new BIUser(-0l);
    protected BINumberIdentity userId;

    protected BIUser() {
    }

    public BIUser(long userId) {
        this.userId = generateID(userId);
    }

    public long getUserId() {
        return userId.getIdentityValue();
    }

    public void setUserId(long userId) {
        this.userId = generateID(userId);
    }

    public void setUserId(BINumberIdentity userId) {
        this.userId = userId;
    }

    private BINumberIdentity generateID(long userId) {
        return new BINumberIdentity(userId);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BIUser cloned = (BIUser) super.clone();
        cloned.userId = (BINumberIdentity) userId.clone();
        return cloned;
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            userId = generateID(reader.getAttrAsLong("userId", UserControl.getInstance().getSuperManagerID()));
        }
    }

    /**
     * @param writer XML写入对象
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.attr("userId", userId.getIdentityValue());
    }

    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("userId", userId);
        return jo;
    }

    public void parseJSON(JSONObject json) throws Exception {
        userId = generateID(json.optLong("userId", UserControl.getInstance().getSuperManagerID()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BIUser)) {
            return false;
        }

        BIUser biUser = (BIUser) o;

        if (!ComparatorUtils.equals(userId, biUser.userId)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }
}
