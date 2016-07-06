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

    R getBetweenPredicateLiteralRender(BetweenPredicate betweenPredicate, String driverTag);

    R getComparisonPredicateLiteralRender(ComparisonPredicate comparisonPredicate, String driverTag);

    R getBooleanAssertionPredicateLiteralRender(BooleanAssertionPredicate booleanAssertionPredicate, String driverTag);

    R getBooleanExpressionPredicateLiteralRender(BooleanExpressionPredicate booleanExpressionPredicate, String driverTag);

    R getBooleanStaticAssertionPredicateLiteralRender(BooleanStaticAssertionPredicate booleanStaticAssertionPredicate, String driverTag);

    R getCompoundPredicateLiteralRender(CompoundPredicate compoundPredicate, String driverTag);

    R getExistsPredicateLiteralRender(ExistsPredicate existsPredicate, String driverTag);

    R getExplicitTruthValueCheckLiteralRender(ExplicitTruthValueCheck explicitTruthValueCheck, String driverTag);

    R getInPredicateLiteralRender(InPredicate inPredicate, String driverTag);

    R getLikePredicateLiteralRender(LikePredicate likePredicate, String driverTag);


    R getNegatedPredicateLiteralRender(NegatedPredicateWrapper negatedPredicateWrapper, String driverTag);

    R getNullnessPredicateLiteralRender(NullnessPredicate nullnessPredicate, String driverTag);

    R getCriteriaQueryLiteralRender(CriteriaQueryImpl criteriaQuery, String driverTag);

    R getBasicFunctionExpressionLiteralRender(BasicFunctionExpression basicFunctionExpression, String driverTag);
    R getBinaryArithmeticOperationLiteralRender(BinaryArithmeticOperation binaryArithmeticOperation, String driverTag);
//    R get(CompoundPredicate compoundPredicate, String driverTag);
//    R get(CompoundPredicate compoundPredicate, String driverTag);
//    R get(CompoundPredicate compoundPredicate, String driverTag);


}
