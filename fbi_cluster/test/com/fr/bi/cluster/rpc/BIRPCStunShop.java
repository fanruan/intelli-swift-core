package com.fr.bi.cluster.rpc;

import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * Created by FineSoft on 2015/7/8.
 */
public class BIRPCStunShop implements BIRPCStunShopInter {
    private Map<String, Book> container = new HashMap<String, Book>();

    public void registerBook(Book book) {
        container.put(book.getName(), book);
    }

    public Book getBookByName(String name) {
        return container.get(name);
    }

    public Set<Book> getBookByAuthor(String author) {
        Iterator<Book> it = container.values().iterator();
        Set<Book> result = new HashSet<Book>();
        while (it.hasNext()) {
            Book temp = it.next();
            if (ComparatorUtils.equals(temp.getAuthor(), author)) {
                result.add(temp);
            }
        }
        return result;
    }

    public Set<Book> getBooks() {
        Iterator<Book> it = container.values().iterator();
        Set<Book> result = new HashSet<Book>();
        while (it.hasNext()) {
            Book temp = it.next();
            result.add(temp);
        }
        return result;
    }

    public Book getFirstBookByPrice(float price) {
        Iterator<Book> it = container.values().iterator();
        Set<Book> result = new HashSet<Book>();
        while (it.hasNext()) {
            Book temp = it.next();
            if (temp.getPrice() == price) {
                return temp;
            }
        }
        return null;
    }

    public Book getFirstBookByPage(int page) {
        Iterator<Book> it = container.values().iterator();
        Set<Book> result = new HashSet<Book>();
        while (it.hasNext()) {
            Book temp = it.next();
            if (temp.getPage() == page) {
                return temp;
            }
        }
        return null;
    }
}