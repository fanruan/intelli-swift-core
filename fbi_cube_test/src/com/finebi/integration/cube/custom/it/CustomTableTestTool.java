package com.finebi.integration.cube.custom.it;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.conf.data.source.ETLTableSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * Created by Lucifer on 2017-3-20.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class CustomTableTestTool {

    public static void calc(List<String> baseTableSourceIds, List<Integer> updateTypes,
                             Map<CubeTableSource, Set<String>> tableBaseSourceIdMap,
                             Map<String, Set<CubeTableSource>> baseSourceIdTableMap,
                             Map<String, Set<Integer>> baseSourceIdUpdateTypeMap) {
        for (int i = 0; i < baseTableSourceIds.size(); i++) {
            List<BusinessTable> tableList = getTablesContainsSourceId(UserControl.getInstance().getSuperManagerID(), baseTableSourceIds.get(i), updateTypes.get(i));
            List<CubeTableSource> tableSourceList = getTableSourcesFromBusinessTables(tableList, UserControl.getInstance().getSuperManagerID(), baseTableSourceIds.get(i));
            for (CubeTableSource tableSource : tableSourceList) {
                if (tableBaseSourceIdMap.containsKey(tableSource)) {
                    tableBaseSourceIdMap.get(tableSource).add(baseTableSourceIds.get(i));
                } else {
                    tableBaseSourceIdMap.put(tableSource, new HashSet<String>());
                    tableBaseSourceIdMap.get(tableSource).add(baseTableSourceIds.get(i));
                }

                if (baseSourceIdTableMap.containsKey(baseTableSourceIds.get(i))) {
                    baseSourceIdTableMap.get(baseTableSourceIds.get(i)).add(tableSource);
                } else {
                    baseSourceIdTableMap.put(baseTableSourceIds.get(i), new HashSet<CubeTableSource>());
                    baseSourceIdTableMap.get(baseTableSourceIds.get(i)).add(tableSource);
                }

                if (baseSourceIdUpdateTypeMap.containsKey(baseTableSourceIds.get(i))) {
                    baseSourceIdUpdateTypeMap.get(baseTableSourceIds.get(i)).add(updateTypes.get(i));
                } else {
                    baseSourceIdUpdateTypeMap.put(baseTableSourceIds.get(i), new HashSet<Integer>());
                    baseSourceIdUpdateTypeMap.get(baseTableSourceIds.get(i)).add(updateTypes.get(i));
                }

            }

        }
    }

    //相关tableSource。
    private static List<BusinessTable> getTablesContainsSourceId(long userId, String baseTableSourceId, int updateType) {
        List<BusinessTable> tableList = new ArrayList<BusinessTable>();
        for (BusinessTable businessTable : BICubeConfigureCenter.getPackageManager().getAllTables(userId)) {
            List<Set<CubeTableSource>> sourceList = businessTable.getTableSource().createGenerateTablesList();
            for (Set<CubeTableSource> sourceSet : sourceList) {
                Iterator iterator = sourceSet.iterator();
                while (iterator.hasNext()) {
                    CubeTableSource tableSource = (CubeTableSource) iterator.next();
                    if (ComparatorUtils.equals(tableSource.getSourceID(), baseTableSourceId)) {
                        tableList.add(businessTable);
                    }
                }
            }
        }
        return tableList;
    }

    private static List<CubeTableSource> getTableSourcesFromBusinessTables(List<BusinessTable> tableList, long userId, String baseTableSourceId) {
        List<CubeTableSource> tableSources = new ArrayList<CubeTableSource>();
        for (BusinessTable table : tableList) {
            if (table.getTableSource() instanceof ETLTableSource) {
                tableSources.add(table.getTableSource());
            } else {
                tableSources.add(table.getTableSource());
            }
        }
        return removeDuplicateSources(tableSources);
    }

    private static List<CubeTableSource> removeDuplicateSources(List<CubeTableSource> sourceList) {
        List<CubeTableSource> tableSources = new ArrayList<CubeTableSource>();
        Set<String> sourceIds = new HashSet<String>();
        for (CubeTableSource source : sourceList) {
            if (!sourceIds.contains(source.getSourceID())) {
                sourceIds.add(source.getSourceID());
                tableSources.add(source);
            }
        }
        return tableSources;
    }
}
