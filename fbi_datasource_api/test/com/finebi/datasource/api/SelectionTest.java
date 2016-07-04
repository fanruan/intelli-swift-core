package com.finebi.datasource.api;


import com.finebi.datasource.api.criteria.*;
import com.finebi.datasource.api.metamodel.*;
import com.finebi.datasource.sql.criteria.AttributeTypeImpl;
import com.finebi.datasource.sql.criteria.internal.CriteriaQueryImpl;
import com.finebi.datasource.sql.criteria.internal.compile.ExplicitParameterInfo;
import com.finebi.datasource.sql.criteria.internal.compile.ImplicitParameterBinding;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.context.AspirContextImpl;
import com.finebi.datasource.sql.criteria.internal.context.AspireContext;
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
    public void testSelect() {
        try {
            AspireContext context = new AspirContextImpl();
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


    public EntityType getEntity() {
        AttributeFactory factory = new AttributeFactory(null);
        AttributeImplementor implementor = factory.buildAttribute(null, new AttributePropertyImpl("id", false, new AttributeTypeImpl(AttributeType.InnerType.Integer)));
        EntityTypeImpl entityType = new EntityTypeImpl(TestCase.class, null, new PerisitentClassImpl());
        entityType.getBuilder().addAttribute(implementor);
        return entityType;
    }

    /**
     * 关联的使用
     * Detail:
     * Author:Connery
     * Date:2016/6/23
     */
    public void testJoin() {
        try {
            PlainTable tar = EasyMock.createMock(PlainTable.class);
            PlainTable src = EasyMock.createMock(PlainTable.class);
            CriteriaBuilder cb = generateCB();
            CriteriaQuery<PlainTable> query = cb.createQuery();


            //查询的表，获得根
//            Root<PlainTable> srcRoot = query.from(src);
//            Root<PlainTable> tarRoot = query.from(tar);
//
//
//            查询的结果元数据
//            query.select(srcRoot);
//
//            对查询根进行关联操作
//            Join join = srcRoot.join(tar);
//            join.on(cb.equal(tarRoot.get("id"), srcRoot.get("id")));
            //获得结果
            Object result = executeQuery(query);
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
    public void testMutiJoin() {
        try {
            PlainTable one = EasyMock.createMock(PlainTable.class);
            PlainTable two = EasyMock.createMock(PlainTable.class);
            PlainTable three = EasyMock.createMock(PlainTable.class);

            CriteriaBuilder cb = generateCB();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            //查询的表，获得根
//            Root<PlainTable> srcRoot = query.from(one);
//            Root<PlainTable> tarRoot = query.from(two);
//            Join join = srcRoot.join(two);
//            对关联进行再次关联操作
//            join.on(cb.equal(tarRoot.get("id"), srcRoot.get("id"))).join(three);
//            query.select(srcRoot);
            Object result = executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    private RenderingContext getContext() {
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
                return parameterName;
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
    public void testWhere() {
        try {
            AspireContext context = new AspirContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            Predicate condition = cb.gt(root.get("id"), 2);
            query.where(condition);

            String result = ((CriteriaQueryImpl) query).render(getContext());
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }


    public void testRootCount() {
        try {
            AspireContext context = new AspirContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            Expression count = cb.count(root);
            query.select(count);
            String result = ((CriteriaQueryImpl) query).render(getContext());
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testCrossJoin() {
        try {
            AspireContext context = new AspirContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            root.join(getEntity());
            query.select(root);
            String result = ((CriteriaQueryImpl) query).render(getContext());
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
            AspireContext context = new AspirContextImpl();
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

    /**
     * Detail:
     * Author:Connery
     * Date:2016/6/21
     */
    public void testSubquerySubquery() {
        try {
            AspireContext context = new AspirContextImpl();
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
}
