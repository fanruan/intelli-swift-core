package com.fr.bi.stable.report.update;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.report.update.operation.ReportSettingsUpdateOperation;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by kary on 2017/2/4.
 */
public class ReportConfVersionNode implements Comparable<ReportConfVersionNode> {
    private ReportVersion version;
    private ReportSettingsUpdateOperation reportOperation;

    public ReportConfVersionNode(ReportVersion version, ReportSettingsUpdateOperation reportOperation) {
        this.version = version;
        this.reportOperation = reportOperation;
    }

    public boolean versionCompare(JSONObject settings) {
        try {
            return ComparatorUtils.equals(settings.getString("version") ,version.getVersionName());
        } catch (JSONException e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        return false;
    }

    public ReportVersion getVersion() {
        return version;
    }

    public boolean isLatestVersion() {
        return ComparatorUtils.equals(version, BIReportConstant.VERSION);
    }

    public ReportSettingsUpdateOperation getReportOperation() {
        return reportOperation;
    }

    @Override
    public int compareTo(ReportConfVersionNode o) {
        if (this.getVersion().getVersionSort() < o.getVersion().getVersionSort()) {
            return -1;
        }
        return 1;
    }
}
