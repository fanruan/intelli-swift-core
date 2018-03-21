package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.dashboard.widget.table.StringControlWidget;
import com.finebi.conf.structure.result.BIStringDetailResult;
import com.fr.swift.cal.Query;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.builder.QueryBuilder;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * @author anchore
 * @date 2018/3/6
 * 下拉文本
 */
public class DropdownStringWidgetAdaptor {
    static BIStringDetailResult caculate(StringControlWidget widget) throws SQLException {
        Query query = QueryBuilder.buildQuery(buildQueryInfo(widget));
        SwiftResultSet result = query.getQueryResult();
        return /*new RowSet(result)*/null;
    }

    static QueryInfo buildQueryInfo(StringControlWidget widget) {
        List<String> selectedValues = widget.getSelectedValues();
//        String queryWords = widget.getKeywords();
        int clickMore = widget.getTimes();
        FilterInfo filterInfo = null;

        return null;
    }

//    private static class RowSet implements BIStringDetailResult {
//        static final int DEFAULT_SIZE = 100;
//
//        SwiftResultSet resultSet;
//        int size;
//
//        public RowSet(SwiftResultSet resultSet) {
//            this.resultSet = resultSet;
//        }
//
//        @Override
//        public int rowSize() {
//            return size;
//        }
//
//        @Override
//        public StringControlResult getResult() {
//            List<String> values = new ArrayList<String>();
//            try {
//                while (resultSet.hasNext()) {
//                    Row row = resultSet.next();
//                    if (size >= DEFAULT_SIZE) {
//                        break;
//                    }
//                    String s = row.getValue(0);
//                    values.add(s);
//                    size++;
//                }
//                StringControlResult result = new StringControlResult();
//                result.setValue(values);
//                result.setHasNext(resultSet.hasNext());
//
//                return result;
//            } catch (SQLException e) {
//                SwiftLoggers.getLogger().error(e);
//                return EMPTY_RESULT;
//            }
//        }
//
//        @Override
//        public ResultType getResultType() {
//            return ResultType.STRING;
//        }
//
//        static final StringControlResult EMPTY_RESULT = new StringControlResult();
//
//        static {
//            EMPTY_RESULT.setValue(Collections.<String>emptyList());
//            EMPTY_RESULT.setHasNext(false);
//        }
//    }
}