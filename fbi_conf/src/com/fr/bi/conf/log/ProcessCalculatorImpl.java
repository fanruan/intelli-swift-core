package com.fr.bi.conf.log;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.Collection;
import java.util.Map;

/**
 * This class created on 17-2-14.
 *
 * @author Neil Wang
 * @since Advanced FineBI Analysis 1.0
 * <p>
 * 进度条计算: cube准备,每个table transport, 每个column index builder,
 * 每个relation和path生成,跟最后替换新的cube,都占均等的等份.
 */
public class ProcessCalculatorImpl implements ProcessCalculator {

    public double calculateProcess(JSONObject recordJson) {

        long cube_start = 0L;
        long cube_end = 0L;
        JSONObject allTableinfo;
        JSONArray allRelationPathSet;
        JSONArray generatedTable;
        JSONArray generatedRelationAndPath;
        try {
            if (recordJson.has("cube_start")) {
                cube_start = recordJson.optLong("cube_start");
            }
            if (recordJson.has("cube_end")) {
                cube_end = recordJson.optLong("cube_end");
            }

            allTableinfo = recordJson.optJSONObject("allTableInfo");
            allRelationPathSet = recordJson.optJSONArray("allRelationInfo");

            generatedTable = recordJson.optJSONArray("tables");
            generatedRelationAndPath = recordJson.optJSONArray("connections");

            return calculateRate(cube_start, cube_end, allTableinfo, allRelationPathSet, generatedTable, generatedRelationAndPath);
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            return 0;
        }
    }

    public double calculateRate(long cube_start, long cube_end, JSONObject allTableInfo, JSONArray allRelationPathSet
            , JSONArray generatedTable, JSONArray generatedRelationAndPath) {
        int allDivisions = getAllDivisions(allTableInfo, allRelationPathSet);
        int divisionsDone = getDivisionsDone(cube_start, cube_end, generatedTable, generatedRelationAndPath);
        return (double) divisionsDone / allDivisions;
    }

    private int getAllDivisions(JSONObject allTableinfo, JSONArray allRelationPathSet) {
        Map<String, Object> columns = allTableinfo.toMap();
        Collection collection = columns.values();
        int columnSize = 0;

        for (Object object : collection) {
            columnSize += (Integer) object;
        }

        return allTableinfo.length() + allRelationPathSet.length() + columnSize + 2;
    }

    private int getDivisionsDone(long cube_start, long cube_end, JSONArray generatedTable, JSONArray generatedRelationAndPath) {
        int divisionsDone = 0;
        try {
            if (cube_start > 0) {
                divisionsDone++;
            }

            int generatedColumnSize = 0;
            for (int i = 0; i < generatedTable.length(); i++) {
                JSONObject column = ((JSONObject) generatedTable.opt(i));
                int columnSize = ((JSONArray) column.get("column")).length();
                generatedColumnSize += columnSize;
            }

            divisionsDone = divisionsDone + generatedTable.length() + generatedRelationAndPath.length() + generatedColumnSize;

            if (cube_end > 0) {
                divisionsDone++;
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        return divisionsDone;
    }

}
