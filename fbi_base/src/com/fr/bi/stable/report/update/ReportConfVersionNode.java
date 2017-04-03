package com.fr.bi.stable.report.update;

import com.fr.bi.stable.report.update.operation.ReportUpdateOperation;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * Created by kary on 2017/2/4.
 */
public class ReportConfVersionNode implements Comparable<ReportConfVersionNode> {
    private ReportVersionEnum version;
    private List<ReportUpdateOperation> reportOperations;

    public ReportConfVersionNode(ReportVersionEnum version, List<ReportUpdateOperation> reportOperation) {
        this.version = version;
        this.reportOperations = reportOperation;
    }

    public ReportVersionEnum getVersion() {
        return version;
    }

    public JSONObject update(JSONObject settings) throws JSONException {
        for (ReportUpdateOperation operation : reportOperations) {
            settings = operation.update(settings);
        }
        return settings;
    }

    @Override
    public int compareTo(ReportConfVersionNode o) {

        if (parseValue(this.getVersion().getVersion()) < parseValue(o.getVersion().getVersion())) {
            return -1;
        }
        return 1;
    }

    private double parseValue(String version) {
        String rs = "";
        String[] split = version.split("\\.");
        if (split.length >= 2) {
            rs += split[0] + ".";
            for (int i = 1; i < split.length; i++) {
                rs += split[i];
            }
        } else {
            rs = version;
        }
        return Double.valueOf(rs);
    }

}
