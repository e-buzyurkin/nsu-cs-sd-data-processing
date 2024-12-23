package ru.nsu.buzyurkin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        File file = new File("src/main/resources/people.xml");

        Map<Integer, Person> idMap = XMLParser.parse(file);
        List<Person> people = new ArrayList<>(idMap.values());
        System.out.println(people.size());
    }
}