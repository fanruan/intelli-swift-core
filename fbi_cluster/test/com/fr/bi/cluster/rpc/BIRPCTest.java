package com.fr.bi.cluster.rpc;

import junit.framework.TestCase;

/**
 * Created by FineSoft on 2015/7/8.
 */
public class BIRPCTest extends TestCase {
    public void testRpc() {
        Book a = new Book();
        a.setName("PROCESSOR");
        a.setAuthor("doug lea");
        a.setPage(100);
        a.setPrice(12.2f);
        Book b = new Book();
        b.setName("GNU");
        b.setAuthor("stallman");
        b.setPage(200);
        b.setPrice(16.2f);
        Book c = new Book();
        c.setName("GNULINUX");
        c.setAuthor("linus");
        c.setPage(210);
        c.setPrice(14.2f);
        Book d = new Book();
        d.setName("GIT");
        d.setAuthor("linus");
        d.setPage(300);
        d.setPrice(14.5f);
        BIRPCStunShop shop = new BIRPCStunShop();
        shop.registerBook(a);
        shop.registerBook(b);
        shop.registerBook(c);
        shop.registerBook(d);
        BIRPC.registerStun(shop, 12312);
        BIRPCStunShopInter shopServer = (BIRPCStunShopInter) BIRPC.getProxy(BIRPCStunShop.class, "127.0.0.1", 12312);
        assertEquals(shopServer.getBookByName("GNU").getAuthor(), "stallman");
        assertEquals(shopServer.getBookByAuthor("linus").size(), 2);
        assertEquals(shopServer.getBooks().size(), 4);
        assertEquals((int) shopServer.getBookByName("GIT").getPage(), 300);
        assertEquals(shopServer.getBookByName("PROCESSOR").getPrice(), 12.2f);
        assertEquals(shopServer.getFirstBookByPrice(12.2f).getName(), "PROCESSOR");
        assertEquals(shopServer.getFirstBookByPage(300).getName(), "GIT");
        BIRPC.release();
    }
}