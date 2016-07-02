package com.finebi.datasource.api;


import com.finebi.datasource.api.criteria.CriteriaBuilder;
import com.finebi.datasource.api.criteria.CriteriaQuery;
import com.finebi.datasource.api.criteria.ParameterExpression;
import com.finebi.datasource.api.criteria.Root;
import com.finebi.datasource.api.metamodel.EntityManager;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.PlainTable;
import com.finebi.datasource.sql.criteria.internal.CriteriaQueryImpl;
import com.finebi.datasource.sql.criteria.internal.compile.ExplicitParameterInfo;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.context.AspirContextImpl;
import com.finebi.datasource.sql.criteria.internal.context.AspireContext;
import com.finebi.datasource.sql.criteria.internal.metamodel.*;
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
            query.select(root);
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
        AttributeImplementor implementor = factory.buildAttribute(null, new PropertyImpl("abc", false));
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

    private Object executeQuery(CriteriaQuery query) {
        return null;
    }

    private CriteriaBuilder generateCB() {
        CriteriaBuilder cb = EasyMock.createMock(CriteriaBuilder.class);
        return cb;
    }


}
