package com.finebi.datasource.sql.criteria;

/**
 * This class created on 2016/6/29.
 *
 * @author Connery
 * @since 4.0
 */

import com.finebi.datasource.api.metamodel.PlainTable;

import java.io.Serializable;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public abstract class AbstractTupleElement<X>
        extends AbstractNode
        implements TupleElementImplementor<X>, Serializable {
    private PlainTable plainTable;
    private String alias;
    private ValueHandlerFactory.ValueHandler<X> valueHandler;

    protected AbstractTupleElement(CriteriaBuilderImpl criteriaBuilder, PlainTable plainTable) {
        super(criteriaBuilder);
        this.plainTable = plainTable;
    }

    @Override
    public PlainTable getPlainTable() {
        return plainTable;
    }


    protected void forceConversion(ValueHandlerFactory.ValueHandler<X> valueHandler) {
        this.valueHandler = valueHandler;
    }

    @Override
    public ValueHandlerFactory.ValueHandler<X> getValueHandler() {
        return valueHandler;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    /**
     * Protected access to define the alias.
     *
     * @param alias The alias to use.
     */
    protected void setAlias(String alias) {
        this.alias = alias;
    }
}

