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
    private static Map<Table, ITableSource> storeKey = new HashMap<Table, ITableSource>();
    private static Map<ITableSource, Table> storeData = new HashMap<ITableSource, Table>();

    public static Table createKey(ITableSource source) {
        if (storeData.containsKey(source)) {
            return storeData.get(source);
        }
        Table key = new BITable(UUID.randomUUID().toString());
        storeKey.put(key, source);
        storeData.put(source, key);
        return key;
    }

    public static ITableSource getSource(Table key) {
        if (storeKey.containsKey(key)) {
            return storeKey.get(key);
        }
        return null;
    }

}