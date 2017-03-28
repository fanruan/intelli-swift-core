package com.fr.bi.stable.report.update.operation;

import com.fr.general.ComparatorUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import junit.framework.TestCase;

/**
 * Created by Kary on 2017/3/27.
 */
public class ReportReportKeyChangeOperationTest extends TestCase {
    public ReportKeyChangeOperation operation;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        operation = createSettingKeyChangeOperation();
    }

    public void testConvertOperation() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("src_", "src");
        jsonObject.put("field_id", "field_id");
        operation.update(jsonObject);
        assertTrue(jsonObject.has("field_id"));
        assertTrue(jsonObject.has("src_"));
    }

    public void testConvert() throws Exception {
        updateKeys("field_id", "fieldId");
    }

    private void updateKeys(String oldKey, String expectedResult) throws JSONException {
        String newKey = operation.updateKey(oldKey);
        assertTrue(ComparatorUtils.equals(expectedResult, newKey));
    }

    private ReportKeyChangeOperation createSettingKeyChangeOperation() throws JSONException {
        JSONObject keys = new JSONObject();
        keys.put("field_id", "fieldId");
        keys.put("src_", "src");
        return new ReportKeyChangeOperation(keys);
    }
}
