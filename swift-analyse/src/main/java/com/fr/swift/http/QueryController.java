package com.fr.swift.http;

import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.info.element.dimension.DetailDimension;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Controller;
import com.fr.third.springframework.web.bind.annotation.PathVariable;
import com.fr.third.springframework.web.bind.annotation.RequestMapping;
import com.fr.third.springframework.web.bind.annotation.RequestMethod;
import com.fr.third.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/21
 */
@Controller
public class QueryController {

    @Autowired
    private SwiftMetaDataService metaDataService;

    @ResponseBody
    @RequestMapping(value = "swift/query/{sourceKey}", method = RequestMethod.GET)
    public List<Row> query(@PathVariable("sourceKey") String sourceKey) throws SQLException {
        List<Row> rows = new ArrayList<Row>();
        QueryInfo queryInfo = createQueryInfo(sourceKey);
        Query query = QueryBuilder.buildQuery(queryInfo);
        SwiftResultSet resultSet = query.getQueryResult();
        if (resultSet != null) {
            while (resultSet.next()) {
                rows.add(resultSet.getRowData());
            }
            resultSet.close();
        }
        return rows;
    }

    private QueryInfo createQueryInfo(String key) throws SwiftMetaDataException {

        SwiftMetaData metaData = metaDataService.getMetaDataByKey(key);
        if (null == metaData) {
            throw new SwiftMetaDataException();
        }
        SourceKey sourceKey = new SourceKey(key);

        String queryId = sourceKey.getId();
        List<Dimension> dimensions = new ArrayList<Dimension>();

        List<Sort> sorts = new ArrayList<Sort>();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            dimensions.add(new DetailDimension(i, sourceKey, new ColumnKey(metaData.getColumnName(i + 1)), null, null, null));
        }
        List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
        return new DetailQueryInfo(queryId, sourceKey, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND),
                dimensions, sorts, null, metaData);
    }
}
