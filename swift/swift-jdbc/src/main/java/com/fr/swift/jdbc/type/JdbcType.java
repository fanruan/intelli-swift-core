package com.fr.swift.jdbc.type;

import java.sql.Types;

/**
 * @author yee
 * @date 2018/8/29
 */
public enum JdbcType {

    BIT(Types.BIT),
    TINYINT(Types.TINYINT),
    SMALLINT(Types.SMALLINT),
    INTEGER(Types.INTEGER),
    INT(Types.INTEGER),
    BIGINT(Types.BIGINT),
    FLOAT(Types.FLOAT),
    REAL(Types.REAL),
    DOUBLE(Types.DOUBLE),
    NUMERIC(Types.NUMERIC),
    DECIMAL(Types.DECIMAL),
    CHAR(Types.CHAR),
    VARCHAR(Types.VARCHAR),
    LONGVARCHAR(Types.LONGVARCHAR),
    DATE(Types.DATE),
    TIME(Types.TIME),
    TIMESTAMP(Types.TIMESTAMP),
    BOOLEAN(Types.BOOLEAN),
    ROWID(Types.ROWID),
    NCHAR(Types.NCHAR),
    NVARCHAR(Types.NVARCHAR),
    LONGNVARCHAR(Types.LONGNVARCHAR);

    private Integer type;

    JdbcType(final Integer type) {
        this.type = type;
    }

    public static JdbcType getType(String type) {
        return JdbcType.valueOf(type.toUpperCase());
    }

    public Integer getType() {
        return type;
    }
}