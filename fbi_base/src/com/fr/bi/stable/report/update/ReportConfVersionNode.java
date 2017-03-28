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

    public void update(JSONObject settings) throws JSONException {
        for (ReportUpdateOperation operation : reportOperations) {
            operation.update(settings);
        }
    }

    @Override
    public int compareTo(ReportConfVersionNode o) {
        if (this.getVersion().getVersion() < o.getVersion().getVersion()) {
            return -1;
        }
        return 1;
    }
}
