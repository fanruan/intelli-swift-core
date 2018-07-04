package com.fr.swift.cube;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.source.DataSource;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class CubeUtil {
    public static boolean isReadable(Segment seg) {
        try {
            seg.getRowCount();
            seg.getAllShowIndex();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isReadable(Column col) {
        try {
            col.getDictionaryEncodedColumn().size();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getTablePath(DataSource dataSource) {
        return String.format("%s/%s",
                dataSource.getMetadata().getSwiftSchema().getDir(),
                dataSource.getSourceKey().getId());
    }
}