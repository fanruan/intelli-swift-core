package com.fr.bi.conf.data.source;

import com.fr.bi.stable.data.source.CubeTableSource;

/**
 * This class created on 2016/11/15.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class TableSourceUtils {
    public static boolean isBasicTable(CubeTableSource tableSource) {
        return !(tableSource instanceof ETLTableSource || tableSource instanceof BIOccupiedCubeTableSource);
    }
}
