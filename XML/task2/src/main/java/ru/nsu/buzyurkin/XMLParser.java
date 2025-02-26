package ru.nsu.buzyurkin;

import ru.nsu.buzyurkin.exceptions.IllegalRelativeException;
import ru.nsu.buzyurkin.exceptions.PersonMergeException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class XMLParser {
    public static Map<Integer, Person> parse(File xml) throws FileNotFoundException, XMLStreamException, IllegalRelativeException, PersonMergeException {
        XMLInputFactory streamFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = streamFactory.createXMLEventReader(new FileInputStream(xml));

        List<Person> idEntries = new ArrayList<>();
        List<Person> nameEntries = new ArrayList<>();
        Person currentPerson = new Person();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                String elementName = event.asStartElement().getName().getLocalPart();
                Iterator<Attribute> attributes = event.asStartElement().getAttributes();

                switch (elementName) {
                    case "person":
                        Person attributesInfo = parsePersonAttributes(attributes);
                        currentPerson = attributesInfo;
                        break;

                    case "id":
                        String parsedId = parseSingleField(reader, attributes);
                        currentPerson.tryUpdateId(parsedId);
                        break;

                    case "firstname":
                        String firstname = parseSingleField(reader, attributes);
                        currentPerson.tryUpdateFirstName(firstname);
                        break;

                    case "family-name", "surname":
                        String surname = parseSingleField(reader, attributes);
                        currentPerson.tryUpdateFamilyName(surname);
                        break;

                    case "fullname":
                        String fullname = parseFullname(reader);
                        currentPerson.tryUpdateFullname(fullname);
                        break;

                    case "gender":
                        String gender = parseSingleField(reader, attributes);
                        if (gender.equals("female") || gender.equals("F")) {
                            currentPerson.tryUpdateGender(Gender.Female);
                        }
                        else if (gender.equals("male") || gender.equals("M")) {
                            currentPerson.tryUpdateGender(Gender.Male);
                        }
                        break;

                    case "spouce":
                        Optional<Person> possibleSpouce = parseSpouce(reader, attributes);
                        if (possibleSpouce.isEmpty()) break;
                        Person spouce = possibleSpouce.get();

                        spouce.tryUpdateSpouce(currentPerson);
                        currentPerson.tryUpdateSpouce(spouce);

                        nameEntries.add(spouce);
                        break;

                    case "wife", "husband":
                        String spouceInfo = parseSingleField(reader, attributes);

                        spouce = new Person();
                        spouce.tryUpdateId(spouceInfo);
                        spouce.tryUpdateGender(elementName.equals("wife") ? Gender.Female : Gender.Male);

                        spouce.tryUpdateSpouce(currentPerson);
                        currentPerson.tryUpdateSpouce(spouce);

                        idEntries.add(spouce);
                        break;

                    case "parent":
                        String parentInfo = parseSingleField(reader, attributes);
                        if (parentInfo.equals("UNKNOWN")) break;

                        Person parent = new Person();
                        parent.tryUpdateId(parentInfo);

                        parent.tryUpdateChildList(currentPerson);
                        currentPerson.tryUpdateParentList(parent);

                        idEntries.add(parent);
                        break;

                    case "father", "mother":
                        parentInfo = parseSingleField(reader, attributes);

                        parent = new Person();
                        parent.tryUpdateFullname(parentInfo);
                        parent.tryUpdateGender(elementName.equals("mother") ? Gender.Female : Gender.Male);

                        parent.tryUpdateChildList(currentPerson);
                        currentPerson.tryUpdateParentList(parent);

                        nameEntries.add(parent);
                        break;

                    case "children":
                        List<Person> children = parseChildren(reader);
                        for (Person child : children) {
                            child.tryUpdateParentList(currentPerson);
                            currentPerson.tryUpdateChildList(child);

                            if (child.id == -1) {
                                nameEntries.add(child);
                            }
                            else {
                                idEntries.add(child);
                            }
                        }
                        break;

                    case "siblings":
                        List<Person> siblings = parseSiblings(reader, attributes);
                        for (Person sibling : siblings) {
                            sibling.tryUpdateSiblingList(currentPerson);
                            currentPerson.tryUpdateSiblingList(sibling);

                            if (sibling.id != -1) {
                                idEntries.add(sibling);
                            }
                            else {
                                nameEntries.add(sibling);
                            }
                        }
                        break;

                    case "children-number":
                        int childrenNum = Integer.parseInt(parseSingleField(reader, attributes));
                        currentPerson.tryUpdateChildrenNumber(childrenNum);
                        break;

                    case "siblings-number":
                        int n_siblings = Integer.parseInt(parseSingleField(reader, attributes));
                        currentPerson.tryUpdateSiblingsNumber(n_siblings);
                        break;

                    default:
                        System.out.println("PARSING: " + elementName);
                        break;
                }
            }
            if (event.isEndElement()) {
                String elementName = event.asEndElement().getName().getLocalPart();
                switch (elementName) {
                    case "person":
                        if (currentPerson.id != -1){
                            idEntries.add(currentPerson);
                        } else {
                            nameEntries.add(currentPerson);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        System.out.println("STOP PARSING");
        System.out.println("START MAPPING BY ID");

        Map<Integer, Person> idMap = mapPeopleById(idEntries);
        System.out.println("STOP MAPPING BY ID");

        setGendersBySpouce(idMap);

        System.out.println("START COMBINING W/O ID");
        combinePeopleByName(idMap, nameEntries);
        System.out.println("FINISH");

        return idMap;
    }

    private static Map<Integer, Person> mapPeopleById(List<Person> peopleWithId) throws PersonMergeException, IllegalRelativeException {
        Map<Integer, Person> idMap = new HashMap<>();
        for (Person person : peopleWithId) {
            mapPerson(idMap, person);
        }

        return idMap;
    }

    private static void mapPerson(Map<Integer, Person> idMap, Person person) throws PersonMergeException, IllegalRelativeException {
        Person mappedPerson = idMap.get(person.id);
        if (mappedPerson == null) {
            idMap.put(person.id, person);
        } else {
            mappedPerson.tryMerge(person);
        }
    }

    // set's people's gender to the opposite of their spouce's
    private static void setGendersBySpouce(Map<Integer, Person> idMap) throws PersonMergeException {
        for (Person person : idMap.values()) {
            if (person.spouceId == -1) continue;

            Person spouce = idMap.get(person.spouceId);
            if (spouce.gender == null) continue;
            person.tryUpdateGender(spouce.gender == Gender.Female ? Gender.Male : Gender.Female);
        }
    }

    private static void combinePeopleByName(Map<Integer, Person> idMap, List<Person> peopleWithNames) throws PersonMergeException, IllegalRelativeException {
        // get map of all namesakes to iterate over People with ID only one time per name
        Map<String, List<Person>> peopleByNames = peopleWithNames.parallelStream().collect(Collectors.groupingBy(Person::fullname));

        for (Entry<String, List<Person>> entry : peopleByNames.entrySet()) {
            mergeNamedNamesakes(idMap, entry.getKey(), entry.getValue());
        }
    }

    private static void mergeNamedNamesakes(Map<Integer, Person> idMap, String name, List<Person> namedPeople) throws PersonMergeException, IllegalRelativeException {
        List<Person> namesakesWithId = idMap.values().parallelStream().filter(p -> name.equals(p.fullname())).toList();

        for (Person namedPerson : namedPeople) {
            for (Person namesake : namesakesWithId) {
//                if (namesake.fullname().equals("Kaylene Startz")) {
//                    System.nanoTime();
//                }

                if (!namedPerson.anythingCommonBesidesName(namesake)) {
                    continue;
                }
                namesake.tryMerge(namedPerson);
                break;
            }
        }
    }

    private static List<Person> parseChildren(XMLEventReader reader) throws XMLStreamException, PersonMergeException {
        reader.nextEvent();
        List<Person> children = new ArrayList<>();
        XMLEvent event;
        String childInfo;
        while (true) {
            event = reader.nextEvent();
            if (event.isStartElement()) {
                childInfo = parseSingleField(reader, event.asStartElement().getAttributes());
                String elementName = event.asStartElement().getName().getLocalPart();
                switch (elementName) {
                    case "child":
                        Person child = new Person();
                        child.tryUpdateFullname(childInfo);
                        children.add(child);
                        break;

                    case "son", "daughter":
                        child = new Person();
                        child.tryUpdateId(childInfo);

                        if (elementName.equals("daughter")) child.tryUpdateGender(Gender.Female);
                        if (elementName.equals("son")) child.tryUpdateGender(Gender.Male);

                        children.add(child);
                        break;

                    default:
                        break;
                }
            }

            if (event.isEndElement()) {
                break;
            }
        }

        return children;
    }

    private static String parseFullname(XMLEventReader reader) throws XMLStreamException, PersonMergeException {
        reader.nextEvent(); // skip empty characters event
        XMLEvent event;
        StringBuilder builder = new StringBuilder();

        // start element of first name
        event = reader.nextEvent();
        Iterator<Attribute> attributes = event.asStartElement().getAttributes();

        builder.append(parseSingleField(reader, attributes)).append(' ');

        // start element of family name
        while (true) {
            event = reader.nextEvent();

            if (event.isStartElement()) {
                break;
            }
        }
        builder.append(parseSingleField(reader, attributes)).append(' ');

        return builder.toString();
    }

    private static List<Person> parseSiblings(XMLEventReader reader, Iterator<Attribute> attributes) throws XMLStreamException, PersonMergeException {
        List<Person> siblings = new ArrayList<>();

        // there are attributes => no character element to read
        if (attributes.hasNext()) {
            Attribute attribute = attributes.next();
            String[] siblingsIds = attribute.getValue().trim().split("\\s+");
            for (String id : siblingsIds) {
                Person sibling = new Person();
                sibling.tryUpdateId(id);
                siblings.add(sibling);
            }

            return siblings;
        }

        reader.nextEvent(); // skip empty characters event
        XMLEvent event;
        String siblingInfo;
        while (true) {
            event = reader.nextEvent();
            if (event.isStartElement()) {
                siblingInfo = parseSingleField(reader, event.asStartElement().getAttributes());
                String elementName = event.asStartElement().getName().getLocalPart();
                switch (elementName) {
                    case "sister", "brother":
                        Person sibling = new Person();
                        sibling.tryUpdateFullname(siblingInfo);

                        if (elementName.equals("sister")) sibling.tryUpdateGender(Gender.Female);
                        if (elementName.equals("brother")) sibling.tryUpdateGender(Gender.Male);

                        siblings.add(sibling);
                        break;

                    default:
                        break;
                }
            }

            if (event.isEndElement()) {
                break;
            }
        }

        return siblings;
    }

    private static Optional<Person> parseSpouce(XMLEventReader reader, Iterator<Attribute> attributes) throws XMLStreamException, PersonMergeException {
        // no attributes => no spouce
        if (!attributes.hasNext()) {
            return Optional.empty();
        }

        String spouceInfo = attributes.next().getValue();
        if (spouceInfo.equals("NONE")) return Optional.empty();

        // real spouce with real name
        Person spouce = new Person();
        spouce.tryUpdateFullname(spouceInfo);

        return Optional.of(spouce);
    }

    // Retrieves string value from field with either single attribute or single charactersElement child
    private static String parseSingleField(XMLEventReader reader, Iterator<Attribute> attributes) throws XMLStreamException, PersonMergeException {
        String charactersInfo = null;

        // there are attributes => no character element to read
        if (attributes.hasNext()) {
            Attribute attribute = attributes.next();
            charactersInfo = attribute.getValue().trim();
        }

        // there are no attributes => read character element
        else {
            XMLEvent event = reader.nextEvent();
            if (!event.isCharacters()) {
                throw new RuntimeException("Something off with document");
            }

            charactersInfo = event.asCharacters().getData().trim();
        }

        // read EndElementEvent
        if (!reader.nextEvent().isEndElement()) {
            throw new RuntimeException("Something off with document");
        }

        return charactersInfo;
    }

    private static Person parsePersonAttributes(Iterator<Attribute> attributes) throws PersonMergeException {
        Person personInfo = new Person();
        while (attributes.hasNext()) {
            Attribute attribute = attributes.next();

            switch (attribute.getName().getLocalPart()) {
                case "id":
                    personInfo.tryUpdateId(attribute.getValue());
                    break;

                case "name":
                    personInfo.tryUpdateFullname(attribute.getValue());
                    break;

                default:
                    break;
            }
        }

        return personInfo;
    }

}