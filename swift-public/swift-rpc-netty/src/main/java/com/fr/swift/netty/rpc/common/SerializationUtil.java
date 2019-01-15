package com.fr.swift.netty.rpc.common;

//import com.dyuproject.protostuff.LinkedBuffer;
//import com.dyuproject.protostuff.ProtostuffIOUtil;
//import com.dyuproject.protostuff.SwiftDatabase;
//import com.dyuproject.protostuff.runtime.RuntimeSchema;

//import com.fr.third.org.objenesis.Objenesis;
//import com.fr.third.org.objenesis.ObjenesisStd;

/**
 * This class created on 2018/6/6
 * 序列化工具类（基于 Protostuff 实现）
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Deprecated
public class SerializationUtil {

//    private static Map<Class<?>, SwiftDatabase<?>> cachedSchema = new ConcurrentHashMap<Class<?>, SwiftDatabase<?>>();

//    private static Objenesis objenesis = new ObjenesisStd(true);

    private SerializationUtil() {
    }

    /**
     * 序列化
     */
    public static <T> byte[] serialize(T obj) {
//        Class<T> cls = (Class<T>) obj.getClass();
//        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
//        try {
//            SwiftDatabase<T> schema = getSchema(cls);
//            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
//        } catch (Exception e) {
//            throw new IllegalStateException(e.getMessage(), e);
//        } finally {
//            buffer.clear();
//        }
        return null;
    }

    /**
     * 反序列化
     */
    public static <T> T deserialize(byte[] data, Class<T> cls) {
//        try {
//            T message = objenesis.newInstance(cls);
//            SwiftDatabase<T> schema = getSchema(cls);
//            ProtostuffIOUtil.mergeFrom(data, message, schema);
//            return message;
//        } catch (Exception e) {
//            throw new IllegalStateException(e.getMessage(), e);
//        }
        return null;
    }

//    private static <T> SwiftDatabase<T> getSchema(Class<T> cls) {
//        SwiftDatabase<T> schema = (SwiftDatabase<T>) cachedSchema.get(cls);
//        if (schema == null) {
//            schema = RuntimeSchema.createFrom(cls);
//            cachedSchema.put(cls, schema);
//        }
//        return schema;
//    }
}
