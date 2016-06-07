package com.fr.bi.cstwriter;

import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.constant.FunctionConstant;
import org.apache.commons.el.Constants;

/**
 * Created by 小灰灰 on 2015/10/23.
 */
public class BIConstantWriter {
    private static final Class[] CLS = {BIReportConstant.class, BIJSONConstant.class, DBConstant.class, FunctionConstant.class};

    private static final Class[] EYLCLS = {Constants.class};

    public static void main(String[] args) throws Exception{
        new JSWriter().write("fbi_web/src/com/fr/bi/web/js/data/constant/biconst.js", "BICst", CLS);
        new IOSWriter().write("fbi/src/com/fr/bi/cstwriter/biconst.h", "# define BI", CLS);
        new JSWriter().write("fbi_analysis_etl/src/com/fr/bi/etl/analysis/web/js/base/constant/etlconst.js", "ETLCst", EYLCLS);
    }
}