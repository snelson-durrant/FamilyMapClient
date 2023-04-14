package edu.byu.cs240.familymap.server_connection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import edu.byu.cs240.familymap.data_storage.DataModel;
import model.Person;
import model.User;
import request.LoginRequest;
import response.PersonIDResponse;
import response.RegisterResponse;

public class LoginTask implements Runnable {

    private String serverHost;
    private String serverPort;
    private LoginRequest loginRequest;
    private Handler messageHandler;

    public LoginTask(Handler messageHandler, String serverHost, String serverPort, LoginRequest loginRequest) {

        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.loginRequest = loginRequest;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {

        ServerProxy serverProxy = new ServerProxy(serverHost, serverPort);
        RegisterResponse loginResponse = serverProxy.login(loginRequest);

        if (loginResponse.isSuccess()) {

            DataModel dataModel = DataModel.initialize();
            dataModel.setAuthtoken(loginResponse.getAuthtoken());
            dataModel.setDataPeople(serverProxy.getAllPeople().getData());
            dataModel.setDataEvents(serverProxy.getAllEvents().getData());
            User currentUser = new User(loginResponse.getUsername(),
                    loginRequest.getPassword(),
                    "", // there seems to be no way to get email after registration
                    serverProxy.getPerson(loginResponse.getPersonID()).getFirstName(),
                    serverProxy.getPerson(loginResponse.getPersonID()).getLastName(),
                    serverProxy.getPerson(loginResponse.getPersonID()).getGender(),
                    loginResponse.getPersonID());
            dataModel.setDataUser(currentUser);
            PersonIDResponse personIDResponse = serverProxy.getPerson(currentUser.getPersonID());
            Person currentPerson = new Person(personIDResponse.getPersonID(),
                    personIDResponse.getAssociatedUsername(),
                    personIDResponse.getFirstName(),
                    personIDResponse.getLastName(),
                    personIDResponse.getGender(),
                    personIDResponse.getFatherID(),
                    personIDResponse.getMotherID(),
                    personIDResponse.getSpouseID());
            dataModel.setDataUserPerson(currentPerson);
        }

        sendMessage(loginResponse.isSuccess());
    }

    private void sendMessage(boolean isSuccess) {

        Message message = Message.obtain();
        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean("SUCCESS_KEY", isSuccess);
        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }
}
