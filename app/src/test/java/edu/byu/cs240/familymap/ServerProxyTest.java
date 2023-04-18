package edu.byu.cs240.familymap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

import edu.byu.cs240.familymap.data_storage.DataModel;
import edu.byu.cs240.familymap.server_connection.ServerProxy;
import request.LoginRequest;
import request.RegisterRequest;
import response.EventResponse;
import response.PersonResponse;
import response.RegisterResponse;

public class ServerProxyTest {

    private static DataModel dataModel;
    private static ServerProxy serverProxy;
    private static RegisterRequest registerRequest;
    private static String firstAuthtoken;

    @BeforeAll
    static void setUp() {

        // make sure to run tests on a clean database
        serverProxy = new ServerProxy("localhost", "8080");
        dataModel = DataModel.initialize();

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("john");
        registerRequest.setPassword("1234");
        registerRequest.setEmail("email@email.com");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Smith");
        registerRequest.setGender("m");
        RegisterResponse registerResponse = serverProxy.register(registerRequest);

        firstAuthtoken = registerResponse.getAuthtoken();
        dataModel.setAuthtoken(firstAuthtoken);
    }

    @Test
    public void loginPass() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(registerRequest.getUsername());
        loginRequest.setPassword(registerRequest.getPassword());

        RegisterResponse loginResponse = serverProxy.login(loginRequest);
        assertTrue(loginResponse.isSuccess());
        assertNotEquals(null, loginResponse.getAuthtoken());
    }

    @Test
    public void loginFail() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wrong");
        loginRequest.setPassword("wrong");

        RegisterResponse loginResponse = serverProxy.login(loginRequest);
        assertFalse(loginResponse.isSuccess());
    }

    @Test
    public void registerPass() {

        RegisterRequest nextRegisterRequest = new RegisterRequest();
        nextRegisterRequest.setUsername("mary");
        nextRegisterRequest.setPassword("4321");
        nextRegisterRequest.setEmail("yahoo@email.com");
        nextRegisterRequest.setFirstName("Mary");
        nextRegisterRequest.setLastName("Smith");
        nextRegisterRequest.setGender("f");

        RegisterResponse registerResponse = serverProxy.register(nextRegisterRequest);
        assertTrue(registerResponse.isSuccess());
        assertNotEquals(null, registerResponse.getAuthtoken());
    }

    @Test
    public void registerFail() {

        // same person twice
        RegisterResponse registerResponse = serverProxy.register(registerRequest);
        assertFalse(registerResponse.isSuccess());
    }

    @Test
    public void getPeoplePass() {

        dataModel.setAuthtoken(firstAuthtoken);

        PersonResponse personResponse = serverProxy.getAllPeople();
        assertTrue(personResponse.isSuccess());
        assertNotNull(personResponse.getData());
    }

    @Test
    public void getPeopleFail() {

        // wrong verification
        dataModel.setAuthtoken("wrong");

        PersonResponse personResponse = serverProxy.getAllPeople();
        assertNull(personResponse);
    }

    @Test
    public void getEventsPass() {

        dataModel.setAuthtoken(firstAuthtoken);

        EventResponse eventResponse = serverProxy.getAllEvents();
        assertTrue(eventResponse.isSuccess());
        assertNotNull(eventResponse.getData());
    }

    @Test
    public void getEventsFail() {

        // wrong verification
        dataModel.setAuthtoken("wrong");

        EventResponse eventResponse = serverProxy.getAllEvents();
        assertNull(eventResponse);
    }
}