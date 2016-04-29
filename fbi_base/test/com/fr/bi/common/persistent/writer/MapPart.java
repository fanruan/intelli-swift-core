package com.fr.bi.common.persistent.writer;

import com.fr.bi.common.world.people.Person;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Connery on 2015/12/31.
 */
public class MapPart {
    private Map<Integer, String> basic;
    private Map<Person, Person> team;

    public void setBasic(Map<Integer, String> basic) {
        this.basic = basic;
    }

    public Map<Person, Person> getTeam() {
        return team;
    }

    public void setTeam(Map<Person, Person> team) {
        this.team = team;
    }

    public Map<Integer, String> getBasic() {
        return basic;
    }

    public static MapPart generateBasicTwo() {
        MapPart part = new MapPart();
        addTwoBasic(part);
        return part;
    }

    private static void addTwoBasic(MapPart part) {
        Map<Integer, String> basic = new HashMap<Integer, String>();
        basic.put(12, "abc");
        basic.put(13, "adbc");
        part.setBasic(basic);
    }

    private static void addTwoPerson(MapPart part) {
        Map<Person, Person> team = new HashMap<Person, Person>();
        team.put(Person.getAB(), Person.getChenHe());
        team.put(Person.getDenChao(), Person.getWangZuLan());
        part.setTeam(team);
    }

    public static MapPart generatePersonTwo() {
        MapPart part = new MapPart();
        addTwoPerson(part);
        return part;
    }
    public static MapPart generateTwo() {
        MapPart part = new MapPart();
        addTwoPerson(part);
        addTwoBasic(part);
        return part;
    }
}