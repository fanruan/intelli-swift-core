package com.fr.bi.stable.data.source;

import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by GUY on 2015/3/3.
 */
public class TableFactory {
    private static Map<Table, ICubeTableSource> storeKey = new HashMap<Table, ICubeTableSource>();
    private static Map<ICubeTableSource, Table> storeData = new HashMap<ICubeTableSource, Table>();

    public static Table createKey(ICubeTableSource source) {
        if (storeData.containsKey(source)) {
            return storeData.get(source);
        }
        Table key = new BITable(UUID.randomUUID().toString());
        storeKey.put(key, source);
        storeData.put(source, key);
        return key;
    }

    public static ICubeTableSource getSource(Table key) {
        if (storeKey.containsKey(key)) {
            return storeKey.get(key);
        }
        return null;
    }

}