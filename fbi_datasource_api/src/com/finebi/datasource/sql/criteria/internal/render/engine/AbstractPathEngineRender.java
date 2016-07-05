package com.finebi.datasource.sql.criteria.internal.render.engine;

import com.finebi.datasource.api.metamodel.Attribute;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.path.AbstractPathImpl;
import com.fr.fineengine.criterion.Projections;
import com.fr.fineengine.criterion.valuetype.ValueTypes;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public class AbstractPathEngineRender extends BasicEngineRender<AbstractPathImpl> {
    public AbstractPathEngineRender(AbstractPathImpl delegate) {
        super(delegate);
    }

    @Override
    public Object render(RenderingContext renderingContext) {
        if (getDelegate().getPathSource() != null) {
            Attribute<?, ?> attribute = getDelegate().getAttribute();
            return Projections.field(attribute.getName(), covert(attribute.getJavaType()));
        } else {
            throw new RuntimeException();
        }
    }

    private ValueTypes covert(Class javaType) {
        if (Integer.class.isAssignableFrom(javaType)) {
            return ValueTypes.Integer;
        }

        if (String.class.isAssignableFrom(javaType)) {
            return ValueTypes.String;
        }
        return null;
    }

    @Override
    public Object renderProjection(RenderingContext renderingContext) {
        return render(renderingContext);
    }
}
