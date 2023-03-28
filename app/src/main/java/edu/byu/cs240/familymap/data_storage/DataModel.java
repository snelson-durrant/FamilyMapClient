package edu.byu.cs240.familymap.data_storage;

import model.Person;
import model.Event;
import model.User;

public class DataModel {

    private Person[] dataPeople;
    private Event[] dataEvents;
    private User dataUser;
    private String authtoken;
    private static DataModel instance;

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
}
