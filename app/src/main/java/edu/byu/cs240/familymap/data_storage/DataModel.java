package edu.byu.cs240.familymap.data_storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    // transition variables
    private Person transitionPerson;
    private Event transitionEvent;

    // singleton logic
    public static DataModel initialize() {

        if (instance == null) {
            instance = new DataModel();
        }
        return instance;
    }

    public Person getTransitionPerson() {
        return transitionPerson;
    }

    public void setTransitionPerson(Person transitionPerson) {
        this.transitionPerson = transitionPerson;
    }

    public Event getTransitionEvent() {
        return transitionEvent;
    }

    public void setTransitionEvent(Event transitionEvent) {
        this.transitionEvent = transitionEvent;
    }

    public Person[] getDataPeople() {
        return dataPeople;
    }

    public void setDataPeople(Person[] dataPeople) {
        this.dataPeople = dataPeople;
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

    public Person getDataUserPerson() {return dataUserPerson;}

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

    public void setFamilyTreeLines(boolean familyTreeLines) {this.familyTreeLines = familyTreeLines;}

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
                if (event.getEventType().equalsIgnoreCase(eventType)) {
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

        ArrayList<Event> sideFiltered = new ArrayList<>();
        ArrayList<Event> genderFiltered = new ArrayList<>();

        for (Event event : dataEvents) {
            if (dataUserPerson.getPersonID().equals(event.getPersonID())) {
                sideFiltered.add(event);
            }
        }

        if (fatherFilter) {
            genRecurse(dataUserPerson.getFatherID(), sideFiltered);
        }
        if (motherFilter) {
            genRecurse(dataUserPerson.getMotherID(), sideFiltered);
        }

        if (maleFilter) {

            for (Event event : sideFiltered) {
                for (Person person : dataPeople) {

                    if (event.getPersonID().equals(person.getPersonID())) {
                        if (person.getGender().equals("m")) {
                            genderFiltered.add(event);
                        }
                    }
                }
            }
        }

        if (femaleFilter) {

            for (Event event : sideFiltered) {
                for (Person person : dataPeople) {

                    if (event.getPersonID().equals(person.getPersonID())) {
                        if (person.getGender().equals("f")) {
                            genderFiltered.add(event);
                        }
                    }
                }
            }
        }

        filteredEvents = genderFiltered.toArray(new Event[0]);
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

    public ArrayList<Event> orderPersonEvents(Person inputPerson) {

        ArrayList<Event> tempEventList = new ArrayList<>();
        Event personBirthEvent = null;
        Event personDeathEvent = null;
        for (Event event : filteredEvents) {
            if (event.getPersonID().equals(inputPerson.getPersonID())) {
                if (event.getEventType().equalsIgnoreCase("birth")) {
                    personBirthEvent = event;
                } else if (event.getEventType().equalsIgnoreCase("death")) {
                    personDeathEvent = event;
                } else {
                    tempEventList.add(event);
                }
            }
        }

        ArrayList<Event> orderedEventList = new ArrayList<>();
        if (personBirthEvent != null) {
            orderedEventList.add(personBirthEvent);
        }

        if (!tempEventList.isEmpty()) {

            Event nextEvent = tempEventList.get(0);
            while (!tempEventList.isEmpty()) {

                for (Event tempEvent : tempEventList) {
                    if (tempEvent.getYear() < nextEvent.getYear()) {

                        nextEvent = tempEvent;
                    } else if (tempEvent.getYear().equals(nextEvent.getYear())) {

                        int compare = tempEvent.getEventType().compareTo(nextEvent.getEventType());
                        if (compare < 0) {
                            // tempEvent's eventType is first alphabetically
                            nextEvent = tempEvent;
                        }
                    }
                }

                orderedEventList.add(nextEvent);
                tempEventList.remove(nextEvent);
            }
        }

        if (personDeathEvent != null) {
            orderedEventList.add(personDeathEvent);
        }

        return orderedEventList;
    }

    public List<FamilyMember> getFamilyMembers(Person inputPerson) {

        ArrayList<FamilyMember> familyMemberList = new ArrayList<>();
        for (Person person : dataPeople) {
            if (inputPerson.getFatherID() != null) {
                if (person.getPersonID().equals(inputPerson.getFatherID())) {
                    familyMemberList.add(new FamilyMember(person, "Father"));
                }
            }
            if (inputPerson.getMotherID() != null) {
                if (person.getPersonID().equals(inputPerson.getMotherID())) {
                    familyMemberList.add(new FamilyMember(person, "Mother"));
                }
            }
            if (person.getPersonID().equals(inputPerson.getSpouseID())) {
                if (inputPerson.getSpouseID() != null) {
                    familyMemberList.add(new FamilyMember(person, "Spouse"));
                }
            }
            if (person.getFatherID() != null) {
                if (person.getFatherID().equals(inputPerson.getPersonID())) {
                    familyMemberList.add(new FamilyMember(person, "Child"));
                }
            }
            if (person.getMotherID() != null) {
                if (person.getMotherID().equals(inputPerson.getPersonID())) {
                    familyMemberList.add(new FamilyMember(person, "Child"));
                }
            }
        }

        return familyMemberList;
    }
}
