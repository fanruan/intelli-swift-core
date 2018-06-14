package com.fr.swift.query.group.info;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.info.cursor.Cursor;
import com.fr.swift.query.group.info.cursor.Expander;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.Column;

import java.util.List;

/**
 * Created by Lyon on 2018/4/25.
 */
public interface GroupByInfo {

    /**
     * groupBy的维度列
     */
    List<Column> getDimensions();

    /**
     * 表的明细过滤
     */
    DetailFilter getDetailFilter();

    /**
     * 维度字典值的排序
     */
    List<Sort> getSorts();

    /**
     * todo 这个展开可以去掉，不在底层处理
     *
     * groupBy的展开
     */
    Expander getExpander();

    /**
     * 分页游标
     */
    Cursor getCursor();
}
