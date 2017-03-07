package com.fr.bi.stable.operation.group;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONObject;
import junit.framework.TestCase;

/**
 * Created by 小灰灰 on 2017/3/6.
 */
public class BIGroupUtilsTest extends TestCase{
    public void testIsCustomGroup(){
        assertEquals(true, BIGroupUtils.isCustomGroup(createGroupByType(BIReportConstant.GROUP.CUSTOM_GROUP)));
        assertEquals(true, BIGroupUtils.isCustomGroup(createGroupByType(BIReportConstant.GROUP.CUSTOM_NUMBER_GROUP)));
        assertEquals(true, BIGroupUtils.isCustomGroup(createGroupByType(BIReportConstant.GROUP.AUTO_GROUP)));
        assertEquals(false, BIGroupUtils.isCustomGroup(createGroupByType(BIReportConstant.GROUP.ID_GROUP)));
        assertEquals(false, BIGroupUtils.isCustomGroup(createGroupByType(BIReportConstant.GROUP.S)));
        assertEquals(false, BIGroupUtils.isCustomGroup(createGroupByType(BIReportConstant.GROUP.NO_GROUP)));
    }

    private IGroup createGroupByType(int type){
        JSONObject jo = JSONObject.create();
        try {
            jo.put("type", type);
            return BIGroupFactory.parseGroup(jo);
        } catch (Exception e) {

        }
        return null;
    }
}
