package com.fr.bi.cal.analyze.session;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.cal.analyze.report.BIReportor;
import com.fr.bi.conf.report.BIReport;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.stable.session.PackAvailableInfo;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;


/**
 * Created by GUY on 2015/4/8.
 */
public abstract class BIAbstractSession extends AbstractSession implements BISessionProvider, PackAvailableInfo, JSONTransform {

    protected BIWeblet let;

    protected BIReport report = new BIReportor();

    protected long accessUserId;

    protected boolean isShareReq;

    public BIAbstractSession(String remoteAddress, BIWeblet let, long userId) {
        super(remoteAddress);
        this.let = let;
        this.accessUserId = userId;
    }


    @Override
    public boolean isPackAvailableForDesign(String packageName) {
        return false;
    }

    @Override
    public boolean isSharedReq() {
        return isShareReq;
    }

    public void setShareReq(boolean isShareReq) {
        this.isShareReq = isShareReq;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        return null;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        report.parseJSON(jo, accessUserId, getLoader());
        this.book2Show = null;
    }

    @Override
    public ICubeDataLoader getLoader() {
        return BICubeManager.getInstance().fetchCubeLoader(accessUserId);
    }

    @Override
    public long getUserId() {
        return accessUserId;
    }

    @Override
    public BIReport getBIReport() {
        return report;
    }
}