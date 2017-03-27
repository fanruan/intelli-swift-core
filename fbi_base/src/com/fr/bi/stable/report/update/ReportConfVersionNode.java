package com.fr.bi.stable.report.update;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.report.update.operation.ReportUpdateOperation;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * Created by kary on 2017/2/4.
 */
public class ReportConfVersionNode implements Comparable<ReportConfVersionNode> {
    private ReportVersion version;
    private List<ReportUpdateOperation> reportOperations;

    public ReportConfVersionNode(ReportVersion version, List<ReportUpdateOperation> reportOperation) {
        this.version = version;
        this.reportOperations = reportOperation;
    }

    public boolean versionCompare(JSONObject settings) {
        try {
            return ComparatorUtils.equals(settings.getString("version"), version.getVersionName());
        } catch (JSONException e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        return false;
    }

    public ReportVersion getVersion() {
        return version;
    }

    public void update(JSONObject settings) throws JSONException {
        for (ReportUpdateOperation operation : reportOperations) {
            operation.update(settings);
        }
    }

    public boolean isLatestVersion() {
        return ComparatorUtils.equals(version, BIReportConstant.VERSION);
    }

    @Override
    public int compareTo(ReportConfVersionNode o) {
        if (this.getVersion().getVersionSort() < o.getVersion().getVersionSort()) {
            return -1;
        }
        return 1;
    }
}
