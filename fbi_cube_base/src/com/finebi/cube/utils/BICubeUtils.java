package com.finebi.cube.utils;

import com.finebi.cube.structure.*;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.CubeColumnReaderService;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

import java.util.UUID;

/**
 * Created by neil wang on 17/5/7.
 */

public class BICubeUtils {

    public static boolean equals(Object o1, Object o2) {
        return ComparatorUtils.equals(o1, o2);
    }

    public static String UUID() {
        return UUID.randomUUID().toString();
    }

    public static boolean tableExist(ITableKey tableKey, CubeTableEntityGetterService tableEntityGetterService, Cube cube) {
        if (cube.isResourceExist(tableKey)) {
            return tableEntityGetterService.tableDataAvailable();
        }
        return false;
    }

    public static boolean tableRelationExist(ITableKey tableKey, BICubeTablePath relationPath, CubeTableEntityGetterService tableEntityGetterService, Cube cube) {
        if (tableExist(tableKey, tableEntityGetterService, cube)) {
            if (cube.isResourceExist(tableKey, relationPath)) {
                return tableEntityGetterService.relationExists(relationPath);
            }
        }
        return false;
    }

    public static boolean tableFieldRelationExist(ITableKey tableKey, BIColumnKey columnKey, BICubeTablePath relationPath, CubeTableEntityGetterService tableEntityGetterService, Cube cube) {
        CubeColumnReaderService columnReaderService = null;
        CubeRelationEntityGetterService basicTableRelation = null;
        CubeRelationEntityGetterService fieldRelation = null;
        try {
            columnReaderService = tableEntityGetterService.getColumnDataGetter(columnKey);
            basicTableRelation = tableEntityGetterService.getRelationIndexGetter(relationPath);

            if (tableRelationExist(tableKey, relationPath, tableEntityGetterService, cube)) {
                /**
                 * 如果基础关联存在，那么需要判断版本。字段版本，必须晚于基础关联版本
                 */
                long basicRelationVersion = basicTableRelation.getCubeVersion();
                if (columnReaderService.existRelationPath(relationPath)) {
                    fieldRelation = columnReaderService.getRelationIndexGetter(relationPath);
                    long fieldRelationVersion = fieldRelation.getCubeVersion();
                    if (basicRelationVersion > fieldRelationVersion) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                /**
                 * 如果基础关联不存在，那么就依据字段关联自身。
                 */
                return columnReaderService.existRelationPath(relationPath);
            }
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        } finally {
            if (columnReaderService != null) {
                columnReaderService.clear();
            }
            if (basicTableRelation != null) {
                basicTableRelation.clear();
            }
            if (fieldRelation != null) {
                fieldRelation.clear();
            }
        }
    }

}