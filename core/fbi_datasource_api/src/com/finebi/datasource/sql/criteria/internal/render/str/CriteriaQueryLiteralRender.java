package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.api.criteria.Order;
import com.finebi.datasource.sql.criteria.internal.CriteriaQueryImpl;
import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

import java.util.List;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class CriteriaQueryLiteralRender extends BasicLiteralRender<CriteriaQueryImpl> {
    public CriteriaQueryLiteralRender(CriteriaQueryImpl delegate) {
        super(delegate);
    }

    public String render(RenderingContext renderingContext) {
        String queryStructure = getDelegate().getQueryStructure().render(renderingContext).toString();
        StringBuilder sb = new StringBuilder(queryStructure);

        if (!getDelegate().getOrderList().isEmpty()) {
            sb.append(" order by ");
            String sep = "";
            List<Order> orders = getDelegate().getOrderList();
            for (Order orderSpec : orders) {
                sb.append(sep)
                        .append(((Renderable) orderSpec.getExpression()).render(renderingContext))
                        .append(orderSpec.isAscending() ? " asc" : " desc");
                sep = ", ";
            }
        }
        return sb.toString();
    }


    public String renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }
}
