package edu.byu.cs240.familymap.server_connection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import edu.byu.cs240.familymap.data_storage.DataModel;
import model.Person;
import model.User;
import request.RegisterRequest;
import response.PersonIDResponse;
import response.RegisterResponse;

public class RegisterTask implements Runnable {

    private final String serverHost;
    private final String serverPort;
    private final RegisterRequest registerRequest;
    private final Handler messageHandler;

    public RegisterTask(Handler messageHandler, String serverHost, String serverPort, RegisterRequest registerRequest) {

        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.registerRequest = registerRequest;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {

        ServerProxy serverProxy = new ServerProxy(serverHost, serverPort);
        RegisterResponse registerResponse = serverProxy.register(registerRequest);

        if (registerResponse.isSuccess()) {

            DataModel dataModel = DataModel.initialize();
            dataModel.setAuthtoken(registerResponse.getAuthtoken());
            dataModel.setDataPeople(serverProxy.getAllPeople().getData());
            dataModel.setDataEvents(serverProxy.getAllEvents().getData());
            User currentUser = new User(registerResponse.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail(),
                    registerRequest.getFirstName(),
                    registerRequest.getLastName(),
                    registerRequest.getGender(),
                    registerResponse.getPersonID());
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

        sendMessage(registerResponse.isSuccess());
    }

    private void sendMessage(boolean isSuccess) {

        Message message = Message.obtain();
        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean("SUCCESS_KEY", isSuccess);
        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }
}
