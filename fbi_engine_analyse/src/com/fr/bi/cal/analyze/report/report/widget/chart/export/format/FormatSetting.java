package com.fr.bi.cal.analyze.report.report.widget.chart.export.format;

import com.fr.json.JSONArray;
import com.fr.json.JSONCreator;
import com.fr.json.JSONParser;

/**
 * Created by Kary on 2017/5/11.
 */
public interface FormatSetting extends JSONCreator,JSONParser {
    String getUnit();

    boolean isNumSeparators();

    int getNumberLevel();

    int getFormat();

    JSONArray getConditions();

    int getIconStyle();

    int getMark();
}
