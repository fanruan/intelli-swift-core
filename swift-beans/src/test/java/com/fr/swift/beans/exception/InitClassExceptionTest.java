package com.fr.swift.beans.exception;

import org.junit.Test;

public class InitClassExceptionTest {
    @Test
    public void test(){
        InitClassException exception=new InitClassException("initclassException");
        assert exception.getMessage().equals("initclassException");
    }
}
