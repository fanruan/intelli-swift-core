package com.fr.bi.common.world;

import com.fr.bi.common.persistent.json.generator.anno.BIJSONElement;
import com.fr.bi.common.world.people.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connery on 2015/12/18.
 */
public class Book {
    @BIJSONElement
    private String bookName;
    @BIJSONElement
    private float price;
    @BIJSONElement
    private Person owner;

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public static Book getOneBook() {
        Book book = new Book();
        book.setBookName("Romance of the Three Kingdoms");
        book.setPrice(29.3f);
        book.setOwner(Person.getABConnectDenChao());
        return book;
    }

    public static Book getRomanceOfThreeKingdomsBook() {
        Book book = new Book();
        book.setBookName("Romance of the Three Kingdoms");
        book.setPrice(29.3f);
        return book;
    }

    public static Book getUnixBook() {
        Book book = new Book();
        book.setBookName("Unix");
        book.setPrice(12.3f);
        return book;
    }

    public static Book getLinuxBook() {
        Book book = new Book();
        book.setBookName("linux");
        book.setPrice(19.3f);
        return book;
    }

    public static Book getDreamOfRedChamberBook() {
        Book book = new Book();
        book.setBookName("Dream of the Red Chamber");
        book.setPrice(23.3f);
        return book;
    }

    public static List<Book> getRomanceBooks() {
        List<Book> books = new ArrayList<Book>();
        books.add(Book.getRomanceOfThreeKingdomsBook());
        books.add(Book.getDreamOfRedChamberBook());
        return books;
    }

    public static List<Book> getTechnicBooks() {
        List<Book> books = new ArrayList<Book>();
        books.add(Book.getLinuxBook());
        books.add(Book.getUnixBook());
        return books;
    }

    public static List<Book> getListThreeRomanceOfThreeKingdomsBook() {
        List<Book> books = new ArrayList<Book>();
        books.add(Book.getRomanceOfThreeKingdomsBook());
        books.add(Book.getRomanceOfThreeKingdomsBook());
        books.add(Book.getRomanceOfThreeKingdomsBook());
        return books;
    }

    public static Book[] getArrayThreeRomanceOfThreeKingdomsBook() {
        Book[] books = new Book[3];
        books[0] = (Book.getRomanceOfThreeKingdomsBook());
        books[1] = (Book.getRomanceOfThreeKingdomsBook());
        books[2] = (Book.getRomanceOfThreeKingdomsBook());
        return books;
    }
}