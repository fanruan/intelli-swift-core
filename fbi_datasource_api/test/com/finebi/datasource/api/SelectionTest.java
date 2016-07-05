package com.finebi.datasource.api;


import com.finebi.datasource.api.criteria.*;
import com.finebi.datasource.api.metamodel.AttributeType;
import com.finebi.datasource.api.metamodel.EntityManager;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.PlainTable;
import com.finebi.datasource.sql.criteria.AttributeTypeImpl;
import com.finebi.datasource.sql.criteria.internal.CriteriaQueryImpl;
import com.finebi.datasource.sql.criteria.internal.OrderImpl;
import com.finebi.datasource.sql.criteria.internal.compile.ExplicitParameterInfo;
import com.finebi.datasource.sql.criteria.internal.compile.ImplicitParameterBinding;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.context.AspireContext;
import com.finebi.datasource.sql.criteria.internal.context.AspireContextImpl;
import com.finebi.datasource.sql.criteria.internal.expression.ExpressionImpl;
import com.finebi.datasource.sql.criteria.internal.metamodel.*;
import com.fr.fineengine.utils.StringHelper;
import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * This class created on 2016/6/23.
 *
 * @author Connery
 * @since 4.0
 */
public class SelectionTest extends TestCase {
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
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root.get("id"));
            String result = ((CriteriaQueryImpl) query).render(new RenderingContext() {
                @Override
                public String generateAlias() {
                    return "alisas";
                }

                @Override
                public ExplicitParameterInfo registerExplicitParameter(ParameterExpression<?> criteriaQueryParameter) {
                    return null;
                }

                @Override
                public String registerLiteralParameterBinding(Object literal, Class javaType) {
                    return null;
                }

                @Override
                public String getCastType(Class javaType) {
                    return "castType";
                }
            });
            assertEquals("select alisas.id from jpa as alisas",result);
            System.out.println(result);
//            查询的表，获得根
//            Root root = query.from(target);

//            查询的结果元数据
//            query.select(root);
//            Object result = executeQuery(query);
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
    /**
     * 添加断言
     * Detail:
     * Author:Osborn
     * Date:2016/7/5
     */
    public void testProjectionSelect() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root.get("id"));
            String result = ((CriteriaQueryImpl) query).render(getContext());
            assertEquals("select generatedAlias0.id from jpa as generatedAlias0",result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public static EntityType getEntity() {
        AttributeFactory factory = new AttributeFactory(null);
        AttributeImplementor implementor = factory.buildAttribute(null, new AttributePropertyImpl("id", false, new AttributeTypeImpl(AttributeType.InnerType.Integer)));
        AttributeImplementor implementor_name = factory.buildAttribute(null, new AttributePropertyImpl("A_name", false, new AttributeTypeImpl(AttributeType.InnerType.String)));
        AttributeImplementor implementor_age = factory.buildAttribute(null, new AttributePropertyImpl("age", false, new AttributeTypeImpl(AttributeType.InnerType.Integer)));
        EntityTypeImpl entityType = new EntityTypeImpl(TestCase.class, null, new PersistentClassImpl("entity", "jpa"));
        entityType.getBuilder().addAttribute(implementor);
        entityType.getBuilder().addAttribute(implementor_name);

        return entityType;
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
    public void testJoin() {

        try {
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            EntityType one = getEntity();
            EntityType two = getEntity();
            Root srcRoot = query.from(one);
            Root tarRoot = query.from(two);
            Join join = srcRoot.join(two);
            join.on(cb.equal(tarRoot.get("id"),srcRoot.get("id")));
            query.select(srcRoot);
            String result = ((CriteriaQueryImpl) query).render(getContext());
            assertEquals("select generatedAlias0 from jpa as generatedAlias0, jpa as generatedAlias1 inner join jpa as generatedAlias2 with generatedAlias1.id=generatedAlias0.id",result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

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
    public void testMutiJoin() {
        try {
            AspireContext aspireContext = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(aspireContext);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            EntityType<PlainTable> one = getEntity();
            EntityType<PlainTable> two = getEntity();
            EntityType<PlainTable> three = getEntity();
            //查询的表，获得根
            Root<PlainTable> srcRoot = query.from(one);
            Root<PlainTable> tarRoot = query.from(two);
            Join join = srcRoot.join(two);
            join.on(cb.equal(tarRoot.get("id"), srcRoot.get("id"))).join(three);
            query.select(srcRoot);
            String result = ((CriteriaQueryImpl) query).render(getContext());
            assertEquals("select generatedAlias0 from jpa as generatedAlias0, jpa as generatedAlias1 inner join jpa as generatedAlias2 with generatedAlias1.id=generatedAlias0.id inner join jpa as generatedAlias3",result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

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
            public ExplicitParameterInfo registerExplicitParameter(ParameterExpression<?> criteriaQueryParameter) {
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

            public String getCastType(Class javaType) {

                return "castType";
            }
        };
    }

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
    public void testWhere() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.gt(root.get("id"), 2);
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext());
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id>2",result);
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
    /**
     * 添加断言
     * Detail:
     * Author:Osborn
     * Date:2016/7/5
     */
    public void testRootCount() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            Expression count = cb.count(root);
            query.select(count);
            String result = ((CriteriaQueryImpl) query).render(getContext());
            assertEquals("select count(*) from jpa as generatedAlias0",result);
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
    /**
     * 添加断言
     * Detail:
     * Author:Osborn
     * Date:2016/7/5
     */
    public void testCrossJoin() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            root.join(getEntity());
            query.select(root);
            String result = ((CriteriaQueryImpl) query).render(getContext());
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 inner join jpa as generatedAlias1",result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    private Object executeQuery(CriteriaQuery query) {
        return null;
    }

    private CriteriaBuilder generateCB() {
        CriteriaBuilder cb = EasyMock.createMock(CriteriaBuilder.class);
        return cb;
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/6/21
     */
    public void testSubquery() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);

            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();

            Subquery subquery = query.subquery(getEntity());
            Root root = subquery.from(getEntity());
            subquery.select(root.get("id"));
            Root main = query.from(getEntity());
            query.select(main);

            Predicate condition = cb.in(root.get("id")).value(subquery);
            query.where(condition);

            String result = ((CriteriaQueryImpl) query).render(getContext());
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testExists() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);

            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();

            Subquery subquery = query.subquery(getEntity());
            Root root = subquery.from(getEntity());
            subquery.select(root.get("id"));
            Root main = query.from(getEntity());
            query.select(main);

            Predicate condition = cb.exists(subquery);
            query.where(condition);

            String result = ((CriteriaQueryImpl) query).render(getContext());
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/6/21
     */
    public void testSubquerySubquery() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);

            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();

            Subquery subquery = query.subquery(getEntity());
            Root root = subquery.from(getEntity());
            subquery.select(root.get("id"));

            Subquery subsubquery = subquery.subquery(getEntity());
            Root subroot = subsubquery.from(getEntity());
            subsubquery.select(subroot.get("id"));
            Predicate subCondition = cb.in(root.get("id")).value(subsubquery);
            subquery.where(subCondition);
            Root main = query.from(getEntity());
            query.select(main);
            Predicate condition = cb.in(main.get("id")).value(subquery);
            query.where(condition);

            String result = ((CriteriaQueryImpl) query).render(getContext());
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
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.equal(root.get("id"), 2);
            query.where(condition);

            String result = ((CriteriaQueryImpl) query).render(getContext());
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
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.between(root.get("id"),1,3);
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext());
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id between 1 and 3",result);
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
    public void testWhereNotNull() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.isNotNull(root.get("id"));
            query.where(condition);

            String result = ((CriteriaQueryImpl) query).render(getContext());
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
    public void testWhereNull() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.isNull(root.get("id"));
            query.where(condition);

            String result = ((CriteriaQueryImpl) query).render(getContext());
            System.out.println(result);
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 where generatedAlias0.id is null",result);
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
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.like(root.get("id"), "%a");
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext());
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
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.notLike(root.get("id"), "%a_");
            query.where(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext());
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
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.like(root.get("id"), "%a");

            query.where(cb.and(condition, cb.equal(root.get("id"), 2)));

            String result = ((CriteriaQueryImpl) query).render(getContext());
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
    public void testSqrt() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(cb.sqrt(root.get("id")));
            String result = ((CriteriaQueryImpl) query).render(getContext());
            assertEquals("select sqrt(generatedAlias0.id) from jpa as generatedAlias0",result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testGroupBy() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            query.groupBy(root.get("A_name"));
            String result = ((CriteriaQueryImpl) query).render(getContext());
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 group by generatedAlias0.A_name",result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testHaving() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            query.groupBy(root.get("A_name"));
            Predicate condition = cb.like(root.get("A_name"), "_a%");
            query.having(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext());
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 group by generatedAlias0.A_name having generatedAlias0.A_name like :_a%",result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Author:Osborn
     * Date:2016/7/5
     */
    public void testOrderBy() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            query.orderBy(new OrderImpl(root.get("id"),true));
            String result = ((CriteriaQueryImpl) query).render(getContext());
            System.out.println(result);


        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
