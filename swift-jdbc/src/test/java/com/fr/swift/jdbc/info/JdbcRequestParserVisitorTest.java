package com.fr.swift.jdbc.info;

import com.fr.swift.api.info.ApiInvocation;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import static junit.framework.TestCase.assertTrue;

/**
 * @author yee
 * @date 2019-01-11
 */
public class JdbcRequestParserVisitorTest {

    private JdbcRequestParserVisitor visitor;

    @Before
    public void setUp() {
        // Generate by Mock Plugin
        // Generate by Mock Plugin
        ApiInvocation mockApiInvocation = PowerMock.createMock(ApiInvocation.class);
        visitor = PowerMock.createMock(JdbcRequestParserVisitor.class);
        EasyMock.expect(visitor.visit(EasyMock.notNull(SqlRequestInfo.class))).andReturn(mockApiInvocation).once();
        EasyMock.expect(visitor.visit(EasyMock.isNull(SqlRequestInfo.class))).andThrow(new RuntimeException()).once();
        EasyMock.expect(visitor.visit(EasyMock.notNull(ColumnsRequestInfo.class))).andReturn(mockApiInvocation).once();
        EasyMock.expect(visitor.visit(EasyMock.isNull(ColumnsRequestInfo.class))).andThrow(new RuntimeException()).once();
        EasyMock.expect(visitor.visit(EasyMock.notNull(TablesRequestInfo.class))).andReturn(mockApiInvocation).once();
        EasyMock.expect(visitor.visit(EasyMock.isNull(TablesRequestInfo.class))).andThrow(new RuntimeException()).once();
        PowerMock.replayAll();
    }

    @Test
    public void visitTest() {
        new SqlRequestInfo().accept(visitor);
        boolean exp = false;
        try {
            visit((SqlRequestInfo) null);
        } catch (Exception e) {
            exp = true;
        }
        assertTrue(exp);
        exp = false;
        new ColumnsRequestInfo().accept(visitor);
        try {
            visit((ColumnsRequestInfo) null);
        } catch (Exception e) {
            exp = true;
        }
        assertTrue(exp);
        exp = false;
        new TablesRequestInfo().accept(visitor);
        try {
            visit((TablesRequestInfo) null);
        } catch (Exception e) {
            exp = true;
        }
        assertTrue(exp);
        PowerMock.verifyAll();
    }


    void visit(SqlRequestInfo queryRequestInfo) {
        visitor.visit(queryRequestInfo);
    }

    void visit(ColumnsRequestInfo createTableRequestInfo) {
        visitor.visit(createTableRequestInfo);
    }

    void visit(TablesRequestInfo deleteRequestInfo) {
        visitor.visit(deleteRequestInfo);
    }
}