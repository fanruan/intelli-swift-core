package com.fr.bi.stable.report.update;

import com.fr.bi.exception.BIReportVersionAbsentException;
import com.fr.bi.fs.BIDesignSetting;
import com.fr.bi.stable.report.update.tool.ReportConfUpdateManager4Test;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import junit.framework.TestCase;

/**
 * Created by Kary on 2017/2/4.
 */
public class ReportSettingUpdateManagerTest extends TestCase {
    private ReportSettingUpdateManager manager = new ReportConfUpdateManager4Test();

    public ReportSettingUpdateManagerTest() throws Exception {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initial();
    }

    private void initial() throws Exception {
    }

    public void testVersionCheck() throws Exception {
        assertEquals(manager.getVersion(getDesignSetting(1.0)).getVersion(), getVersionByValue(1.0));
        assertEquals(manager.getVersion(getDesignSetting(2.0)).getVersion(), getVersionByValue(2.0));
        assertEquals(manager.getVersion(new BIDesignSetting("{}")).getVersion(), getVersionByValue(4.0));
        //版本号找不到时会抛出异常
        try {
            manager.getVersion(getDesignSetting(3.7));
            assertTrue(false);
        } catch (BIReportVersionAbsentException e) {
            assertTrue(true);
        }
    }

    public void testUpdate() throws Exception {
        JSONObject settingsA = manager.updateReportSettings(getDesignSetting(0.0)).getReportJSON();
        assertTrue(settingsA.has("version1.0") && settingsA.has("version2.0") && settingsA.has("version3.0"));
        JSONObject settingsB = manager.updateReportSettings(getDesignSetting(1.0)).getReportJSON();
        assertTrue(settingsB.has("version2.0") && settingsB.has("version3.0"));
        JSONObject settingsC = manager.updateReportSettings(getDesignSetting(1.0)).getReportJSON();
        assertTrue(settingsC.has("version3.0"));
    }

    private BIDesignSetting getDesignSetting(Double version) throws JSONException {
        String str = "{\"version\":\"" + version + "\"}";
        return new BIDesignSetting(str);
    }

    private String getVersionByValue(double versionValue) {
        return String.valueOf(versionValue);
    }
}
