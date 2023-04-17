package edu.byu.cs240.familymap.data_storage;

import model.Person;

public class FamilyMember {

    private final Person person;
    private final String relationship;

    public FamilyMember(Person person, String relationship) {
        this.person = person;
        this.relationship = relationship;
    }

    public Person getPerson() {
        return person;
    }

    public String getRelationship() {
        return relationship;
    }
}