package com.fr.bi.cluster.rpc;

import java.util.Set;

/**
 * Created by FineSoft on 2015/7/8.
 */
public interface BIRPCStunShopInter {
    void registerBook(Book book);

    Book getBookByName(String name);

    Set<Book> getBookByAuthor(String author);

    Set<Book> getBooks();

    Book getFirstBookByPrice(float price);

    Book getFirstBookByPage(int page);
}