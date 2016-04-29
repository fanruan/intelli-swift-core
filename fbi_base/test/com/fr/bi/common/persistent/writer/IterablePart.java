package com.fr.bi.common.persistent.writer;

import com.fr.bi.common.world.people.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connery on 2015/12/31.
 */
public class IterablePart {
    private List<Person> persons;
    private List<IterablePart> parts;
    private List<List<Person>> personsTeams;

    public IterablePart() {
        persons = new ArrayList<Person>();
        parts = new ArrayList<IterablePart>();
        personsTeams = new ArrayList<List<Person>>();
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public static IterablePart generateTwoPersonIter() {
        IterablePart part = new IterablePart();
        part.persons.add(Person.getAB());
        part.persons.add(Person.getDenChao());
        return part;
    }

    public List<IterablePart> getParts() {
        return parts;
    }

    public List<List<Person>> getPersonsTeams() {
        return personsTeams;
    }

    public void setPersonsTeams(List<List<Person>> personsTeams) {
        this.personsTeams = personsTeams;
    }

    public void setParts(List<IterablePart> parts) {
        this.parts = parts;
    }

    public static IterablePart generateTwoPersonConnect() {
        IterablePart part = new IterablePart();
        Person ab = Person.getAB();
        Person de = Person.getDenChao();
        ab.setIdol(de);
        de.setIdol(ab);
        part.persons.add(ab);
        part.persons.add(de);
        return part;
    }

    public static IterablePart generateParts() {
        IterablePart part = new IterablePart();
        Person ab = Person.getAB();
        Person de = Person.getDenChao();
        ab.setIdol(de);
        de.setIdol(ab);
        part.persons.add(ab);
        part.persons.add(de);
        IterablePart parts = new IterablePart();
        parts.parts.add(part);
        parts.parts.add(part);
        return parts;
    }

    public static IterablePart generateTeams() {
        IterablePart part = new IterablePart();
        Person ab = Person.getAB();
        Person de = Person.getDenChao();
        ab.setIdol(de);
        de.setIdol(ab);
        part.persons.add(ab);
        part.persons.add(de);
        part.personsTeams.add(part.persons);
        part.personsTeams.add(part.persons);
        part.personsTeams.add(part.persons);

        return part;
    }
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof IterablePart)) return false;
//        IterablePart part = (IterablePart) o;
//        return !(persons != null ? !BIComparatorUtils.equals(persons, (part.persons)) : part.persons != null);
//    }
//
//    @Override
//    public int hashCode() {
//        return persons != null ? persons.hashCode() : 0;
//    }
}