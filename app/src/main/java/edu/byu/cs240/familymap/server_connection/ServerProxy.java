package edu.byu.cs240.familymap.server_connection;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.byu.cs240.familymap.data_storage.DataModel;
import json.Encoder;
import request.LoginRequest;
import request.RegisterRequest;
import response.EventIDResponse;
import response.EventResponse;
import response.PersonIDResponse;
import response.PersonResponse;
import response.RegisterResponse;

public class ServerProxy {

    private final String serverHost;
    private final String serverPort;

    public ServerProxy(String serverHost, String serverPort) {

        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public RegisterResponse register(RegisterRequest registerRequest) {

        try {

            RegisterResponse registerResponse;

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");

            http.connect();
            OutputStream request = http.getOutputStream();
            writeString(Encoder.encode(registerRequest), request);
            request.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                Gson gson = new Gson();
                Reader reader = new InputStreamReader(http.getInputStream());
                registerResponse = gson.fromJson(reader, RegisterResponse.class);
                return registerResponse;
            } else {

                System.out.println("SERVER ERROR: " + http.getResponseMessage());

                RegisterResponse errResponse = new RegisterResponse();
                errResponse.setSuccess(false);
                return errResponse;
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }

    public RegisterResponse login(LoginRequest loginRequest) {

        try {

            RegisterResponse loginResponse;

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");

            http.connect();
            OutputStream request = http.getOutputStream();
            writeString(Encoder.encode(loginRequest), request);
            request.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                Gson gson = new Gson();
                Reader reader = new InputStreamReader(http.getInputStream());
                loginResponse = gson.fromJson(reader, RegisterResponse.class);
                return loginResponse;
            } else {

                System.out.println("SERVER ERROR: " + http.getResponseMessage());

                RegisterResponse errResponse = new RegisterResponse();
                errResponse.setSuccess(false);
                return errResponse;
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }

    public PersonResponse getAllPeople() {

        try {

            PersonResponse personResponse;

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Accept", "application/json");

            http.addRequestProperty("Authorization", DataModel.initialize().getAuthtoken());

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                Gson gson = new Gson();
                Reader reader = new InputStreamReader(http.getInputStream());
                personResponse = gson.fromJson(reader, PersonResponse.class);
                return personResponse;
            } else {

                System.out.println("SERVER ERROR: " + http.getResponseMessage());
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }

    public EventResponse getAllEvents() {

        try {

            EventResponse eventResponse;

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Accept", "application/json");

            http.addRequestProperty("Authorization", DataModel.initialize().getAuthtoken());

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                Gson gson = new Gson();
                Reader reader = new InputStreamReader(http.getInputStream());
                eventResponse = gson.fromJson(reader, EventResponse.class);
                return eventResponse;
            } else {

                System.out.println("SERVER ERROR: " + http.getResponseMessage());
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }

    public PersonIDResponse getPerson(String personID) {

        try {

            PersonIDResponse personIDResponse;

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person/" + personID);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Accept", "application/json");

            http.addRequestProperty("Authorization", DataModel.initialize().getAuthtoken());

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                Gson gson = new Gson();
                Reader reader = new InputStreamReader(http.getInputStream());
                personIDResponse = gson.fromJson(reader, PersonIDResponse.class);
                return personIDResponse;
            } else {

                System.out.println("SERVER ERROR: " + http.getResponseMessage());
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }

    public EventIDResponse getEvent() {

        try {

            EventIDResponse eventIDResponse;

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Accept", "application/json");

            http.addRequestProperty("Authorization", DataModel.initialize().getAuthtoken());

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                Gson gson = new Gson();
                Reader reader = new InputStreamReader(http.getInputStream());
                eventIDResponse = gson.fromJson(reader, EventIDResponse.class);
                return eventIDResponse;
            } else {

                System.out.println("SERVER ERROR: " + http.getResponseMessage());
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }

    private void writeString(String str, OutputStream os) throws IOException {

        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
