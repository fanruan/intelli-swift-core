package com.fr.bi.common.world.people;

import com.fr.bi.common.persistent.json.generator.anno.BIJSONElement;

/**
 * Created by Connery on 2015/12/31.
 */
public class Person {
    private String name;
    private int age;
    private Boolean male;
    @BIJSONElement
    private Person idol;

    public Person getIdol() {
        return idol;
    }

    public void setIdol(Person idol) {
        this.idol = idol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public static Person getWangZuLan() {
        Person person = new Person();
        person.setAge(35);
        person.setMale(true);
        person.setName("WangZuLan");
        return person;
    }

    public static Person getAB() {
        Person person = new Person();
        person.setAge(31);
        person.setMale(false);
        person.setName("AB");
        return person;
    }

    public static Person getDenChao() {
        Person person = new Person();
        person.setAge(42);
        person.setMale(false);
        person.setName("DenChao");
        return person;
    }

    public static Person getChenHe() {
        Person person = new Person();
        person.setAge(35);
        person.setMale(false);
        person.setName("ChenHe");
        return person;
    }

    public static Person getABConnectDenChao() {
        Person ab = getAB();
        Person denchao = getDenChao();
        ab.setIdol(denchao);
        denchao.setIdol(ab);
        return ab;
    }
}