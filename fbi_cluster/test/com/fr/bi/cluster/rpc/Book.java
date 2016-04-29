package com.fr.bi.cluster.rpc;

import java.io.Serializable;

/**
 * Created by FineSoft on 2015/7/8.
 */
public class Book implements Serializable {
    private Integer page;
    private String name;
    private String author;
    private float price;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}