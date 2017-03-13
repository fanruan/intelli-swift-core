package com.fr.bi.conf.log;

import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * This class created on 17-2-16.
 *
 * @author Neil Wang
 * @since Advanced FineBI Analysis 1.0
 */
public interface ProcessCalculator {

    double calculateProcess(JSONObject recordJson);

    double calculateRate(long cube_start, long cube_end, JSONObject allTableInfo, JSONArray allRelationPathSet
            , JSONArray generatedTable, JSONArray generatedRelationAndPath);

}
