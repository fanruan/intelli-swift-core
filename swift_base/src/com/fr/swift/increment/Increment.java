package com.fr.swift.increment;

import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.excel.ExcelDataSource;

/**
 * This class created on 2018-1-15 14:44:16
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface Increment {

    QueryDBSource getIncreaseSource();

    QueryDBSource getDecreaseSource();

    QueryDBSource getModifySource();

    ExcelDataSource getIncreaseExcelSource();

    int getUpdateType();

    /**
     * 更新类型
     */
    final class UPDATE_TYPE {
        public static final int ALL = 0x0;
        public static final int PART = 0x1;
        public static final int NEVER = 0x2;
    }
}
