package com.fr.bi.stable.report.update;

import com.finebi.ProductConstants;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.exception.BIReportVersionAbsentException;
import com.fr.bi.fs.BIDesignSetting;
import com.fr.bi.stable.utils.DateUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Kary on 2017/2/4.
 */
public class ReportSettingUpdateManager {
    private static ReportSettingUpdateManager ourInstance = new ReportSettingUpdateManager();

    public static ReportSettingUpdateManager getInstance() {
        return ourInstance;
    }

    private List<ReportConfVersionNode> versionNodes = new ArrayList<ReportConfVersionNode>();

    //
    protected ReportSettingUpdateManager() {
        initial();
    }

    public BIDesignSetting updateReportSettings(BIDesignSetting setting) throws Exception {
        JSONObject reportSettings = setting.getReportJSON();
        Iterator<ReportConfVersionNode> iterator = versionNodes.iterator();
        while (iterator.hasNext()) {
            ReportConfVersionNode node = iterator.next();
            boolean needUpdate = updateStatusCheck(setting, node);
            if (needUpdate) {
                BILoggerFactory.getLogger(this.getClass()).debug(BIStringUtils.append("profile files is updating ", this.getVersion(setting).getVersion() + "------>" + node.getVersion().getVersion()));
                reportSettings = node.update(reportSettings);
            }
        }
        return new BIDesignSetting(reportSettings.toString());
    }

    public boolean needUpdate(BIDesignSetting setting) throws Exception {
        Iterator<ReportConfVersionNode> iterator = versionNodes.iterator();
        while (iterator.hasNext()) {
            ReportConfVersionNode node = iterator.next();
            boolean needUpdate = updateStatusCheck(setting, node);
            if (needUpdate) {
                return true;
            }
        }
        return false;
    }

    private boolean updateStatusCheck(BIDesignSetting setting, ReportConfVersionNode node) throws BIReportVersionAbsentException, JSONException, ParseException {
        boolean isBeforeLatestVersion = parseValue(getVersion(setting).getVersion()) < parseValue(node.getVersion().getVersion());
        boolean isBeforeLatestDate = !setting.getReportJSON().has("lastModifyTime") || DateUtils.parse(setting.getReportJSON().getString("lastModifyTime")).before(DateUtils.parse(ProductConstants.getReleaseDate()));
        return isBeforeLatestDate || isBeforeLatestVersion;
    }

//    /*
//    * 兼容原来的格式（yyyy-MM-dd）
//    * */
//    private boolean isBeforeReleaseTime(BIDesignSetting setting) throws JSONException, ParseException {
//        if (!setting.getReportJSON().has("lastModifyTime")) {
//            return true;
//        }
//        String datePattern = "yyyy-MM-dd HH:mm:ss";
//        Date lastModifyTime;
//        try {
//            lastModifyTime = DateUtils.parse(setting.getReportJSON().getString("lastModifyTime"), datePattern);
//        } catch (ParseException e) {
//            lastModifyTime = DateUtils.parse(setting.getReportJSON().getString("lastModifyTime"));
//        }
//        Date releaseTime;
//        try {
//            releaseTime = DateUtils.parse(ProductConstants.getReleaseDate(), datePattern);
//        } catch (ParseException e) {
//            releaseTime = DateUtils.parse(ProductConstants.getReleaseDate());
//        }
//        return lastModifyTime.before(releaseTime);
//    }

    /***
     *
     * @return
     * @throws Exception 该版本号无法在历史记录里找到
     * 处理出现未记录版本号的情况
     */
    public ReportVersionEnum getVersion(BIDesignSetting setting) throws BIReportVersionAbsentException, JSONException {
        JSONObject reportSettings = setting.getReportJSON();
        if (null == reportSettings || !reportSettings.has("version")) {
            return ReportVersionEnum.VERSION_4_0;
        }
        for (ReportConfVersionNode node : versionNodes) {
            if (ComparatorUtils.equals(reportSettings.getString("version"), node.getVersion().getVersion())) {
                return node.getVersion();
            }
        }
        throw new BIReportVersionAbsentException("version: " + reportSettings.get("version") + "not found");
    }

    private void initial() {
        try {
            if (null == versionNodes || versionNodes.size() == 0) {
                initialNodeList();
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
    }

    private void initialNodeList() throws Exception {
        versionNodes = ReportVersionNodeFactory.createVersionNodes();
    }

    protected void reSetNodeList(List nodeList) throws Exception {
        this.versionNodes = nodeList;
        Collections.sort(versionNodes);
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
