package edu.byu.cs240.familymap.user_interface;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs240.familymap.R;
import edu.byu.cs240.familymap.data_storage.DataModel;
import edu.byu.cs240.familymap.server_connection.LoginTask;
import edu.byu.cs240.familymap.server_connection.RegisterTask;
import request.LoginRequest;
import request.RegisterRequest;

public class LoginFragment extends Fragment {

    private Button registerButton;
    private Button signInButton;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private EditText serverHostEditText;
    private EditText serverPortEditText;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;

    private Listener listener;

    public interface Listener {

        void notifyDone();
    }

    public void registerListener(Listener listener) {

        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        serverHostEditText = view.findViewById(R.id.serverHostField);
        serverPortEditText = view.findViewById(R.id.serverPortField);
        userNameEditText = view.findViewById(R.id.userNameField);
        passwordEditText = view.findViewById(R.id.passwordField);
        firstNameEditText = view.findViewById(R.id.firstNameField);
        lastNameEditText = view.findViewById(R.id.lastNameField);
        emailEditText = view.findViewById(R.id.emailField);
        RadioGroup genderRadioGroup = view.findViewById(R.id.genderRadioGroup);
        maleRadioButton = view.findViewById(R.id.maleRadioButton);
        femaleRadioButton = view.findViewById(R.id.femaleRadioButton);
        registerButton = view.findViewById(R.id.registerButton);
        signInButton = view.findViewById(R.id.signInButton);

        signInButton.setEnabled(false);
        registerButton.setEnabled(false);

        registerButton.setOnClickListener(v -> {

            if(listener != null) {

                Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {

                        Bundle bundle = message.getData();
                        boolean success = bundle.getBoolean("SUCCESS_KEY", false);

                        if (success) {

                            String successMessage = "Successfully registered " +
                                    DataModel.initialize().getDataUser().getFirstName() + " " +
                                    DataModel.initialize().getDataUser().getLastName();
                            Toast.makeText(getContext(), successMessage, Toast.LENGTH_SHORT).show();

                            listener.notifyDone();
                        } else {
                            Toast.makeText(getContext(), "ERROR: Registration Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                String gender;
                if (maleRadioButton.isChecked()) {
                    gender = "m";
                } else {
                    gender = "f";
                }

                RegisterRequest registerRequest = new RegisterRequest();
                registerRequest.setUsername(userNameEditText.getText().toString());
                registerRequest.setPassword(passwordEditText.getText().toString());
                registerRequest.setFirstName(firstNameEditText.getText().toString());
                registerRequest.setLastName(lastNameEditText.getText().toString());
                registerRequest.setEmail(emailEditText.getText().toString());
                registerRequest.setGender(gender);

                RegisterTask registerTask = new RegisterTask(uiThreadMessageHandler,
                        serverHostEditText.getText().toString(),
                        serverPortEditText.getText().toString(),
                        registerRequest);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(registerTask);
            }
        });

        signInButton.setOnClickListener(v -> {

            if(listener != null) {

                Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {

                        Bundle bundle = message.getData();
                        boolean success = bundle.getBoolean("SUCCESS_KEY", false);

                        if (success) {

                            String successMessage = "Successfully signed in " +
                                    DataModel.initialize().getDataUser().getFirstName() + " " +
                                    DataModel.initialize().getDataUser().getLastName();
                            Toast.makeText(getContext(), successMessage, Toast.LENGTH_SHORT).show();

                            listener.notifyDone();
                        } else {
                            Toast.makeText(getContext(), "ERROR: Sign In Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setUsername(userNameEditText.getText().toString());
                loginRequest.setPassword(passwordEditText.getText().toString());

                LoginTask loginTask = new LoginTask(uiThreadMessageHandler,
                        serverHostEditText.getText().toString(),
                        serverPortEditText.getText().toString(),
                        loginRequest);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(loginTask);
            }
        });

        userNameEditText.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {

                enableButtons();
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {

                enableButtons();
            }
        });

        firstNameEditText.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {

                enableButtons();
            }
        });

        lastNameEditText.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {

                enableButtons();
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {

                enableButtons();
            }
        });

        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                enableButtons();
            }
        });

        return view;
    }

    private void enableButtons() {

        registerButton.setEnabled(qualifyRegisterButton());
        signInButton.setEnabled(qualifySignInButton());
    }

    private boolean qualifyRegisterButton() {

        return !userNameEditText.getText().toString().equals("") &&
                !passwordEditText.getText().toString().equals("") &&
                !firstNameEditText.getText().toString().equals("") &&
                !lastNameEditText.getText().toString().equals("") &&
                !emailEditText.getText().toString().equals("") &&
                (maleRadioButton.isChecked() | femaleRadioButton.isChecked());
    }

    private boolean qualifySignInButton() {

        return !userNameEditText.getText().toString().equals("") &&
                !passwordEditText.getText().toString().equals("");
    }
}


