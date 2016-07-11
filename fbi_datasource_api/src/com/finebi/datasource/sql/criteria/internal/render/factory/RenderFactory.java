package com.finebi.datasource.sql.criteria.internal.render.factory;

import com.finebi.datasource.sql.criteria.internal.CriteriaQueryImpl;
import com.finebi.datasource.sql.criteria.internal.QueryStructure;
import com.finebi.datasource.sql.criteria.internal.expression.*;
import com.finebi.datasource.sql.criteria.internal.expression.function.BasicFunctionExpression;
import com.finebi.datasource.sql.criteria.internal.path.AbstractFromImpl;
import com.finebi.datasource.sql.criteria.internal.path.AbstractPathImpl;
import com.finebi.datasource.sql.criteria.internal.path.RootImpl;
import com.finebi.datasource.sql.criteria.internal.predicate.*;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public interface RenderFactory<R> {


    R getRootRender(RootImpl root, String driverTag);

    R getAbstractFromRender(AbstractFromImpl from, String driverTag);

    R getAbstractPathRender(AbstractPathImpl path, String driverTag);

    R getQueryStructureRender(QueryStructure queryStructure, String driverTag);

    R getPathTypeExpressionRender(PathTypeExpression pathTypeExpression, String driverTag);

    R getBetweenPredicateRender(BetweenPredicate betweenPredicate, String driverTag);

    R getComparisonPredicateRender(ComparisonPredicate comparisonPredicate, String driverTag);

    R getBooleanAssertionPredicateRender(BooleanAssertionPredicate booleanAssertionPredicate, String driverTag);

    R getBooleanExpressionPredicateRender(BooleanExpressionPredicate booleanExpressionPredicate, String driverTag);

    R getBooleanStaticAssertionPredicateRender(BooleanStaticAssertionPredicate booleanStaticAssertionPredicate, String driverTag);

    R getCompoundPredicateRender(CompoundPredicate compoundPredicate, String driverTag);

    R getExistsPredicateRender(ExistsPredicate existsPredicate, String driverTag);

    R getExplicitTruthValueCheckRender(ExplicitTruthValueCheck explicitTruthValueCheck, String driverTag);

    R getInPredicateRender(InPredicate inPredicate, String driverTag);

    R getLikePredicateRender(LikePredicate likePredicate, String driverTag);

    R getNegatedPredicateRender(NegatedPredicateWrapper negatedPredicateWrapper, String driverTag);

    R getNullnessPredicateRender(NullnessPredicate nullnessPredicate, String driverTag);

    R getCriteriaQueryLiteralRender(CriteriaQueryImpl criteriaQuery, String driverTag);

    R getBasicFunctionExpressionLiteralRender(BasicFunctionExpression basicFunctionExpression, String driverTag);

    R getBinaryArithmeticOperationLiteralRender(BinaryArithmeticOperation binaryArithmeticOperation, String driverTag);

    R getCoalesceExpressionLiteralRender(CoalesceExpression coalesceExpression, String driverTag);

    R getConcatExpressionLiteralRender(ConcatExpression concatExpression, String driverTag);

    R getEntityTypeExpressionLiteralRender(EntityTypeExpression entityTypeExpression, String driverTag);

    R getLiteralExpressionLiteralRender(LiteralExpression literalExpression, String driverTag);

    R getNullifExpressionLiteralRender(NullifExpression nullifExpression, String driverTag);

    R getNullLiteralExpressionLiteralRender(NullLiteralExpression nullLiteralExpression, String driverTag);

    R getParameterExpressionImplLiteralRender(ParameterExpressionImpl parameterExpression, String driverTag);

    R getPathTypeExpressionLiteralRender(PathTypeExpression PathTypeExpression, String driverTag);

    R getSimpleCaseExpressionLiteralRender(SimpleCaseExpression simpleCaseExpression, String driverTag);

    R getSubqueryComparisonModifierExpressionLiteralRender(SubqueryComparisonModifierExpression subqueryComparisonModifierExpression, String driverTag);

    R getUnaryArithmeticOperationLiteralRender(UnaryArithmeticOperation unaryArithmeticOperation, String driverTag);
    //    R get(CompoundPredicate compoundPredicate, String driverTag);
    //    R get(CompoundPredicate compoundPredicate, String driverTag);
    //    R get(CompoundPredicate compoundPredicate, String driverTag);


}
