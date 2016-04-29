package com.fr.bi.common.world;

import com.fr.bi.common.persistent.json.analyser.anno.BIElementType;
import com.fr.bi.common.persistent.json.generator.anno.BIJSONElement;
import com.fr.bi.common.world.people.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Connery on 2015/12/18.
 */
public class BookRack {
    @BIJSONElement
    @BIElementType(implemented = ArrayList.class, genericType = Book.class)
    private List<Book> books;
    @BIJSONElement
    private Person owner;
    @BIJSONElement
    private Map<BookTag, List<Book>> category;

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public List<Book> getBooks() {
        return books;
    }

    public Map<BookTag, List<Book>> getCategory() {
        return category;
    }

    public void setCategory(Map<BookTag, List<Book>> category) {
        this.category = category;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }


    public static BookRack getOneBookRack() {
        BookRack rack = new BookRack();
        rack.setBooks(Book.getListThreeRomanceOfThreeKingdomsBook());
        rack.setOwner(Person.getAB());
        return rack;
    }

    public static BookRack getOneBookRackWithCategory() {
        BookRack rack = new BookRack();
        Map<BookTag, List<Book>> category = new HashMap<BookTag, List<Book>>();
        category.put(BookTag.getRomanceTag(), Book.getRomanceBooks());
        category.put(BookTag.getTechnicTag(), Book.getTechnicBooks());
        rack.setCategory(category);
        return rack;
    }
}