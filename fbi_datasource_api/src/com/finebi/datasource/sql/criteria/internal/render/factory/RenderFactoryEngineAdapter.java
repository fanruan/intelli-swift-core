package com.finebi.datasource.sql.criteria.internal.render.factory;

import com.finebi.datasource.sql.criteria.internal.CriteriaQueryImpl;
import com.finebi.datasource.sql.criteria.internal.QueryStructure;
import com.finebi.datasource.sql.criteria.internal.expression.BinaryArithmeticOperation;
import com.finebi.datasource.sql.criteria.internal.expression.PathTypeExpression;
import com.finebi.datasource.sql.criteria.internal.expression.function.BasicFunctionExpression;
import com.finebi.datasource.sql.criteria.internal.path.AbstractFromImpl;
import com.finebi.datasource.sql.criteria.internal.path.AbstractPathImpl;
import com.finebi.datasource.sql.criteria.internal.path.RootImpl;
import com.finebi.datasource.sql.criteria.internal.predicate.*;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;
import com.finebi.datasource.sql.criteria.internal.render.engine.AbstractPathEngineRender;
import com.finebi.datasource.sql.criteria.internal.render.engine.QueryStructureRenderFineEngine;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public class RenderFactoryEngineAdapter implements RenderFactory {
    public RenderExtended getQueryStructureRender(QueryStructure queryStructure, String driverTag) {
        return new QueryStructureRenderFineEngine(queryStructure);
    }

    @Override
    public Object getAbstractPathRender(AbstractPathImpl path, String driverTag) {
        return new AbstractPathEngineRender(path);
    }

    @Override
    public Object getAbstractFromRender(AbstractFromImpl from, String driverTag) {
        return null;
    }

    @Override
    public Object getRootRender(RootImpl root, String driverTag) {
        return null;
    }

    @Override
    public Object getPathTypeExpressionRender(PathTypeExpression pathTypeExpression, String driverTag) {
        return null;
    }

    @Override
    public Object getBetweenPredicateLiteralRender(BetweenPredicate betweenPredicate, String driverTag) {
        return null;
    }

    @Override
    public Object getComparisonPredicateLiteralRender(ComparisonPredicate comparisonPredicate, String driverTag) {
        return null;
    }

    @Override
    public Object getBooleanAssertionPredicateLiteralRender(BooleanAssertionPredicate booleanAssertionPredicate, String driverTag) {
        return null;
    }

    @Override
    public Object getBooleanExpressionPredicateLiteralRender(BooleanExpressionPredicate booleanExpressionPredicate, String driverTag) {
        return null;
    }

    @Override
    public Object getBooleanStaticAssertionPredicateLiteralRender(BooleanStaticAssertionPredicate booleanStaticAssertionPredicate, String driverTag) {
        return null;
    }

    @Override
    public Object getCompoundPredicateLiteralRender(CompoundPredicate compoundPredicate, String driverTag) {
        return null;
    }

    @Override
    public Object getExistsPredicateLiteralRender(ExistsPredicate existsPredicate, String driverTag) {
        return null;
    }

    @Override
    public Object getExplicitTruthValueCheckLiteralRender(ExplicitTruthValueCheck explicitTruthValueCheck, String driverTag) {
        return null;
    }

    @Override
    public Object getInPredicateLiteralRender(InPredicate inPredicate, String driverTag) {
        return null;
    }

    @Override
    public Object getLikePredicateLiteralRender(LikePredicate likePredicate, String driverTag) {
        return null;
    }

    @Override
    public Object getNegatedPredicateLiteralRender(NegatedPredicateWrapper negatedPredicateWrapper, String driverTag) {
        return null;
    }

    @Override
    public Object getNullnessPredicateLiteralRender(NullnessPredicate nullnessPredicate, String driverTag) {
        return null;
    }

    @Override
    public Object getCriteriaQueryLiteralRender(CriteriaQueryImpl criteriaQuery, String driverTag) {
        return null;
    }

    @Override
    public Object getBasicFunctionExpressionLiteralRender(BasicFunctionExpression basicFunctionExpression, String driverTag) {
        return null;
    }

    @Override
    public Object getBinaryArithmeticOperationLiteralRender(BinaryArithmeticOperation binaryArithmeticOperation, String driverTag) {
        return null;
    }
}
