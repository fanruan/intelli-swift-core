package com.finebi.datasource.api;


import com.finebi.datasource.api.criteria.*;
import com.finebi.datasource.api.metamodel.EntityManager;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.PlainTable;
import com.finebi.datasource.sql.criteria.internal.CriteriaQueryImpl;
import com.finebi.datasource.sql.criteria.internal.compile.ExplicitParameterInfo;
import com.finebi.datasource.sql.criteria.internal.compile.ImplicitParameterBinding;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.context.AspireContext;
import com.finebi.datasource.sql.criteria.internal.context.AspireContextImpl;
import com.finebi.datasource.sql.criteria.internal.metamodel.EntityManagerImpl;
import com.finebi.datasource.sql.criteria.internal.render.factory.RenderFactory;
import com.finebi.datasource.sql.criteria.internal.render.factory.RenderFactoryDebug;
import com.fr.fineengine.utils.StringHelper;
import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * This class created on 2016/6/23.
 *
 * @author Connery
 * @since 4.0
 */
public class WherePredicateTest extends TestCase {

    private AspireContext context = new AspireContextImpl(new RenderFactoryDebug());
    private EntityManager manager = new EntityManagerImpl(context);

    /**
     * 普通查询
     * Detail:
     * Author:Connery
     * Date:2016/6/23
     */
    /**
     * 添加断言
     * Detail:
     * Author:Osborn
     * Date:2016/7/5
     */
    public void testSelect() {
        try {

            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root.get("id"));
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0.id from jpa as generatedAlias0", result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }


    public static EntityType getEntity() {
        return SelectionTest.getEntity();
    }

    /**
     * 关联的使用
     * Detail:
     * Author:Connery
     * Date:2016/6/23
     */
    /**
     * 添加断言,修改代码
     * Detail:
     * Author:Osborn
     * Date:2016/7/5
     */


    /**
     * Detail:
     * Author:Connery
     * Date:2016/6/23
     */
    /**
     * 添加断言,修改代码
     * Detail:
     * Author:Osborn
     * Date:2016/7/5
     */


    public static RenderingContext getContext() {
        return new RenderingContext() {
            private int aliasCount;
            private int explicitParameterCount;

            public String generateAlias() {
                return "generatedAlias" + aliasCount++;
            }

            public String generateParameterName() {
                return "param" + explicitParameterCount++;
            }

            @Override
            public ExplicitParameterInfo registerExplicitParameter(ParameterExpression criteriaQueryParameter) {
                ExplicitParameterInfo parameterInfo = null;
                if (parameterInfo == null) {
                    if (StringHelper.isNotEmpty(criteriaQueryParameter.getName())) {
                        parameterInfo = new ExplicitParameterInfo(
                                criteriaQueryParameter.getName(),
                                null,
                                criteriaQueryParameter.getJavaType()
                        );
                    } else if (criteriaQueryParameter.getPosition() != null) {
                        parameterInfo = new ExplicitParameterInfo(
                                null,
                                criteriaQueryParameter.getPosition(),
                                criteriaQueryParameter.getJavaType()
                        );
                    } else {
                        parameterInfo = new ExplicitParameterInfo(
                                generateParameterName(),
                                null,
                                criteriaQueryParameter.getJavaType()
                        );
                    }

//                    explicitParameterInfoMap.put(criteriaQueryParameter, parameterInfo);
                }

                return parameterInfo;
            }

            public String registerLiteralParameterBinding(final Object literal, final Class javaType) {
                final String parameterName = generateParameterName();
                final ImplicitParameterBinding binding = new ImplicitParameterBinding() {
                    public String getParameterName() {
                        return parameterName;
                    }

                    public Class getJavaType() {
                        return javaType;
                    }

                    public void bind(TypedQuery typedQuery) {
                        typedQuery.setParameter(parameterName, literal);
                    }
                };

//                implicitParameterBindings.add(binding);
                return literal.toString();
            }

            @Override
            public RenderFactory getRenderFactory() {
                return new RenderFactoryDebug();
            }

            public String getCastType(Class javaType) {

                return "castType";
            }
        };
    }

    /**
     * 添加断言
     * Detail:
     * Author:Osborn
     * Date:2016/7/5
     */
    public void testWhere() {
        try {

            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.gt(root.get("id"), 2);
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id>2", result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }


    /**
     * 添加断言
     * Detail:
     * Author:Osborn
     * Date:2016/7/5
     */
//    public void testRootCount() {
//        try {
//
//            CriteriaBuilder cb = manager.getCriteriaBuilder();
//            CriteriaQuery<PlainTable> query = cb.createQuery();
//            Root root = query.from(getEntity());
//            Expression count = cb.count(root);
//            query.select(count);
//            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
//            assertEquals("select count(*) from jpa as generatedAlias0", result);
//            System.out.println(result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            assertTrue(false);
//        }
//    }


    private Object executeQuery(CriteriaQuery query) {
        return null;
    }

    private CriteriaBuilder generateCB() {
        CriteriaBuilder cb = EasyMock.createMock(CriteriaBuilder.class);
        return cb;
    }

    public void testSubQuery() {
        try {

            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();

            Subquery subquery = query.subquery(getEntity());
            Root subRoot = subquery.from(getEntity());
            subquery.select(subRoot.get("id"));
            Root root = query.from(getEntity());
            query.select(root);

            Predicate condition = cb.in(subRoot.get("id")).value(subquery);
            query.where(condition);

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testWhereExists() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();

            Subquery subquery = query.subquery(getEntity());
            Root root = subquery.from(getEntity());
            subquery.select(root.get("id"));
            Root main = query.from(getEntity());
            query.select(main);

            Predicate condition = cb.exists(subquery);
            query.where(condition);

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }


    /**
     * 普通查询
     * Detail:
     * Author:Connery
     * Date:2016/6/23
     */
    public void testWhereEq() {
        try {
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.equal(root.get("id"), 2);
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id=2",result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testWhereNotEq() {
        try {
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.notEqual(root.get("id"), 2);
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id<>2",result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * 普通查询
     * Detail:
     * Author:Osborn
     * Date:2016/7/5
     */
    public void testWhereBetween() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.between(root.get("id"), 1, 3);
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id between 1 and 3", result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * 普通查询
     * Detail:
     * Author:Connery
     * Date:2016/6/23
     */
    public void testWhereIsNotNull() {
        try {

            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.isNotNull(root.get("id"));
            query.where(condition);

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * 普通查询
     * Detail:
     * Author:Connery
     * Date:2016/6/23
     */
    public void testWhereIsNull() {
        try {

            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.isNull(root.get("id"));
            query.where(condition);

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            System.out.println(result);
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id is null", result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * 普通查询
     * Detail:
     * Author:Connery
     * Date:2016/6/23
     */
    public void testWhereLike() {
        try {

            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.like(root.get("id"), "%a");
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id like :%a",result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * 普通查询
     * Detail:
     * Author:Osborn
     * Date:2016/7/5
     */
    public void testWhereNotLike() {
        try {

            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.notLike(root.get("id"), "%a_");
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id not like :%a_",result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * 普通查询
     * Detail:
     * Author:Connery
     * Date:2016/6/23
     */
    public void testWhereAnd() {
        try {

            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.like(root.get("id"), "%a");

            query.where(cb.and(condition, cb.equal(root.get("id"), 2)));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where ( generatedAlias0.id like :%a ) and ( generatedAlias0.id=2 )",result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testWhereOr() {
        try {

            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.like(root.get("id"), "%a");

            query.where(cb.or(condition, cb.equal(root.get("id"), 2)));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where ( generatedAlias0.id like :%a ) or ( generatedAlias0.id=2 )",result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testWhereGreaterThan() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.greaterThan(root.get("id"), 1);
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id>1",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testWhereGreaterThanOrEq() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.greaterThanOrEqualTo(root.get("id"), 1);
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id>=1",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testWhereLessThan() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.lessThan(root.get("id"), 1);
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id<1",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testWhereLessThanOrEq() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.lessThanOrEqualTo(root.get("id"), 1);
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id<=1",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testWhereIsTrue() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.isTrue(cb.lessThanOrEqualTo(root.get("id"), 1));
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
//            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id<=1",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testWhereIsFalseLe() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.isFalse(cb.lessThanOrEqualTo(root.get("id"), 1));
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id>1",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testWhereIsFalseEq() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.isFalse(cb.equal(root.get("id"), 1));
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id<>1",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testWhereIsFalseGe() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.isFalse(cb.greaterThanOrEqualTo(root.get("id"), 1));
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id<1",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testWhereIsFalseIsNULL() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.isFalse(cb.isNull(root.get("id")));
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id is not null",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testWhereIsFalseIsNotNULL() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.isFalse(cb.isNotNull(root.get("id")));
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id is null",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testWhereSome() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Subquery subquery = query.subquery(getEntity());
            Root subRoot = subquery.from(getEntity());
            subquery.select(subRoot.get("id"));
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.greaterThan(cb.some(subquery),3);
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where some ()>3",result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

    }

    public void testWhereAll() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Subquery subquery = query.subquery(getEntity());
            Root subRoot = subquery.from(getEntity());
            subquery.select(subRoot.get("id"));
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.greaterThan(cb.all(subquery),3);
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where all ()>3",result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

    }

    public void testWhereAny() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Subquery subquery = query.subquery(getEntity());
            Root subRoot = subquery.from(getEntity());
            subquery.select(subRoot.get("id"));
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.greaterThan(cb.any(subquery),3);
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where any ()>3",result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * conjunction
     * Detail:
     *
     * @Author Osborn
     * @Date 2016/7/6
     */
    public void testWhereDisjunction() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate conjunction = cb.disjunction();

            query.where(conjunction);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where 0=1",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testWhereConjunction() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate conjunction = cb.conjunction();

            query.where(conjunction);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where 1=1",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testWhereNULLIF() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.isTrue(cb.abs(root.get("id")));

            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
//            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where 1=1",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}

