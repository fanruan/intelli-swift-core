package com.finebi.datasource.api;


import com.finebi.datasource.api.criteria.*;
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
            PlainTable target = EasyMock.createMock(PlainTable.class);
            PlainResultMate resultMate = EasyMock.createMock(PlainResultMate.class);

            CriteriaBuilder cb = generateCB();

            CriteriaQuery<PlainTable> query = cb.createQuery();
            //查询的结果元数据
            query.select(resultMate);

            //查询的表，获得根
            query.from(target);
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
            PlainResultMate resultMate = EasyMock.createMock(PlainResultMate.class);
            CriteriaBuilder cb = generateCB();
            CriteriaQuery<PlainTable> query = cb.createQuery();

            //查询的结果元数据
            query.select(resultMate);

            //查询的表，获得根
            Root<PlainTable> root = query.from(src);

            //对查询根进行关联操作
            Join join = root.join(src);
            join.on(cb.equal(tar.getColumn("id"), src.getColumn("id")));
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
            CriteriaQuery<PlainTable> query = cb.createQuery(PlainTable.class);
            Root<PlainTable> root = query.from(two);
            Join join = root.join(two);
            //对关联进行再次关联操作
            join.on(cb.equal(one.getColumn("id"), two.getColumn("id"))).join(three);
            query.select(one);
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
