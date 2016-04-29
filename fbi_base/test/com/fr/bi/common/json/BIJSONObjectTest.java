package com.fr.bi.common.json;

import com.fr.bi.common.persistent.json.generator.tool.BIJSONAutoTool;
import com.fr.bi.common.world.Book;
import com.fr.bi.common.world.BookRack;
import com.fr.bi.common.world.people.Student;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Connery on 2015/12/18.
 */
public class BIJSONObjectTest extends TestCase {

    public void testIteratorJson() {
        String list = generateString(Book.getListThreeRomanceOfThreeKingdomsBook());
        String array = generateString(Book.getArrayThreeRomanceOfThreeKingdomsBook());
        assertEquals(list, array);

    }

    private String generateString(Object obj) {
        try {
            String result = BIJSONAutoTool.generateJSON(obj, "", false).toString();
            BILogger.getLogger().info(result);
            return result;
        } catch (Exception ex) {
            BILogger.getLogger().error(ex.getMessage(), ex);
        }
        return "";
    }

    public void testArrayJson() {
        Book[] books = new Book[3];
        books[0] = (Book.getRomanceOfThreeKingdomsBook());
        books[1] = (Book.getRomanceOfThreeKingdomsBook());
        books[2] = (Book.getRomanceOfThreeKingdomsBook());

        try {
            BILogger.getLogger().info(BIJSONAutoTool.generateJSON(books, "", false).toString());
        } catch (Exception ex) {
            BILogger.getLogger().error(ex.getMessage(), ex);
        }
    }

    public void testListInObj() {
        try {
            String str = generateString(BookRack.getOneBookRack());
            Object jsonObj = BIJSONAutoTool.generateObject(str, BookRack.class);
            String str_2 = generateString(jsonObj);
            assertEquals(str, str_2);
//            Object jsonObj_2 = BIJSONAutoTool.generateObject(str_2, BookRack.class);
//            assertTrue(BIComparatorUtils.isExactlyEquals(jsonObj, jsonObj_2));

        } catch (Exception ignore) {

        }
    }

    public void testMapInObj() {
        generateString(BookRack.getOneBookRackWithCategory());
    }

    public void testMapJson() {
        Map<String, Book> map = new HashMap<String, Book>();
        map.put("one", Book.getOneBook());
        map.put("two", Book.getOneBook());
        map.put("three", Book.getOneBook());
        try {
            BILogger.getLogger().info(BIJSONAutoTool.generateJSON(map, "", false).toString());
        } catch (Exception ex) {
            BILogger.getLogger().error(ex.getMessage(), ex);
        }
    }

    public void testSampleObject() {
        Book book = Book.getOneBook();
        try {

            String str = BIJSONAutoTool.generateJSON(book, "", false).toString();
            Object object = BIJSONAutoTool.generateObject(str, Book.class);
            BILogger.getLogger().info(BIJSONAutoTool.generateJSON(book, "", false).toString());
        } catch (Exception ex) {
            BILogger.getLogger().error(ex.getMessage(), ex);
        }
    }

    public static Student getStudent() {
        Student student = new Student();
        student.setAge(10);
        student.setName("root");
        student.setChildName("child");
        Book book = new Book();
        book.setBookName("Romance of the Three Kingdoms");
        book.setOwner(student);
        Book book2 = new Book();
        book2.setBookName("Dream of the Red Chamber");
        book2.setOwner(student);
        student.setFavoriteBook(book);
        BookRack bookRack = new BookRack();
        ArrayList<Book> books = new ArrayList<Book>();
        books.add(book);
        books.add(book2);
        bookRack.setBooks(books);
        bookRack.setOwner(student);
        student.setBookRack(bookRack);
        return student;
    }
}