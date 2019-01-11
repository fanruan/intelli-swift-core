package com.fr.swift.config.convert;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

/**
 * @author yee
 * @date 2019-01-08
 */
public class URIConverterTest {
    private URI uri = URI.create("file://seg0");
    private String json = "\"file://seg0\"";

    @Test
    public void convertToDatabaseColumn() {
        String json = new URIConverter().convertToDatabaseColumn(uri);
        assertEquals(this.json, json);
    }

    @Test
    public void convertToEntityAttribute() {
        URI uri = new URIConverter().convertToEntityAttribute(json);
        assertEquals(this.uri, uri);
    }
}