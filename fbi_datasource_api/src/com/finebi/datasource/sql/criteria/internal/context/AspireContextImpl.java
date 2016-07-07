package com.finebi.datasource.sql.criteria.internal.context;

import com.finebi.datasource.api.criteria.CriteriaBuilder;
import com.finebi.datasource.api.metamodel.Metamodel;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.render.factory.RenderFactory;
import com.finebi.datasource.sql.criteria.internal.render.factory.RenderFactoryEngineAdapter;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * 不是线程安全，可以多线程访问，但是不能多线程写数据。
 * 注意加锁
 * This class created on 2016/7/1.
 *
 * @author Connery
 * @since 4.0
 */
public class AspireContextImpl implements AspireContext {
    private RenderFactory renderFactory;
    private CriteriaBuilder criteriaBuilder;
    private Metamodel metamodel;
    private ContextProperty properties;
    private boolean closed;

    public AspireContextImpl() {
        renderFactory = new RenderFactoryEngineAdapter();
        this.criteriaBuilder = new CriteriaBuilderImpl(this);
        properties = new ContextProperty();
        closed = false;
    }

    public void close() {
        closed = false;
    }

    private boolean isImmutable() {
        return this.closed;
    }

    public Builder getBuilder() {
        if (!isImmutable()) {
            return new Builder();
        } else {
            throw BINonValueUtils.beyondControl();
        }
    }

    public class Builder {
        public void addProperty(String name, Object value) {
            properties.addProperty(name, value);
        }

        public void setMetamodel(Metamodel metamodel) {
            AspireContextImpl.this.metamodel = metamodel;
            AspireContextImpl.this.metamodel.close();
        }
    }


    public AspireContextImpl(RenderFactory renderFactory) {
        this.renderFactory = renderFactory;
    }

    @Override
    public Metamodel getMetamodel() {
        return metamodel;
    }

    @Override
    public RenderFactory getRenderFactory() {
        return renderFactory;
    }

    @Override
    public Object getProperty(String propertyName) {
        return null;
    }
}
