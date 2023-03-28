package edu.byu.cs240.familymap.server_connection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import edu.byu.cs240.familymap.data_storage.DataModel;
import model.User;
import request.RegisterRequest;
import response.RegisterResponse;

public class RegisterTask implements Runnable {

    private String serverHost;
    private String serverPort;
    private RegisterRequest registerRequest;
    private Handler messageHandler;

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
