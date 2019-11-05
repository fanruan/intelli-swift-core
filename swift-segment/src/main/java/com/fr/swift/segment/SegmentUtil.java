package com.fr.swift.segment;

import com.fr.swift.segment.column.Column;
import com.fr.swift.util.IoUtil;

import java.util.Collection;

/**
 * @author anchore
 * @date 2019/11/1
 */
public class SegmentUtil {
    /**
     * 释放历史块内部所有资源
     *
     * @param seg 块
     */
    public static void releaseHisSeg(Segment seg) {
        if (seg != null && seg.isHistory()) {
            IoUtil.release(seg);
        }
    }

    /**
     * 释放历史块内部用到的所有资源
     *
     * @param segs
     */
    public static void releaseHisSeg(Collection<? extends Segment> segs) {
        if (segs == null) {
            return;
        }
        for (Segment seg : segs) {
            releaseHisSeg(seg);
        }
    }

    /**
     * 释放历史块column
     *
     * @param column 列
     */
    public static void releaseHisColumn(Column<?> column) {
        if (column != null && column.getLocation().getStoreType().isPersistent()) {
            IoUtil.release(column.getDetailColumn(), column.getDictionaryEncodedColumn(), column.getBitmapIndex());
        }
    }

    public static void releaseHisColumn(Collection<? extends Column<?>> columns) {
        if (columns == null) {
            return;
        }
        for (Column<?> column : columns) {
            releaseHisColumn(column);
        }
    }

    public static int safeGetRowCount(Segment seg) {
        try {
            return seg.isReadable() ? seg.getRowCount() : 0;
        } finally {
            releaseHisSeg(seg);
        }
    }
}