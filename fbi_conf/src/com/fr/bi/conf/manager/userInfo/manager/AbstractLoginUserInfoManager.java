package com.fr.bi.conf.manager.userInfo.manager;

import com.fr.bi.conf.manager.userInfo.BILoginUserInfo;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.inter.Release;
import com.fr.general.ComparatorUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by GUY on 2015/3/27.
 */
public abstract class AbstractLoginUserInfoManager implements Release, XMLable {

    /**
     *
     */
    private static final long serialVersionUID = 7534859754025727534L;
    private static final String XML_TAG = "UserInfoManager";
    private BILoginUserInfo anylysis = new BILoginUserInfo();
    private BILoginUserInfo current = new BILoginUserInfo();
    protected BIUser biUser;
    protected AbstractLoginUserInfoManager(long userId) {
        biUser = new BIUser(userId);
    }

    public BILoginUserInfo getAnylysisUserInfo() {
        synchronized (anylysis) {
            return anylysis;
        }
    }

    public void setAnylysisUserInfo(BILoginUserInfo anylysis) {
        synchronized (this.anylysis) {
            this.anylysis = anylysis;
        }
    }

    public BILoginUserInfo getCurrentUserInfo() {
        synchronized (current) {
            return current;
        }
    }

    public void setCurrentUserInfo(BILoginUserInfo current) {
        synchronized (this.current) {
            this.current = current;
        }
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            String tag_name = reader.getTagName();
            if (ComparatorUtils.equals(tag_name, "anylysis")) {
                reader.readXMLObject(anylysis);
            } else if (ComparatorUtils.equals(tag_name, "current")) {
                reader.readXMLObject(current);

            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);

        writer.startTAG("anylysis");
        anylysis.writeXML(writer);
        writer.end();

        writer.startTAG("current");
        current.writeXML(writer);
        writer.end();

        writer.end();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void clear() {
        anylysis = null;
        current = null;
    }

    public int compareLoginUserInfo(BILoginUserInfo generatingUserInfo) {
        return ComparatorUtils.equals(generatingUserInfo, current) ? 0 : 1;
    }

    public int compareLoginUserInfo() {
        return ComparatorUtils.equals(anylysis, current) ? 0 : 1;
    }
}