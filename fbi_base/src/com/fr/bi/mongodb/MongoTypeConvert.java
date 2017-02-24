package com.fr.bi.mongodb;

import org.bson.BsonUndefined;
import org.bson.types.ObjectId;

import java.util.Date;

/**
 * Created by wang on 2017/1/20.
 */
public class MongoTypeConvert {

    public static int convertJavaType2SqlType(String classType){
        if(classType==null){
            return java.sql.Types.NULL;
        }else if(classType.equals(MongoConstants.MONGODB_ARRAY)){
            return java.sql.Types.VARCHAR;
        }else if(classType.equals(MongoConstants.MONGODB_BINARY)){
            return java.sql.Types.BINARY;
        }else if(classType.equals(MongoConstants.MONGODB_UNDEFINED)){
            return java.sql.Types.VARCHAR;
        }else if(classType.equals(MongoConstants.MONGODB_REGULAR_EXPRESSION)){
            return java.sql.Types.VARCHAR;
        } else if(classType.equals(MongoConstants.MONGODB_NULL)){
            return java.sql.Types.NULL;
        }else if(classType.equals(MongoConstants.MONGODB_BOOLEAN)){
            return java.sql.Types.BOOLEAN;
        }else if(classType.equals(MongoConstants.MONGODB_DOUBLE)){
            return java.sql.Types.DOUBLE;
        }else if(classType.equals(MongoConstants.MONGODB_INTEFER)){
            return java.sql.Types.INTEGER;
        }else if(classType.equals(MongoConstants.MONGODB_OBJECTID)){
            return java.sql.Types.VARCHAR;
        }else if(classType.equals(MongoConstants.MONGODB_STRING)){
            return java.sql.Types.VARCHAR;
        }else if(classType.equals(MongoConstants.MONGODB_TIMESTAMP)){
            return java.sql.Types.TIMESTAMP;
        }else {
            return java.sql.Types.VARCHAR;
        }
    }
    /**
     * 将mongodb的数据类型转换成Cube的数据类型
     * @param val
     * @return
     */
    public static Object dealWithMongoDBBasicObject(Object val) {
        if (val instanceof org.bson.types.ObjectId) {
            val = ((ObjectId) val).toHexString();
        } else if (val instanceof org.bson.BsonRegularExpression) {
            val = val.toString();
        } else if (val instanceof BsonUndefined) {
            val = "undefined";
        } else if (val instanceof java.lang.Integer) {
            val = ((Integer) val).longValue();
        } else if (val instanceof java.util.Date) {
            val = ((Date) val).getTime();
        } else if (val instanceof Boolean) {
            val = val.toString();
        }
        return val;
    }
}
