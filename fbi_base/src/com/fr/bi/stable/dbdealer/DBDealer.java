package com.fr.bi.stable.dbdealer;

import java.sql.ResultSet;

public interface DBDealer<T> {

    T dealWithResultSet(ResultSet rs);

}