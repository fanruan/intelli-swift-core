package com.fr.bi.common.persistent.writer;

import com.fr.bi.common.world.people.Person;

import java.util.ArrayList;

/**
 * Created by Connery on 2015/12/31.
 */
public class NormalPart {

    private Person person;
    private ArrayList<Person> team;

    public NormalPart() {
        team = new ArrayList<Person>();
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public ArrayList<Person> getTeam() {
        return team;
    }

    public void setTeam(ArrayList<Person> team) {
        this.team = team;
    }

    public static NormalPart generateOneContent() {
        NormalPart part = new NormalPart();
        part.person = Person.getAB();
        part.person.setIdol(Person.getDenChao());
        part.person.getIdol().setIdol(part.person);
        part.team.add(part.person);
        part.team.add(part.person.getIdol());
        return part;
    }

    public static NormalPart generateOnePerson() {
        NormalPart part = new NormalPart();
        part.person = Person.getAB();
        part.person.setIdol(Person.getDenChao());
        part.person.getIdol().setIdol(part.person);
        return part;
    }
}