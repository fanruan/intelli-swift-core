package com.fr.bi.cstwriter;

import com.fr.bi.stable.constant.*;

import java.io.File;

/**
 * Created by 小灰灰 on 2015/10/23.
 */
public class BIConstantWriter {
    private static final Class[] CLS = {BIReportConstant.class, BIJSONConstant.class, BIChartSettingConstant.class, DBConstant.class, FunctionConstant.class};

    private static final Class[] EYLCLS = {com.fr.bi.etl.analysis.Constants.class};

    public static void main(String[] args) throws Exception {
        String path = System.getProperty("user.dir");
        File parent = new File(new File(path).getParent());
        new JSWriter().write(new File(parent, "nuclear-web/fbi_web/src/com/fr/bi/web/js/data/constant/biconst.js"), "BICst", CLS);
        new IOSWriter().write(new File(parent, "nuclear-core/fbi/src/com/fr/bi/cstwriter/biconst.h"), "# define BI", CLS);
        new JSWriter().write(new File(parent, "nuclear-core/fbi_analysis_etl/src/com/fr/bi/etl/analysis/web/js/base/constant/etlconst.js"), "ETLCst", EYLCLS);
    }
}