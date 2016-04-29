package com.fr.bi.common.persistent.writer;

import com.fr.bi.common.world.people.Person;

/**
 * Created by Connery on 2015/12/31.
 */
public class ArrayPart {
    private Person[] persons;
    private String[] strContent;
    private Float[] floats;
    private Double[] doubles;
    private Integer[] integers;
    private Byte[] bytes;
    private Character[] characters;
    private Boolean[] booleans;
    private Long[] longs;
    private Short[] shorts;
    private float[] pfloats;
    private double[] pdoubles;
    private int[] pintegers;
    private byte[] pbytes;
    private char[] pcharacters;
    private boolean[] pbooleans;
    private long[] plongs;
    private short[] pshorts;

    public String[] getStrContent() {
        return strContent;
    }

    public void setStrContent(String[] strContent) {
        this.strContent = strContent;
    }

    public Float[] getFloats() {
        return floats;
    }

    public void setFloats(Float[] floats) {
        this.floats = floats;
    }

    public Double[] getDoubles() {
        return doubles;
    }

    public void setDoubles(Double[] doubles) {
        this.doubles = doubles;
    }

    public Integer[] getIntegers() {
        return integers;
    }

    public void setIntegers(Integer[] integers) {
        this.integers = integers;
    }

    public Byte[] getBytes() {
        return bytes;
    }

    public void setBytes(Byte[] bytes) {
        this.bytes = bytes;
    }

    public Character[] getCharacters() {
        return characters;
    }

    public void setCharacters(Character[] characters) {
        this.characters = characters;
    }

    public Boolean[] getBooleans() {
        return booleans;
    }

    public void setBooleans(Boolean[] booleans) {
        this.booleans = booleans;
    }

    public Long[] getLongs() {
        return longs;
    }

    public void setLongs(Long[] longs) {
        this.longs = longs;
    }

    public Short[] getShorts() {
        return shorts;
    }

    public void setShorts(Short[] shorts) {
        this.shorts = shorts;
    }

    public float[] getPfloats() {
        return pfloats;
    }

    public void setPfloats(float[] pfloats) {
        this.pfloats = pfloats;
    }

    public double[] getPdoubles() {
        return pdoubles;
    }

    public void setPdoubles(double[] pdoubles) {
        this.pdoubles = pdoubles;
    }

    public int[] getPintegers() {
        return pintegers;
    }

    public void setPintegers(int[] pintegers) {
        this.pintegers = pintegers;
    }

    public byte[] getPbytes() {
        return pbytes;
    }

    public void setPbytes(byte[] pbytes) {
        this.pbytes = pbytes;
    }

    public char[] getPcharacters() {
        return pcharacters;
    }

    public void setPcharacters(char[] pcharacters) {
        this.pcharacters = pcharacters;
    }

    public boolean[] getPbooleans() {
        return pbooleans;
    }

    public void setPbooleans(boolean[] pbooleans) {
        this.pbooleans = pbooleans;
    }

    public long[] getPlongs() {
        return plongs;
    }

    public void setPlongs(long[] plongs) {
        this.plongs = plongs;
    }

    public short[] getPshorts() {
        return pshorts;
    }

    public void setPshorts(short[] pshorts) {
        this.pshorts = pshorts;
    }

    public Person[] getPersons() {
        return persons;
    }

    public ArrayPart() {
        this.persons = null;
    }

    public void setPersons(Person[] persons) {
        this.persons = persons;
    }

    public static ArrayPart generateNull() {
        return new ArrayPart();
    }

    public static ArrayPart generatePersonOne() {
        ArrayPart part = new ArrayPart();
        addPersonOne(part);
        return part;
    }

    public static ArrayPart generatePersonTwo() {
        ArrayPart part = new ArrayPart();
        addPersonTwo(part);
        return part;
    }

    private static void addPersonOne(ArrayPart part) {
        Person[] persons = new Person[1];
        persons[0] = Person.getAB();
        part.setPersons(persons);
    }

    private static void addPersonTwo(ArrayPart part) {
        Person[] persons = new Person[2];
        persons[0] = Person.getAB();
        persons[1] = Person.getChenHe();
        part.setPersons(persons);
    }

    private static void addBasicOne(ArrayPart part) {
        part.setBooleans(new Boolean[]{true});
        part.setPbooleans(new boolean[]{Boolean.FALSE.booleanValue()});
        part.setBytes(new Byte[]{1});
        part.setPbytes(new byte[]{Byte.valueOf("1").byteValue()});
        part.setCharacters(new Character[]{Character.valueOf('c')});
        part.setPcharacters(new char[]{Character.valueOf('c').charValue()});
        part.setDoubles(new Double[]{Double.valueOf(12.23)});
        part.setPdoubles(new double[]{Double.valueOf(12.23).doubleValue()});
        part.setFloats(new Float[]{Float.valueOf(12.312f)});
        part.setPfloats(new float[]{Float.valueOf(12.312f).floatValue()});
        part.setIntegers(new Integer[]{Integer.valueOf(123)});
        part.setPintegers(new int[]{Integer.valueOf(123).intValue()});
        part.setLongs(new Long[]{Long.valueOf(12344)});
        part.setPlongs(new long[]{Long.valueOf(12344).longValue()});
        part.setShorts(new Short[]{Short.valueOf("12")});
        part.setPshorts(new short[]{Short.valueOf("12").shortValue()});
    }

    public static ArrayPart generateBasicOne() {
        ArrayPart part = new ArrayPart();
        addBasicOne(part);
        return part;
    }

    private static void addBasicTwo(ArrayPart part) {
        part.setBooleans(new Boolean[]{true, false});
        part.setPbooleans(new boolean[]{Boolean.TRUE.booleanValue(), Boolean.FALSE.booleanValue()});

        part.setBytes(new Byte[]{1, 12});
        part.setPbytes(new byte[]{Byte.valueOf("1").byteValue(), Byte.valueOf("12").byteValue()});
        part.setCharacters(new Character[]{Character.valueOf('c'), Character.valueOf('b')});
        part.setPcharacters(new char[]{Character.valueOf('c').charValue(), Character.valueOf('b').charValue()});
        part.setDoubles(new Double[]{Double.valueOf(12.23), Double.valueOf(123.2)});
        part.setPdoubles(new double[]{Double.valueOf(12.23).doubleValue(), Double.valueOf(123.2).doubleValue()});
        part.setFloats(new Float[]{Float.valueOf(12.312f), Float.valueOf(121.312f)});
        part.setPfloats(new float[]{Float.valueOf(12.312f).floatValue(), Float.valueOf(22.312f).floatValue()});
        part.setIntegers(new Integer[]{Integer.valueOf(123)});
        part.setPintegers(new int[]{Integer.valueOf(123).intValue(), Integer.valueOf(1233).intValue()});
        part.setLongs(new Long[]{Long.valueOf(12344), Long.valueOf(124)});
        part.setPlongs(new long[]{Long.valueOf(12344).longValue(), Long.valueOf(1344).longValue()});
        part.setShorts(new Short[]{Short.valueOf("12"), Short.valueOf("123")});
        part.setPshorts(new short[]{Short.valueOf("12").shortValue(), Short.valueOf("132").shortValue()});
    }

    public static ArrayPart generateBasicTwo() {
        ArrayPart part = new ArrayPart();
        addBasicTwo(part);
        return part;
    }

    public static ArrayPart generateTwo() {
        ArrayPart part = new ArrayPart();
        addBasicTwo(part);
        addPersonTwo(part);
        return part;
    }

    public static ArrayPart generateOne() {
        ArrayPart part = new ArrayPart();
        addBasicOne(part);
        addPersonOne(part);
        return part;
    }
}