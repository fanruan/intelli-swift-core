package com.fr.bi.field.dimension;

import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.dimension.dimension.BIDateDimension;
import com.fr.bi.field.dimension.dimension.BINumberDimension;
import com.fr.bi.field.dimension.dimension.BIStringDimension;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONObject;

/**
 * Created by GUY on 2015/4/8.
 */
public class BIDimensionFactory {
    public static BIDimension parseDimension(JSONObject jo, long userId) throws Exception {
        BIDimension dimension = null;
        if (jo.has("type")) {
            int type = jo.getInt("type");
            int subtype = 0;
            try {
                subtype = jo.getInt("subtype");
            } catch (Exception ex) {

            }
            type += subtype;
            switch (type) {
                case BIReportConstant.TARGET_TYPE.STRING: {
                    dimension = new BIStringDimension();
                    break;
                }
                case BIReportConstant.TARGET_TYPE.NUMBER: {
                    dimension = new BINumberDimension();
                    break;
                }
                case BIReportConstant.TARGET_TYPE.DATE: {
                    dimension = new BIDateDimension();
                    break;
                }
            }
            if (dimension != null) {
                dimension.parseJSON(jo, userId);
            }
        }
        return dimension;
    }
}