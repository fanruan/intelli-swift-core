package com.fr.bi.fs;

import com.fr.data.dao.DAOBean;
import com.fr.json.JSONObject;

public class BISharedReportNode extends DAOBean {
    private long reportId;
    private long createBy;
    private long shareTo;
    private String createByName;
    private String shareToName;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public long getReportId() {
        return reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

    public long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(long createBy) {
        this.createBy = createBy;
    }

    public long getShareTo() {
        return shareTo;
    }

    public void setShareTo(long shareTo) {
        this.shareTo = shareTo;
    }

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }

    public String getShareToName() {
        return shareToName;
    }

    public void setShareToName(String shareToName) {
        this.shareToName = shareToName;
    }

    public BISharedReportNode(long id) {
        this.id = id;
    }

    @Override
    protected int hashCode4Properties() {
        return 0;
    }

    @Override
    public boolean equals4Properties(Object o) {
        return false;
    }

    public BISharedReportNode(){

    }

    public BISharedReportNode(long reportId, String createBy, String shareTo) {
        this.reportId = reportId;
        this.createByName = createBy;
        this.shareToName = shareTo;
    }

    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("reportId")) {
            this.reportId = jo.getLong("reportId");
        }
        if (jo.has("createByName")) {
            this.createByName = jo.getString("createByName");
        }
        if (jo.has("shareTo")) {
            this.shareToName = jo.getString("shareToName");
        }
    }

    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("reportId", this.reportId);
        jo.put("createByName", this.createByName);
        jo.put("shareToName", this.shareToName);
        return jo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BISharedReportNode that = (BISharedReportNode) o;

        if (reportId != that.reportId) return false;
        if (createBy != that.createBy) return false;
        if (shareTo != that.shareTo) return false;
        if (createByName != null ? !createByName.equals(that.createByName) : that.createByName != null) return false;
        return shareToName != null ? shareToName.equals(that.shareToName) : that.shareToName == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (reportId ^ (reportId >>> 32));
        result = 31 * result + (int) (createBy ^ (createBy >>> 32));
        result = 31 * result + (int) (shareTo ^ (shareTo >>> 32));
        result = 31 * result + (createByName != null ? createByName.hashCode() : 0);
        result = 31 * result + (shareToName != null ? shareToName.hashCode() : 0);
        return result;
    }
}