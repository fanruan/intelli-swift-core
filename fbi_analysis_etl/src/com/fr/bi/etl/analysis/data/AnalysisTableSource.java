package com.fr.bi.etl.analysis.data;

import com.fr.bi.stable.data.source.ITableSource;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public interface AnalysisTableSource extends ITableSource {
    UserTableSource createUserTableSource(long userId);
}