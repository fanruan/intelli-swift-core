package com.finebi.datasource.sql.criteria.internal.render.factory;

import com.finebi.datasource.sql.criteria.internal.CriteriaQueryImpl;
import com.finebi.datasource.sql.criteria.internal.QueryStructure;
import com.finebi.datasource.sql.criteria.internal.expression.PathTypeExpression;
import com.finebi.datasource.sql.criteria.internal.path.AbstractFromImpl;
import com.finebi.datasource.sql.criteria.internal.path.AbstractPathImpl;
import com.finebi.datasource.sql.criteria.internal.path.RootImpl;
import com.finebi.datasource.sql.criteria.internal.predicate.*;
import com.finebi.datasource.sql.criteria.internal.render.LiteralRender;
import com.finebi.datasource.sql.criteria.internal.render.str.*;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public class RenderFactoryDebug implements RenderFactory<LiteralRender> {
    public LiteralRender getQueryStructureRender(QueryStructure queryStructure, String driverTag) {
        return new QueryStructureRenderDebug(queryStructure);
    }

    @Override
    public LiteralRender getAbstractPathRender(AbstractPathImpl path, String driverTag) {
        return new AbstractPathRenderLiteral(path);
    }

    @Override
    public LiteralRender getAbstractFromRender(AbstractFromImpl from, String driverTag) {
        return new AbstractFromRenderLiteral(from);
    }

    @Override
    public LiteralRender getRootRender(RootImpl root, String driverTag) {
        return new RootLiteralRender(root);
    }

    @Override
    public LiteralRender getPathTypeExpressionRender(PathTypeExpression pathTypeExpression, String driverTag) {
        return null;
    }

    @Override
    public LiteralRender getBetweenPredicateLiteralRender(BetweenPredicate betweenPredicate, String driverTag) {
        return null;
    }

    @Override
    public LiteralRender getComparisonPredicateLiteralRender(ComparisonPredicate comparisonPredicate, String driverTag) {
        return null;
    }

    @Override
    public LiteralRender getBooleanAssertionPredicateLiteralRender(BooleanAssertionPredicate booleanAssertionPredicate, String driverTag) {
        return null;
    }

    @Override
    public LiteralRender getBooleanExpressionPredicateLiteralRender(BooleanExpressionPredicate booleanExpressionPredicate, String driverTag) {
        return null;
    }

    @Override
    public LiteralRender getBooleanStaticAssertionPredicateLiteralRender(BooleanStaticAssertionPredicate booleanStaticAssertionPredicate, String driverTag) {
        return null;
    }

    @Override
    public LiteralRender getCompoundPredicateLiteralRender(CompoundPredicate compoundPredicate, String driverTag) {
        return null;
    }

    @Override
    public LiteralRender getExistsPredicateLiteralRender(ExistsPredicate existsPredicate, String driverTag) {
        return null;
    }

    @Override
    public LiteralRender getExplicitTruthValueCheckLiteralRender(ExplicitTruthValueCheck explicitTruthValueCheck, String driverTag) {
        return null;
    }

    @Override
    public LiteralRender getInPredicateLiteralRender(InPredicate inPredicate, String driverTag) {
        return null;
    }

    @Override
    public LiteralRender getLikePredicateLiteralRender(LikePredicate likePredicate, String driverTag) {
        return null;
    }

    @Override
    public LiteralRender getNegatedPredicateLiteralRender(NegatedPredicateWrapper negatedPredicateWrapper, String driverTag) {
        return null;
    }

    @Override
    public LiteralRender getNullnessPredicateLiteralRender(NullnessPredicate nullnessPredicate, String driverTag) {
        return null;
    }

    @Override
    public LiteralRender getCriteriaQueryLiteralRender(CriteriaQueryImpl criteriaQuery, String driverTag) {
        return new CriteriaQueryLiteralRender(criteriaQuery);
    }
}
