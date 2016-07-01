package com.finebi.datasource.api;


import com.finebi.datasource.api.criteria.CriteriaBuilder;
import com.finebi.datasource.api.criteria.CriteriaQuery;
import com.finebi.datasource.api.metamodel.EntityManager;
import com.finebi.datasource.api.metamodel.PlainTable;
import com.finebi.datasource.sql.criteria.internal.context.AspirContextImpl;
import com.finebi.datasource.sql.criteria.internal.context.AspireContext;
import com.finebi.datasource.sql.criteria.internal.metamodel.EntityManagerImpl;
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


//            查询的表，获得根
//            Root root = query.from(target);

//            查询的结果元数据
//            query.select(root);
            Object result = executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
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
