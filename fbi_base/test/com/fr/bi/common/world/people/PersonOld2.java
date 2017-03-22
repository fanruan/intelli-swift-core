package com.fr.bi.common.world.people;

import com.fr.bi.common.persistent.json.generator.anno.BIJSONElement;

/**
 * Created by Connery on 2015/12/31.
 */
public class PersonOld2 {
    private String nameOld;
    private int age;
    private Boolean male;

    public String getNameOld() {
        return nameOld;
    }

    public void setNameOld(String nameOld) {
        this.nameOld = nameOld;
    }

    @BIJSONElement
    private PersonOld2 idol;

    public PersonOld2 getIdol() {
        return idol;
    }

    public void setIdol(PersonOld2 idol) {
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

    public static PersonOld2 getWangZuLan() {
        PersonOld2 person = new PersonOld2();
        person.setAge(35);
        person.setMale(true);
        person.setNameOld("WangZuLan");
        return person;
    }

    public static PersonOld2 getAB() {
        PersonOld2 person = new PersonOld2();
        person.setAge(31);
        person.setMale(false);
        person.setNameOld("AB");
        return person;
    }

    public static PersonOld2 getDenChao() {
        PersonOld2 person = new PersonOld2();
        person.setAge(42);
        person.setMale(false);
        person.setNameOld("DenChao");
        return person;
    }

    public static PersonOld2 getChenHe() {
        PersonOld2 person = new PersonOld2();
        person.setAge(35);
        person.setMale(false);
        person.setNameOld("ChenHe");
        return person;
    }

    public static PersonOld2 getABConnectDenChao() {
        PersonOld2 ab = getAB();
        PersonOld2 denchao = getDenChao();
        ab.setIdol(denchao);
        denchao.setIdol(ab);
        return ab;
    }
}