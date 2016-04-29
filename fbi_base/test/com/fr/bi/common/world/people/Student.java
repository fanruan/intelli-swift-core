package com.fr.bi.common.world.people;

import com.fr.bi.common.json.BIPlainRoot4Test;
import com.fr.bi.common.world.Book;
import com.fr.bi.common.world.BookRack;

/**
 * Created by Connery on 2015/12/18.
 */
public class Student extends Person {
    private String childName;
    private int age;
    private Book favoriteBook;
    private BookRack bookRack;
    private BIPlainRoot4Test plainRoot4Test;

    public BookRack getBookRack() {
        return bookRack;
    }

    public void setBookRack(BookRack bookRack) {
        this.bookRack = bookRack;
    }

    public Book getFavoriteBook() {
        return favoriteBook;
    }

    public void setFavoriteBook(Book favoriteBook) {
        this.favoriteBook = favoriteBook;
    }

    public BIPlainRoot4Test getPlainRoot4Test() {
        return plainRoot4Test;
    }

    public void setPlainRoot4Test(BIPlainRoot4Test plainRoot4Test) {
        this.plainRoot4Test = plainRoot4Test;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}