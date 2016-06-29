package com.finebi.datasource.sql.criteria;

import com.finebi.datasource.api.criteria.*;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.PlainTable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2016/6/29.
 *
 * @author Connery
 * @since 4.0
 */
public class CriteriaQueryImpl<T> implements CriteriaQuery<T> {
    private Selection<? extends T> selection;
    private Set<Root<?>> roots = new HashSet<Root<?>>();
    private CriteriaBuilderImpl criteriaBuilder;

    public CriteriaQueryImpl(CriteriaBuilderImpl criteriaBuilder) {
        this.criteriaBuilder = criteriaBuilder;
    }


    @Override
    public CriteriaQuery<T> select(Selection<? extends T> selection) {
        this.selection = selection;
        return this;
    }


    @Override
    public CriteriaQuery<T> where(Expression<Boolean> restriction) {
        return null;
    }

    @Override
    public CriteriaQuery<T> where(Predicate... restrictions) {
        return null;
    }

    @Override
    public CriteriaQuery<T> groupBy(Expression<?>... grouping) {
        return null;
    }

    @Override
    public CriteriaQuery<T> groupBy(List<Expression<?>> grouping) {
        return null;
    }

    @Override
    public CriteriaQuery<T> having(Expression<Boolean> restriction) {
        return null;
    }

    @Override
    public CriteriaQuery<T> having(Predicate... restrictions) {
        return null;
    }

    @Override
    public CriteriaQuery<T> orderBy(Order... o) {
        return null;
    }

    @Override
    public CriteriaQuery<T> orderBy(List<Order> o) {
        return null;
    }

    @Override
    public CriteriaQuery<T> distinct(boolean distinct) {
        return null;
    }

    @Override
    public List<Order> getOrderList() {
        return null;
    }

    @Override
    public Set<ParameterExpression<?>> getParameters() {
        return null;
    }

    @Override
    public <X> Root<X> from(Class<X> entityClass) {
        return null;
    }

    @Override
    public <X> Root<X> from(PlainTable plainTable) {
        Root<X> root = new RootImpl<X>(criteriaBuilder, plainTable);
        roots.add(root);
        return root;
    }

    @Override
    public <X> Root<X> from(EntityType<X> entity) {
        return null;
    }

    @Override
    public Set<Root<?>> getRoots() {
        return roots;
    }

    @Override
    public Selection<T> getSelection() {
        return (Selection<T>) selection;
    }

    @Override
    public List<Expression<?>> getGroupList() {
        return null;
    }

    @Override
    public Predicate getGroupRestriction() {
        return null;
    }

    @Override
    public boolean isDistinct() {
        return false;
    }

    @Override
    public Class<T> getResultType() {
        return null;
    }

    @Override
    public <U> Subquery<U> subquery(Class<U> type) {
        return null;
    }

    @Override
    public Predicate getRestriction() {
        return null;
    }
}
