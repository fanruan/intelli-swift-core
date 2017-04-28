package com.fr.bi.cal.analyze.report.report.styles;

import com.fr.json.JSONParser;

/**
 * Created by Kary on 2017/4/7.
 */
public interface IReportStyle extends JSONParser {
    String getThemeStyle();
    int getWsTableStyle();
}
