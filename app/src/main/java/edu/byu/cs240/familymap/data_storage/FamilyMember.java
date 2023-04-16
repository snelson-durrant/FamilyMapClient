package edu.byu.cs240.familymap.data_storage;

import model.Person;

public class FamilyMember {

    private Person person;
    private String relationship;

    public FamilyMember(Person person, String relationship) {
        this.person = person;
        this.relationship = relationship;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}