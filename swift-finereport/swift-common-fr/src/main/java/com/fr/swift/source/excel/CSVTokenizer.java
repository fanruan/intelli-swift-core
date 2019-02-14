package com.fr.swift.source.excel;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Created by Young's on 2016/7/19.
 */
public class CSVTokenizer implements Enumeration {
    private static final String COMMA = ",";
    private static final String TAB = "\t";
    private static final String SPACE = " ";
    private static final String DOUBLE_QUATE = "\"";
    private static final String SINGLE_QUATE = "'";
    private String record;
    private String separator;
    private String quate;
    private boolean returnSeparators;
    private int currentIndex;

    public CSVTokenizer(String aString, String theSeparator, String theQuate, boolean fragReturnSeparators) {
        this.record = aString.trim();
        this.separator = theSeparator;
        this.quate = theQuate;
        this.returnSeparators = fragReturnSeparators;
        this.currentIndex = 0;
    }

    public CSVTokenizer(String aString, String theSeparator, boolean fragReturnSeparators) {
        this(
                aString,
                theSeparator,
                "\"",
                fragReturnSeparators);
    }

    public CSVTokenizer(String aString, String theSeparator) {
        this(aString, theSeparator, false);
    }

    public CSVTokenizer(String aString) {
        this(aString, ",");
    }

    public boolean hasMoreTokens() {
        return this.currentIndex < this.record.length();
    }

    public String nextToken()
            throws NoSuchElementException, IllegalArgumentException {
        String token = null;

        if (!hasMoreTokens()) {
            throw new NoSuchElementException();
        }
        if (this.record.startsWith(this.quate, this.currentIndex)) {
            String rec = this.record.substring(this.currentIndex + this.quate.length());
            token = "";
            while (true) {
                int end = rec.indexOf(this.quate);
                if (end < 0) {
                    throw new IllegalArgumentException("Illegal format");
                }
                if (!rec.startsWith(this.quate, end + 1)) {
                    token = token + rec.substring(0, end);
                    break;
                }
                token = token + rec.substring(0, end + 1);
                rec = rec.substring(end + this.quate.length() * 2);
                this.currentIndex += 1;
            }
            this.currentIndex += token.length() + this.quate.length() * 2 + this.separator.length();
        } else {
            int end;
            if ((end = this.record.indexOf(this.separator, this.currentIndex)) >= 0) {
                int start = this.currentIndex;
                token = this.record.substring(start, end);
                this.currentIndex = (end + this.separator.length());
            } else {
                int start = this.currentIndex;
                token = this.record.substring(start);
                this.currentIndex = this.record.length();
            }
        }

        return token;
    }

    public String nextToken(String theSeparator) {
        this.separator = theSeparator;
        return nextToken();
    }

    public boolean hasMoreElements() {
        return hasMoreTokens();
    }

    public Object nextElement() {
        return nextToken();
    }

    public int countTokens() {
        int count = 0;

        int preserve = this.currentIndex;
        while (hasMoreTokens()) {
            nextToken();
            count++;
        }
        this.currentIndex = preserve;

        return count;
    }

    public String getQuate() {
        return this.quate;
    }

    public void setQuate(String quate) {
        this.quate = quate;
    }
}
