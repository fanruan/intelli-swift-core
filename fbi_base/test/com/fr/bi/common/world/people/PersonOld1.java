package com.fr.bi.common.world.people;

import com.fr.bi.common.persistent.json.generator.anno.BIJSONElement;

/**
 * Created by Connery on 2015/12/31.
 */
public class PersonOld1 {
    private String nameOld;
    private int age;
    private Boolean male;
    private String ignoredString;

    public void setIgnoredString(String ignoredString) {
        this.ignoredString = ignoredString;
    }

    public String getNameOld() {
        return nameOld;
    }

    public void setNameOld(String nameOld) {
        this.nameOld = nameOld;
    }

    @BIJSONElement
    private PersonOld1 idol;

    public PersonOld1 getIdol() {
        return idol;
    }

    public void setIdol(PersonOld1 idol) {
        this.idol = idol;
    }

    public Boolean getMale() {
        return male;
    }

    public void setMale(Boolean male) {
        this.male = male;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static PersonOld1 getWangZuLan() {
        PersonOld1 person = new PersonOld1();
        person.setAge(35);
        person.setMale(true);
        person.setNameOld("WangZuLan");
        return person;
    }

    public static PersonOld1 getAB() {
        PersonOld1 person = new PersonOld1();
        person.setAge(31);
        person.setMale(false);
        person.setNameOld("AB");
        return person;
    }

    public static PersonOld1 getDenChao() {
        PersonOld1 person = new PersonOld1();
        person.setAge(42);
        person.setMale(false);
        person.setNameOld("DenChao");
        return person;
    }

    public static PersonOld1 getChenHe() {
        PersonOld1 person = new PersonOld1();
        person.setAge(35);
        person.setMale(false);
        person.setNameOld("ChenHe");
        person.setIgnoredString("ignore");
        return person;
    }

    public static PersonOld1 getABConnectDenChao() {
        PersonOld1 ab = getAB();
        PersonOld1 denchao = getDenChao();
        ab.setIdol(denchao);
        denchao.setIdol(ab);
        denchao.setIgnoredString("ignore");
        return ab;
    }
}