package com.fr.swift.cloud.query.post.meta;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.config.entity.SwiftMetaDataEntity;
import com.fr.swift.cloud.config.service.SwiftMetaDataService;
import com.fr.swift.cloud.db.SwiftDatabase;
import com.fr.swift.cloud.exception.meta.SwiftMetaDataException;
import com.fr.swift.cloud.query.info.bean.element.DimensionBean;
import com.fr.swift.cloud.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.cloud.query.info.bean.type.DimensionType;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.SwiftMetaData;

import java.util.List;

/**
 * @author lyon
 * @date 2018/11/28
 */
public class DetailMetaDataCreator extends BaseMetaDataCreator<DetailQueryInfoBean> {

    @Override
    public SwiftMetaData create(DetailQueryInfoBean bean) throws SwiftMetaDataException {
        final String tableName = bean.getTableName();
        SwiftMetaData meta = SwiftContext.get().getBean(SwiftMetaDataService.class).getMeta(new SourceKey(bean.getTableName()));
        SwiftDatabase schema = meta.getSwiftDatabase();
        List<DimensionBean> dimensionBeans = bean.getDimensions();
        if (dimensionBeans.size() == 1 && dimensionBeans.get(0).getType() == DimensionType.DETAIL_ALL_COLUMN) {
            // yee 这里避免将原有metadata返回，上层误用可能将metadata修改
            return meta.clone();
        }
        return new SwiftMetaDataEntity(null, schema, schema.getName(), tableName, tableName, createDimension(meta, dimensionBeans));
    }
}
