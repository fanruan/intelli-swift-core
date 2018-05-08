package com.fr.swift.conf;

import com.fr.swift.conf.business.table2source.TableToSource;
import com.fr.swift.conf.business.table2source.unique.TableToSourceUnique;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/3/14
 */
public class Table2SourceCreator {
    public static List<TableToSource> create(int size) {
        List<TableToSource> result = new ArrayList<TableToSource>();
        for (int i = 0; i < size; i++) {
            result.add(new TableToSourceUnique("table" + i, "source" + i));
        }
        return result;
    }

    public static List<TableToSource> modify(int size) {
        List<TableToSource> result = new ArrayList<TableToSource>();
        for (int i = 0; i < size; i++) {
            result.add(new TableToSourceUnique("table" + i, "modifySource" + i));
        }
        return result;
    }
}
