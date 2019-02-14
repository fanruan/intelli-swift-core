package com.fr.swift.beans.factory.classreading;

public class Type {

    public static final int VOID = 0;

    public static final int BOOLEAN = 1;

    public static final int CHAR = 2;

    public static final int BYTE = 3;

    public static final int SHORT = 4;

    public static final int INT = 5;

    public static final int FLOAT = 6;

    public static final int LONG = 7;

    public static final int DOUBLE = 8;

    public static final int ARRAY = 9;

    public static final int OBJECT = 10;

    public static final int METHOD = 11;

    public static final Type VOID_TYPE = new Type(VOID, null, ('V' << 24)
            | (5 << 16) | (0 << 8) | 0, 1);

    public static final Type BOOLEAN_TYPE = new Type(BOOLEAN, null, ('Z' << 24)
            | (0 << 16) | (5 << 8) | 1, 1);

    public static final Type CHAR_TYPE = new Type(CHAR, null, ('C' << 24)
            | (0 << 16) | (6 << 8) | 1, 1);

    public static final Type BYTE_TYPE = new Type(BYTE, null, ('B' << 24)
            | (0 << 16) | (5 << 8) | 1, 1);

    public static final Type SHORT_TYPE = new Type(SHORT, null, ('S' << 24)
            | (0 << 16) | (7 << 8) | 1, 1);

    public static final Type INT_TYPE = new Type(INT, null, ('I' << 24)
            | (0 << 16) | (0 << 8) | 1, 1);
    public static final Type FLOAT_TYPE = new Type(FLOAT, null, ('F' << 24)
            | (2 << 16) | (2 << 8) | 1, 1);
    public static final Type LONG_TYPE = new Type(LONG, null, ('J' << 24)
            | (1 << 16) | (1 << 8) | 2, 1);

    public static final Type DOUBLE_TYPE = new Type(DOUBLE, null, ('D' << 24)
            | (3 << 16) | (3 << 8) | 2, 1);

    private final int sort;

    private final char[] buf;
    private final int off;

    private final int len;


    private Type(final int sort, final char[] buf, final int off, final int len) {
        this.sort = sort;
        this.buf = buf;
        this.off = off;
        this.len = len;
    }

    public static Type getType(final String typeDescriptor) {
        return getType(typeDescriptor.toCharArray(), 0);
    }

    private static Type getType(final char[] buf, final int off) {
        int len;
        switch (buf[off]) {
            case 'V':
                return VOID_TYPE;
            case 'Z':
                return BOOLEAN_TYPE;
            case 'C':
                return CHAR_TYPE;
            case 'B':
                return BYTE_TYPE;
            case 'S':
                return SHORT_TYPE;
            case 'I':
                return INT_TYPE;
            case 'F':
                return FLOAT_TYPE;
            case 'J':
                return LONG_TYPE;
            case 'D':
                return DOUBLE_TYPE;
            case '[':
                len = 1;
                while (buf[off + len] == '[') {
                    ++len;
                }
                if (buf[off + len] == 'L') {
                    ++len;
                    while (buf[off + len] != ';') {
                        ++len;
                    }
                }
                return new Type(ARRAY, buf, off, len + 1);
            case 'L':
                len = 1;
                while (buf[off + len] != ';') {
                    ++len;
                }
                return new Type(OBJECT, buf, off + 1, len - 1);
            // case '(':
            default:
                return new Type(METHOD, buf, off, buf.length - off);
        }
    }

    public int getDimensions() {
        int i = 1;
        while (buf[off + i] == '[') {
            ++i;
        }
        return i;
    }

    public Type getElementType() {
        return getType(buf, off + getDimensions());
    }

    public String getClassName() {
        switch (sort) {
            case VOID:
                return "void";
            case BOOLEAN:
                return "boolean";
            case CHAR:
                return "char";
            case BYTE:
                return "byte";
            case SHORT:
                return "short";
            case INT:
                return "int";
            case FLOAT:
                return "float";
            case LONG:
                return "long";
            case DOUBLE:
                return "double";
            case ARRAY:
                StringBuilder sb = new StringBuilder(getElementType().getClassName());
                for (int i = getDimensions(); i > 0; --i) {
                    sb.append("[]");
                }
                return sb.toString();
            case OBJECT:
                return new String(buf, off, len).replace('/', '.').intern();
            default:
                return null;
        }
    }
}
