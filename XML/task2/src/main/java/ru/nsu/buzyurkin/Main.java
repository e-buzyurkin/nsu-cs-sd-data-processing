package ru.nsu.buzyurkin;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.xml.sax.SAXException;
import ru.nsu.buzyurkin.generated.*;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static Map<Integer, Person> peopleIds;
    private static final Map<String, PersonType> collectedData = new HashMap<>();

    public static void main(String[] args) throws Exception {
        File file = new File("src/main/resources/people.xml");

        peopleIds = XMLParser.parse(file);

        System.out.println("COLLECTING DATA");
        People people = new People();
        for (var info : peopleIds.values()) {
            PersonType person = new PersonType();
            setPersonInfo(person, info);
            collectedData.put("P" + info.id, person);
        }
        System.out.println("SETTING INFO ABOUT PEOPLE");
        for (var person : collectedData.values()) {
            setSpouse(person);
            setChildren(person);
            setParents(person);
            setSiblings(person);
        }

        people.getPerson().addAll(collectedData.values());
        System.out.println("START VALIDATION");
        try {
            JAXBContext jc;
            ClassLoader classLoader = People.class.getClassLoader();
            jc = JAXBContext.newInstance("ru.nsu.buzyurkin.generated", classLoader);
            Marshaller writer = jc.createMarshaller();

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            File schemaFile = new File("src/main/resources/schema.xsd");
            writer.setSchema(schemaFactory.newSchema(schemaFile));
            writer.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            writer.marshal(people, new File("src/main/resources/structured_people.xml"));
        } catch (JAXBException | SAXException e) {
            e.printStackTrace();
        }
    }

    private static List<Person> extractByName(String name, List<Person> people) {
        return people.stream().filter(person -> person.fullname().equals(name)).toList();
    }

    private static void setPersonInfo(PersonType person, Person info) {
        person.setId("P" + info.id);
        person.setName(info.firstName + " " + info.familyName);

        if (info.gender != null) {
            person.setGender(GenderType.fromValue(info.gender.name().substring(0, 1)));
        }
    }

    private static void setSpouse(PersonType person) {
        Person info = peopleIds.get(Integer.parseInt(person.getId().substring(1)));

        if (info.spouceId != -1) {
            PersonRef personRef = new PersonRef();
            personRef.setId(collectedData.get("P" + info.spouceId));

            if (peopleIds.get(info.spouceId).gender == Gender.Female) {
                person.setWife(personRef);
            } else if (peopleIds.get(info.spouceId).gender == Gender.Male) {
                person.setHusband(personRef);
            }
        }

    }

    private static void setChildren(PersonType person) {
        Person info = peopleIds.get(Integer.parseInt(person.getId().substring(1)));
        ChildrenType childrenType = new ChildrenType();

        for (Integer child : info.childrenIds) {
            PersonRef personRef = new PersonRef();
            personRef.setId(collectedData.get("P" + child));

            if (peopleIds.get(child).gender == Gender.Female) {
                childrenType.getDaughter().add(personRef);
            } else if (peopleIds.get(child).gender == Gender.Male) {
                childrenType.getSon().add(personRef);
            }
        }

        person.getChildren().add(childrenType);
    }

    private static void setParents(PersonType person) {
        Person info = peopleIds.get(Integer.parseInt(person.getId().substring(1)));
        ParentsType parentsType = new ParentsType();

        for (Integer parent : info.parentsIds) {
            PersonRef personRef = new PersonRef();
            personRef.setId(collectedData.get("P" + parent));

            if (peopleIds.get(parent).gender == Gender.Female) {
                parentsType.setMother(personRef);
            } else if (peopleIds.get(parent).gender == Gender.Male) {
                parentsType.setFather(personRef);
            }
        }

        person.getParents().add(parentsType);
    }

    private static void setSiblings(PersonType person) {
        var info = peopleIds.get(Integer.parseInt(person.getId().substring(1)));
        SiblingsType siblingsType = new SiblingsType();

        for (Integer sibling : info.siblingsIds) {
            PersonRef personRef = new PersonRef();
            personRef.setId(collectedData.get("P" + sibling));

            if (peopleIds.get(sibling).gender == Gender.Female) {
                siblingsType.getSister().add(personRef);
            } else if (peopleIds.get(sibling).gender == Gender.Male) {
                siblingsType.getBrother().add(personRef);
            }
        }

        person.getSiblings().add(siblingsType);
    }
}