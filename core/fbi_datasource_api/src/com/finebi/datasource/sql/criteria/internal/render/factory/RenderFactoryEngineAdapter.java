package com.finebi.datasource.sql.criteria.internal.render.factory;

import com.finebi.datasource.sql.criteria.internal.CriteriaQueryImpl;
import com.finebi.datasource.sql.criteria.internal.QueryStructure;
import com.finebi.datasource.sql.criteria.internal.expression.*;
import com.finebi.datasource.sql.criteria.internal.expression.function.BasicFunctionExpression;
import com.finebi.datasource.sql.criteria.internal.path.AbstractFromImpl;
import com.finebi.datasource.sql.criteria.internal.path.AbstractPathImpl;
import com.finebi.datasource.sql.criteria.internal.path.RootImpl;
import com.finebi.datasource.sql.criteria.internal.predicate.*;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;
import com.finebi.datasource.sql.criteria.internal.render.engine.*;

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
    public Object getNullLiteralExpressionLiteralRender(NullLiteralExpression nullLiteralExpression, String driverTag) {
        return null;
    }

    @Override
    public Object getParameterExpressionImplLiteralRender(ParameterExpressionImpl parameterExpression, String driverTag) {
        return null;
    }

    @Override
    public Object getPathTypeExpressionLiteralRender(PathTypeExpression PathTypeExpression, String driverTag) {
        return null;
    }

    @Override
    public Object getSimpleCaseExpressionLiteralRender(SimpleCaseExpression simpleCaseExpression, String driverTag) {
        return null;
    }

    @Override
    public Object getSubqueryComparisonModifierExpressionLiteralRender(SubqueryComparisonModifierExpression subqueryComparisonModifierExpression, String driverTag) {
        return null;
    }

    @Override
    public Object getUnaryArithmeticOperationLiteralRender(UnaryArithmeticOperation unaryArithmeticOperation, String driverTag) {
        return null;
    }

    @Override
    public Object getCoalesceExpressionLiteralRender(CoalesceExpression coalesceExpression, String driverTag) {
        return null;
    }

    @Override
    public Object getLiteralExpressionLiteralRender(LiteralExpression literalExpression, String driverTag) {
        return new LiteralExpressionEngineRender(literalExpression);
    }

    @Override
    public Object getNullifExpressionLiteralRender(NullifExpression nullifExpression, String driverTag) {
        return null;
    }

    @Override
    public Object getConcatExpressionLiteralRender(ConcatExpression concatExpression, String driverTag) {
        return null;
    }

    @Override
    public Object getEntityTypeExpressionLiteralRender(EntityTypeExpression entityTypeExpression, String driverTag) {
        return null;
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
    public Object getBetweenPredicateRender(BetweenPredicate betweenPredicate, String driverTag) {
        return new BetweenPredicateEngineRender(betweenPredicate);
    }

    @Override
    public Object getComparisonPredicateRender(ComparisonPredicate comparisonPredicate, String driverTag) {
        return new ComparisonPredicateFineEngineRender(comparisonPredicate);
    }

    @Override
    public Object getBooleanAssertionPredicateRender(BooleanAssertionPredicate booleanAssertionPredicate, String driverTag) {
        return null;
    }

    @Override
    public Object getBooleanExpressionPredicateRender(BooleanExpressionPredicate booleanExpressionPredicate, String driverTag) {
        return null;
    }

    @Override
    public Object getBooleanStaticAssertionPredicateRender(BooleanStaticAssertionPredicate booleanStaticAssertionPredicate, String driverTag) {
        return null;
    }

    @Override
    public Object getCompoundPredicateRender(CompoundPredicate compoundPredicate, String driverTag) {
        return null;
    }

    @Override
    public Object getExistsPredicateRender(ExistsPredicate existsPredicate, String driverTag) {
        return null;
    }

    @Override
    public Object getExplicitTruthValueCheckRender(ExplicitTruthValueCheck explicitTruthValueCheck, String driverTag) {
        return null;
    }

    @Override
    public Object getInPredicateRender(InPredicate inPredicate, String driverTag) {
        return new InPredicateEngineRender(inPredicate);
    }

    @Override
    public Object getLikePredicateRender(LikePredicate likePredicate, String driverTag) {
        return new LikePredicateEngineRender(likePredicate);
    }

    @Override
    public Object getNegatedPredicateRender(NegatedPredicateWrapper negatedPredicateWrapper, String driverTag) {
        return null;
    }

    @Override
    public Object getNullnessPredicateRender(NullnessPredicate nullnessPredicate, String driverTag) {
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
