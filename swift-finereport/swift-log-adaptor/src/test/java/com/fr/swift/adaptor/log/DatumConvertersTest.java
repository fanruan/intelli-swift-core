package com.fr.swift.adaptor.log;

import com.fr.swift.structure.Pair;
import com.fr.swift.util.function.UnaryOperator;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.fr.swift.structure.Pair.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author anchore
 * @date 2019/1/12
 */
public class DatumConvertersTest {

    private final Map<Class<?>, Pair<?, ?>[]> map = new HashMap<Class<?>, Pair<?, ?>[]>();

    private final Map<Class<?>, Pair<?, ?>[]> reverseMap = new HashMap<Class<?>, Pair<?, ?>[]>();

    @Before
    public void setUp() throws Exception {
        map.put(boolean.class, new Pair[]{of(true, 1L), of(false, 0L)});
        map.put(Boolean.class, new Pair[]{of(true, 1L), of(false, 0L)});

        map.put(byte.class, new Pair[]{of(((byte) 1), 1L)});
        map.put(Byte.class, new Pair[]{of(((byte) 1), 1L)});

        map.put(short.class, new Pair[]{of(((short) 1), 1L)});
        map.put(Short.class, new Pair[]{of(((short) 1), 1L)});

        map.put(int.class, new Pair[]{of(1, 1L)});
        map.put(Integer.class, new Pair[]{of(1, 1L)});

        map.put(long.class, new Pair[]{of(1L, 1L)});
        map.put(Long.class, new Pair[]{of(1L, 1L)});

        map.put(float.class, new Pair[]{of(1F, 1D)});
        map.put(Float.class, new Pair[]{of(1F, 1D)});

        map.put(double.class, new Pair[]{of(1D, 1D)});
        map.put(Double.class, new Pair[]{of(1D, 1D)});

        map.put(char.class, new Pair[]{of('1', "1")});
        map.put(Character.class, new Pair[]{of('1', "1")});

        map.put(String.class, new Pair[]{of("1", "1")});

        map.put(Date.class, new Pair[]{of(new Date(1), 1L)});
        map.put(java.sql.Date.class, new Pair[]{of(new java.sql.Date(1), 1L)});

        reverseMap.put(boolean.class, new Pair[]{of(false, null), of(true, 1L), of(false, 0L)});
        reverseMap.put(Boolean.class, new Pair[]{of(null, null), of(true, 1L), of(false, 0L)});

        reverseMap.put(byte.class, new Pair[]{of(((byte) 0), null), of(((byte) 1), 1L)});
        reverseMap.put(Byte.class, new Pair[]{of(null, null), of(((byte) 1), 1L)});

        reverseMap.put(short.class, new Pair[]{of(((short) 0), null), of(((short) 1), 1L)});
        reverseMap.put(Short.class, new Pair[]{of(null, null), of(((short) 1), 1L)});

        reverseMap.put(int.class, new Pair[]{of(0, null), of(1, 1L)});
        reverseMap.put(Integer.class, new Pair[]{of(null, null), of(1, 1L)});

        reverseMap.put(long.class, new Pair[]{of(0L, null), of(1L, 1L)});
        reverseMap.put(Long.class, new Pair[]{of(null, null), of(1L, 1L)});

        reverseMap.put(float.class, new Pair[]{of(0F, null), of(1F, 1D)});
        reverseMap.put(Float.class, new Pair[]{of(null, null), of(1F, 1D)});

        reverseMap.put(double.class, new Pair[]{of(0D, null), of(1D, 1D)});
        reverseMap.put(Double.class, new Pair[]{of(null, null), of(1D, 1D)});

        reverseMap.put(char.class, new Pair[]{of('\0', null), of('1', "1")});
        reverseMap.put(Character.class, new Pair[]{of(null, null), of('1', "1")});

        reverseMap.put(String.class, new Pair[]{of(null, null), of("1", "1")});

        reverseMap.put(Date.class, new Pair[]{of(null, null), of(new Date(1), 1L)});
        reverseMap.put(java.sql.Date.class, new Pair[]{of(null, null), of(new java.sql.Date(1), 1L)});
    }

    @Test
    public void getConverter() {
        for (Entry<Class<?>, Pair<?, ?>[]> entry : map.entrySet()) {
            UnaryOperator<Object> converter = DatumConverters.getConverter(entry.getKey());
            assertNull(null, converter.apply(null));

            for (Pair<?, ?> pair : entry.getValue()) {
                Object converted = converter.apply(pair.getKey());
                assertEquals(pair.getValue(), converted);
            }
        }
    }

    @Test
    public void getReverseConverter() {
        for (Entry<Class<?>, Pair<?, ?>[]> entry : reverseMap.entrySet()) {
            for (Pair<?, ?> pair : entry.getValue()) {
                Object converted = DatumConverters.getReverseConverter(entry.getKey()).apply(pair.getValue());
                assertEquals(pair.getKey(), converted);
            }
        }
    }
}