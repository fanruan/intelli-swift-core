package com.fr.swift.base.json.token;

import com.fr.swift.base.json.exception.JsonParseException;
import com.fr.swift.base.json.reader.CharReader;

import java.io.IOException;

/**
 * @author yee
 * @date 2018-12-13
 */
public class Tokenizer {
    private static final double MAX_SAFE_DOUBLE = 1.7976931348623157e+308;
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;
    private CharReader charReader;

    public Tokenizer(CharReader charReader) {
        this.charReader = charReader;
    }

    private boolean isWhiteSpace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
    }

    public Token nextToken() throws IOException {
        char ch;
        for (; ; ) {
            if (!charReader.hasMore()) {
                return new Token<String>(TokenType.END_DOCUMENT, "EOF");
            }

            ch = charReader.next();
            if (!isWhiteSpace(ch)) {
                break;
            }
        }
        switch (ch) {
            case '{':
                return new Token<String>(TokenType.BEGIN_OBJECT, String.valueOf(ch));
            case '}':
                return new Token<String>(TokenType.END_OBJECT, String.valueOf(ch));
            case '[':
                return new Token<String>(TokenType.BEGIN_ARRAY, String.valueOf(ch));
            case ']':
                return new Token<String>(TokenType.END_ARRAY, String.valueOf(ch));
            case ',':
                return new Token<String>(TokenType.SEP_COMMA, String.valueOf(ch));
            case ':':
                return new Token<String>(TokenType.SEP_COLON, String.valueOf(ch));
            case 'n':
                return readNull();
            case 't':
            case 'f':
                return readBoolean();
            case '"':
                return readString();
            case '-':
                return readNumber();
            default:
        }

        if (isDigit(ch)) {
            return readNumber();
        }

        throw new JsonParseException("Illegal character");
    }

    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private Token readNull() throws IOException {
        if (!(charReader.next() == 'u' && charReader.next() == 'l' && charReader.next() == 'l')) {
            throw new JsonParseException("Invalid json string");
        }

        return new Token<String>(TokenType.NULL, "null");
    }

    private Token readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (; ; ) {
            char ch = charReader.next();
            // 处理转义字符
            if (ch == '\\') {
                if (!isEscape()) {
                    throw new JsonParseException("Invalid escape character");
                }
                sb.append('\\');
                ch = charReader.peek();
                sb.append(ch);
                // 处理 Unicode 编码，形如 \u4e2d。且只支持 \u0000 ~ \uFFFF 范围内的编码
                if (ch == 'u') {
                    for (int i = 0; i < 4; i++) {
                        ch = charReader.next();
                        if (isHex(ch)) {
                            sb.append(ch);
                        } else {
                            throw new JsonParseException("Invalid character");
                        }
                    }
                }
            } else if (ch == '"') {
                // 碰到另一个双引号，则认为字符串解析结束，返回 Token
                return new Token(TokenType.STRING, sb.toString());
            } else if (ch == '\r' || ch == '\n') {
                // 传入的 JSON 字符串不允许换行
                throw new JsonParseException("Invalid character");
            } else {
                sb.append(ch);
            }
        }
    }

    private boolean isEscape() throws IOException {
        char ch = charReader.next();
        return (ch == '"' || ch == '\\' || ch == 'u' || ch == 'r'
                || ch == 'n' || ch == 'b' || ch == 't' || ch == 'f');
    }

    private boolean isHex(char ch) {
        return ((ch >= '0' && ch <= '9') || ('a' <= ch && ch <= 'f')
                || ('A' <= ch && ch <= 'F'));
    }

    private Token readBoolean() throws IOException {
        charReader.back();
        char ch = charReader.next();
        String expected = null;
        if (ch == 't') {
            // true
            expected = "rue";
        } else if (ch == 'f') {
            // false
            expected = "alse";
        } else {
            throw new JsonParseException("Unexpected char: " + ch);
        }
        for (int i = 0; i < expected.length(); i++) {
            char theChar = charReader.next();
            if (theChar != expected.charAt(i)) {
                throw new JsonParseException("Unexpected char: " + theChar);
            }
        }
        return new Token<Boolean>(TokenType.BOOLEAN, ch == 't');
    }

    private Token readNumber() throws IOException {
        // ###.xxxExxx
        StringBuilder intPart = new StringBuilder(10);
        // xxx.###Exxx
        StringBuilder fraPart = new StringBuilder(10);
        // xxx.xxxE###
        StringBuilder expPart = new StringBuilder(10);
        boolean hasFraPart = false;
        boolean hasExpPart = false;
        charReader.back();
        char ch = charReader.peek();
        boolean minusSign = ch == '-';
        boolean expMinusSign = false;
        if (minusSign) {
            charReader.next();
        }
        ReadNumStatus status = ReadNumStatus.READ_NUMBER_INT_PART;
        for (; ; ) {
            if (charReader.hasMore()) {
                ch = charReader.peek();
            } else {
                status = ReadNumStatus.READ_NUMBER_END;
            }
            switch (status) {
                case READ_NUMBER_INT_PART:
                    ReadNumResult result = readNumber(ch, intPart, status, hasExpPart, expMinusSign);
                    status = result.getStatus();
                    hasExpPart = result.isHasExpPart();
                    expMinusSign = result.isExpMinusSign();
                    hasFraPart = result.isHasFraPart();
                    continue;
                case READ_NUMBER_FRA_PART:
                    ReadNumResult fra = readNumber(ch, fraPart, status, hasExpPart, expMinusSign);
                    status = fra.getStatus();
                    hasExpPart = fra.isHasExpPart();
                    expMinusSign = fra.isExpMinusSign();
                    continue;
                case READ_NUMBER_EXP_PART:
                    if (ch >= '0' && ch <= '9') {
                        if (expPart == null) {
                            expPart = new StringBuilder(10);
                        }
                        expPart.append(charReader.next());
                    } else {
                        if (expPart == null) {
                            throw new JsonParseException("Unexpected char: "
                                    + charReader.next());
                        }
                        // end of number:
                        status = ReadNumStatus.READ_NUMBER_END;
                    }
                    continue;
                case READ_NUMBER_END:
                    // build parsed number:
                    if (intPart == null) {
                        throw new JsonParseException("Missing integer part of number.");
                    }
                    long lInt = minusSign ? -string2Long(intPart)
                            : string2Long(intPart);
                    if (!hasFraPart && !hasExpPart) {
                        return new Token<Number>(TokenType.NUMBER, lInt);
                    }
                    if (hasFraPart && fraPart == null) {
                        throw new JsonParseException("Missing fraction part of number.");
                    }
                    double dFraPart = hasFraPart ? (minusSign ? -string2Fraction(fraPart)
                            : string2Fraction(fraPart))
                            : 0.0;
                    double number = hasExpPart ? (lInt + dFraPart)
                            * Math.pow(10, expMinusSign ? -string2Long(expPart) : string2Long(expPart))
                            : (lInt + dFraPart);
                    if (number > MAX_SAFE_DOUBLE) {
                        throw new NumberFormatException(
                                "Exceeded maximum value: 1.7976931348623157e+308");
                    }
                    return new Token<Number>(TokenType.NUMBER, number);
                default:
            }
            continue;
        }
    }

    /**
     * parse "0123" as 123:
     *
     * @param cs
     * @return
     */
    private long string2Long(CharSequence cs) {
        if (cs.length() > 16) {
            throw new JsonParseException("Number string is too long.");
        }
        long n = 0;
        for (int i = 0; i < cs.length(); i++) {
            n = n * 10 + (cs.charAt(i) - '0');
            if (n > MAX_SAFE_INTEGER) {
                throw new JsonParseException("Exceeded maximum value: "
                        + MAX_SAFE_INTEGER);
            }
        }
        return n;
    }


    /**
     * parse "0123" as 0.0123
     *
     * @param cs
     * @return
     */
    private double string2Fraction(CharSequence cs) {
        if (cs.length() > 16) {
            throw new JsonParseException("Number string is too long.");
        }
        double d = 0.0;
        for (int i = 0; i < cs.length(); i++) {
            int n = cs.charAt(i) - '0';
            d = d + (n == 0 ? 0 : n / Math.pow(10, i + 1));
        }
        return d;
    }

    private ReadNumResult readNumber(char ch, StringBuilder builder, ReadNumStatus status, boolean hasExpPart, boolean expMinusSign) throws IOException {
        boolean hasFraPart = false;
        if (isDigit(ch)) {
            if (builder == null) {
                builder = new StringBuilder(10);
            }
            builder.append(charReader.next());
        } else if (ch == '.') {
            charReader.next();
            hasFraPart = true;
            status = ReadNumStatus.READ_NUMBER_FRA_PART;
        } else if (ch == 'e' || ch == 'E') {
            charReader.next();
            hasExpPart = true;
            // try to determin exp part's sign:
            char signChar = charReader.peek();
            if (signChar == '-' || signChar == '+') {
                expMinusSign = signChar == '-';
                charReader.next();
            }
            status = ReadNumStatus.READ_NUMBER_EXP_PART;
        } else {
            if (builder == null) {
                throw new JsonParseException("Unexpected char: "
                        + charReader.next());
            }
            // end of number:
            status = ReadNumStatus.READ_NUMBER_END;
        }
        ReadNumResult result = new ReadNumResult(status, hasExpPart, expMinusSign);
        result.setHasFraPart(hasFraPart);
        return result;
    }

    private enum ReadNumStatus {
        //
        READ_NUMBER_INT_PART, READ_NUMBER_FRA_PART, READ_NUMBER_EXP_PART, READ_NUMBER_END
    }

    private class ReadNumResult {
        private ReadNumStatus status;
        private boolean hasExpPart;
        private boolean expMinusSign;
        private boolean hasFraPart;

        public ReadNumResult(ReadNumStatus status, boolean hasExpPart, boolean expMinusSign) {
            this.status = status;
            this.hasExpPart = hasExpPart;
            this.expMinusSign = expMinusSign;
        }

        public ReadNumStatus getStatus() {
            return status;
        }

        public void setStatus(ReadNumStatus status) {
            this.status = status;
        }

        public boolean isHasExpPart() {
            return hasExpPart;
        }

        public void setHasExpPart(boolean hasExpPart) {
            this.hasExpPart = hasExpPart;
        }

        public boolean isExpMinusSign() {
            return expMinusSign;
        }

        public void setExpMinusSign(boolean expMinusSign) {
            this.expMinusSign = expMinusSign;
        }

        public boolean isHasFraPart() {
            return hasFraPart;
        }

        public void setHasFraPart(boolean hasFraPart) {
            this.hasFraPart = hasFraPart;
        }
    }
}
