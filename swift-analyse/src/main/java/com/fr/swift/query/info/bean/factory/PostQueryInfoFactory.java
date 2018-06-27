package com.fr.swift.query.info.bean.factory;

import com.fr.swift.query.info.bean.post.CalculatedFieldQueryInfoBean;
import com.fr.swift.query.info.bean.post.HavingFilterQueryInfoBean;
import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.query.info.bean.post.RowSortQueryInfoBean;
import com.fr.swift.query.info.bean.post.TreeAggregationQueryInfoBean;
import com.fr.swift.query.info.bean.post.TreeFilterQueryInfoBean;
import com.fr.swift.query.info.bean.post.TreeSortQueryInfoBean;
import com.fr.swift.query.info.group.post.PostQueryInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/22
 */
public class PostQueryInfoFactory implements BeanFactory<List<PostQueryInfo>, List<PostQueryInfoBean>> {

    public static final BeanFactory<PostQueryInfo, PostQueryInfoBean> SINGLE_POST_QUERY_INFO_FACTORY = new BeanFactory<PostQueryInfo, PostQueryInfoBean>() {
        // TODO 6/22 具体属性
        @Override
        public PostQueryInfoBean create(PostQueryInfo source) {
            switch (source.getType()) {
                case CAL_FIELD:
                    return new CalculatedFieldQueryInfoBean();
                case ROW_SORT:
                    return new RowSortQueryInfoBean();
                case TREE_SORT:
                    return new TreeSortQueryInfoBean();
                case TREE_FILTER:
                    return new TreeFilterQueryInfoBean();
                case HAVING_FILTER:
                    return new HavingFilterQueryInfoBean();
                case TREE_AGGREGATION:
                    return new TreeAggregationQueryInfoBean();
            }
            return null;
        }
    };

    @Override
    public List<PostQueryInfoBean> create(List<PostQueryInfo> source) {
        List<PostQueryInfoBean> result = new ArrayList<PostQueryInfoBean>();
        if (null != source) {
            for (PostQueryInfo postQueryInfo : source) {
                result.add(SINGLE_POST_QUERY_INFO_FACTORY.create(postQueryInfo));
            }
        }
        return result;
    }
}
