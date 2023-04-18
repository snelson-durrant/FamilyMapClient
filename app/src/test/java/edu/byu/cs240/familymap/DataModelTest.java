package edu.byu.cs240.familymap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs240.familymap.data_storage.DataModel;
import edu.byu.cs240.familymap.data_storage.FamilyMember;
import model.Event;
import model.Person;

public class DataModelTest {

    private DataModel dataModel;
    private Person father;
    private Person mother;
    private Person person;
    private Person child;
    private Person random;

    @BeforeEach
    public void setUp() {

        dataModel = DataModel.initialize();

        ArrayList<Person> testPeople = new ArrayList<>();
        father = new Person("f", "father", "Father",
                "Father", "m", null, null, "m");
        testPeople.add(father);
        mother = new Person("m", "mother", "Mother",
                "Mother", "f", null, null, "f");
        testPeople.add(mother);
        person = new Person("p", "person", "Person",
                "Person", "m", "f", "m", null);
        testPeople.add(person);
        child = new Person("c", "child", "Child",
                "Child", "f", "p", null, null);
        testPeople.add(child);
        dataModel.setDataUserPerson(child);
        random = new Person("r", "random", "Random",
                "Random", "m", null, null, null);
        testPeople.add(random);

        Person[] arr = new Person[testPeople.size()];
        dataModel.setDataPeople(testPeople.toArray(arr));

        ArrayList<Event> testEvents = new ArrayList<>();
        Event birth = new Event("birth", "person", "p", 0f,
                0f, "USA", "Provo", "Birth", 2000);
        testEvents.add(birth);
        Event marriage = new Event("marriage", "person", "p", 0f,
                0f, "USA", "Provo", "Marriage", 2030);
        testEvents.add(marriage);
        Event death = new Event("death", "person", "p", 0f,
                0f, "USA", "Provo", "Death", 2080);
        testEvents.add(death);
        Event conflict2 = new Event("new_job", "person", "p", 0f,
                0f, "USA", "Provo", "New_Job", 2040);
        testEvents.add(conflict2);
        Event conflict1 = new Event("award", "person", "p", 0f,
                0f, "USA", "Provo", "Award", 2040);
        testEvents.add(conflict1);
        Event femaleEvent = new Event("run", "mother", "m", 0f,
                0f, "USA", "Provo", "Run", 2040);
        testEvents.add(femaleEvent);

        Event[] arrE = new Event[testEvents.size()];
        dataModel.setDataEvents(testEvents.toArray(arrE));

        dataModel.setFatherFilter(true);
        dataModel.setMotherFilter(true);
        dataModel.setMaleFilter(true);
        dataModel.setFemaleFilter(true);
        dataModel.filter();
    }

    @Test
    public void familyRelationshipPass() {

        List<FamilyMember> foundFamily = dataModel.getFamilyMembers(person);
        assertEquals(3, foundFamily.size());
        boolean foundMother = false;
        boolean foundFather = false;
        boolean foundChild = false;
        for (FamilyMember member : foundFamily) {
            if (member.getRelationship().equals("Father") &&
                    member.getPerson().equals(father)) {
                foundFather = true;
            } else if (member.getRelationship().equals("Mother") &&
                    member.getPerson().equals(mother)) {
                foundMother = true;
            } else if (member.getRelationship().equals("Child") &&
                    member.getPerson().equals(child)) {
                foundChild = true;
            }
        }
        assertTrue(foundFather && foundMother && foundChild);

    }

    @Test
    public void familyRelationshipFail() {

        // test incomplete child
        List<FamilyMember> foundFamily = dataModel.getFamilyMembers(child);
        assertEquals(1, foundFamily.size());

        // test incomplete father, mother
        foundFamily = dataModel.getFamilyMembers(father);
        assertEquals(2, foundFamily.size());
        foundFamily = dataModel.getFamilyMembers(mother);
        assertEquals(2, foundFamily.size());

        // test no relations
        foundFamily = dataModel.getFamilyMembers(random);
        assertEquals(0, foundFamily.size());

    }

    @Test
    public void eventFilterPass() {

        // no filters
        dataModel.setFatherFilter(true);
        dataModel.setMotherFilter(true);
        dataModel.setMaleFilter(true);
        dataModel.setFemaleFilter(true);
        dataModel.filter();
        assertEquals(6, dataModel.getFilteredEvents().length);

        // filter father's side
        dataModel.setFatherFilter(false);
        dataModel.setMotherFilter(true);
        dataModel.setMaleFilter(true);
        dataModel.setFemaleFilter(true);
        dataModel.filter();
        assertEquals(0, dataModel.getFilteredEvents().length);

        // filter male events
        dataModel.setFatherFilter(true);
        dataModel.setMotherFilter(true);
        dataModel.setMaleFilter(false);
        dataModel.setFemaleFilter(true);
        dataModel.filter();
        assertEquals(1, dataModel.getFilteredEvents().length);
    }

    @Test
    public void eventFilterFail() {

        // all filters
        dataModel.setFatherFilter(false);
        dataModel.setMotherFilter(false);
        dataModel.setMaleFilter(false);
        dataModel.setFemaleFilter(false);
        dataModel.filter();
        assertEquals(0, dataModel.getFilteredEvents().length);
    }

    @Test
    public void eventSortPass() {

        ArrayList<Event> orderedEvents = dataModel.orderPersonEvents(person);
        assertEquals(5, orderedEvents.size());
        assertEquals("Birth", orderedEvents.get(0).getEventType());
        assertEquals("Award", orderedEvents.get(2).getEventType());
        assertEquals("Death", orderedEvents.get(4).getEventType());
    }

    @Test
    public void eventSortFail() {

        // only one event
        ArrayList<Event> orderedEvents = dataModel.orderPersonEvents(mother);
        assertEquals(1, orderedEvents.size());

        // no events
        orderedEvents = dataModel.orderPersonEvents(random);
        assertEquals(0, orderedEvents.size());
    }

    @Test
    public void personSearchPass() {

        dataModel.personSearch("er");
        assertEquals(3, dataModel.getSearchPeople().size());
        dataModel.personSearch("random");
        assertEquals(1, dataModel.getSearchPeople().size());
    }

    @Test
    public void personSearchFail() {

        // no results
        dataModel.personSearch("hey");
        assertEquals(0, dataModel.getSearchPeople().size());

        // case insensitive
        dataModel.personSearch("PERSON");
        assertEquals(1, dataModel.getSearchPeople().size());
    }

    @Test
    public void eventSearchPass() {

        dataModel.eventSearch("provo");
        assertEquals(6, dataModel.getSearchEvents().size());
        dataModel.eventSearch("award");
        assertEquals(1, dataModel.getSearchEvents().size());
        dataModel.eventSearch("2040");
        assertEquals(3, dataModel.getSearchEvents().size());
    }

    @Test
    public void eventSearchFail() {

        // no results
        dataModel.eventSearch("hey");
        assertEquals(0, dataModel.getSearchEvents().size());

        // case insensitive
        dataModel.eventSearch("PROVO");
        assertEquals(6, dataModel.getSearchEvents().size());
    }
}