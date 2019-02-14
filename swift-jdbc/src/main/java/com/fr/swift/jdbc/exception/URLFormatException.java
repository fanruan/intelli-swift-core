package com.fr.swift.jdbc.exception;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/26
 */
class URLFormatException extends SQLException {
    private static final String EXAMPLE = "The url should be formatted like\n" +
            "\n\tjdbc:swift:<mode>://<host>:<port>/<db>.\n\n" +
            "The modes which are supported are emb and remote.";
    private static final long serialVersionUID = 2878667811296877797L;

    URLFormatException(String url) {
        super(url + " is not valid. " + EXAMPLE);
    }
}
