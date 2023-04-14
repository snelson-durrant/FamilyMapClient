package edu.byu.cs240.familymap.data_storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.byu.cs240.familymap.server_connection.ServerProxy;
import model.Person;
import model.Event;
import model.User;

public class DataModel {

    private Person[] dataPeople;
    private Event[] dataEvents;
    private Event[] filteredEvents;
    private User dataUser;
    private Person dataUserPerson;
    private String authtoken;
    private static DataModel instance;

    // settings variables
    private boolean lifeStoryLines = true;
    private boolean familyTreeLines = true;
    private boolean spouseLines = true;
    private boolean fatherFilter = true;
    private boolean motherFilter = true;
    private boolean maleFilter = true;
    private boolean femaleFilter = true;

    // singleton
    public static DataModel initialize() {

        if (instance == null) {

            instance = new DataModel();
        }
        return instance;
    }

    public Person[] getDataPeople() {
        return dataPeople;
    }

    public void setDataPeople(Person[] dataPeople) {
        this.dataPeople = dataPeople;
    }

    public Event[] getDataEvents() {
        return dataEvents;
    }

    public void setDataEvents(Event[] dataEvents) {
        this.dataEvents = dataEvents;
    }

    public User getDataUser() {
        return dataUser;
    }

    public void setDataUser(User dataUser) {
        this.dataUser = dataUser;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public void setDataUserPerson(Person dataUserPerson) {
        this.dataUserPerson = dataUserPerson;
    }

    public boolean isLifeStoryLines() {
        return lifeStoryLines;
    }

    public void setLifeStoryLines(boolean lifeStoryLines) {
        this.lifeStoryLines = lifeStoryLines;
    }

    public boolean isFamilyTreeLines() {
        return familyTreeLines;
    }

    public void setFamilyTreeLines(boolean familyTreeLines) {
        this.familyTreeLines = familyTreeLines;
    }

    public boolean isSpouseLines() {
        return spouseLines;
    }

    public void setSpouseLines(boolean spouseLines) {
        this.spouseLines = spouseLines;
    }

    public void setFatherFilter(boolean fatherFilter) {
        this.fatherFilter = fatherFilter;
    }

    public void setMotherFilter(boolean motherFilter) {
        this.motherFilter = motherFilter;
    }

    public void setMaleFilter(boolean maleFilter) {
        this.maleFilter = maleFilter;
    }

    public void setFemaleFilter(boolean femaleFilter) {
        this.femaleFilter = femaleFilter;
    }

    public boolean isFatherFilter() {
        return fatherFilter;
    }

    public boolean isMotherFilter() {
        return motherFilter;
    }

    public boolean isMaleFilter() {
        return maleFilter;
    }

    public boolean isFemaleFilter() {
        return femaleFilter;
    }

    public Event[] getFilteredEvents() {
        return filteredEvents;
    }

    public List<String> getAllEventTypes() {

        List<String> eventTypes = new ArrayList<>();

        for (Event event : dataEvents) {

            boolean found = false;
            for (String eventType : eventTypes) {
                if (event.getEventType().equals(eventType)) {
                    found = true;
                }
            }

            if (!found) {
                eventTypes.add(event.getEventType());
            }
        }

        return eventTypes;
    }

    public void filter() {

        ArrayList<Event> tempEvents = new ArrayList<>();
        ArrayList<Event> tempEvents2 = new ArrayList<>();

        for (Event event : dataEvents) {

            if (dataUserPerson.getPersonID().equals(event.getPersonID())) {
                tempEvents.add(event);
            }
        }
        if (fatherFilter) {

            genRecurse(dataUserPerson.getFatherID(), tempEvents);
        }
        if (motherFilter) {

            genRecurse(dataUserPerson.getMotherID(), tempEvents);
        }
        if (maleFilter) {

            for (Event event : tempEvents) {
                for (Person person : dataPeople) {

                    if (event.getPersonID().equals(person.getPersonID())) {
                        if (person.getGender().equals("m")) {
                            tempEvents2.add(event);
                        }
                    }
                }
            }
        }
        if (femaleFilter) {

            for (Event event : tempEvents) {
                for (Person person : dataPeople) {

                    if (event.getPersonID().equals(person.getPersonID())) {
                        if (person.getGender().equals("f")) {
                            tempEvents2.add(event);
                        }
                    }
                }
            }
        }

        filteredEvents = tempEvents2.toArray(new Event[0]);
    }

    private void genRecurse(String inputID, ArrayList<Event> events) {

        if (inputID != null) {
            for (Event event : dataEvents) {
                if (inputID.equals(event.getPersonID())) {
                    events.add(event);
                }
            }

            for (Person person : dataPeople) {
                if (inputID.equals(person.getPersonID())) {
                    genRecurse(person.getFatherID(), events);
                    genRecurse(person.getMotherID(), events);
                }
            }
        }
    }
}
