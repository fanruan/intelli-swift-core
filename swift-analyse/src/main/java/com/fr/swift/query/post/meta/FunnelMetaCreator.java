package com.fr.swift.query.post.meta;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.info.bean.element.aggregation.funnel.PostGroupBean;
import com.fr.swift.query.info.bean.query.FunnelQueryBean;
import com.fr.swift.query.info.bean.type.PostQueryType;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyon on 2018/12/28.
 */
public class FunnelMetaCreator implements MetaDataCreator<FunnelQueryBean> {
    @Override
    public SwiftMetaData create(FunnelQueryBean queryBean) throws SwiftMetaDataException {
        List<SwiftMetaDataColumn> metaDataColumns = new ArrayList<SwiftMetaDataColumn>();
        metaDataColumns.add(new MetaDataColumnBean("day", Types.VARCHAR));
        PostGroupBean groupBean = queryBean.getAggregation().getPostGroup();
        if (groupBean != null) {
            metaDataColumns.add(new MetaDataColumnBean(groupBean.getColumn(), Types.VARCHAR));
        }
        List<String> funnelEvents = queryBean.getAggregation().getFunnelEvents();
        for (String event : funnelEvents) {
            metaDataColumns.add(new MetaDataColumnBean(event, Types.BIGINT));
        }
        if (!queryBean.getPostAggregations().isEmpty()
                && queryBean.getPostAggregations().get(0).getType() == PostQueryType.FUNNEL_MEDIAN) {
            metaDataColumns.add(new MetaDataColumnBean("median", Types.DOUBLE));
        }
        return new SwiftMetaDataBean(queryBean.getTableName(), metaDataColumns);
    }
}
