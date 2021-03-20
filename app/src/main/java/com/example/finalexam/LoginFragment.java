package com.example.finalexam;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    MaterialButton cnaButton;
    MaterialButton loginButton;
    EditText emailInputLogin;
    EditText passwordInputLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        AppCompatActivity mActivity = (AppCompatActivity) getActivity();
        assert mActivity != null;
        Objects.requireNonNull(mActivity.getSupportActionBar()).setTitle(R.string.login_fragment);
        // Inflate the layout for this fragment
        View lView = inflater.inflate(R.layout.fragment_login, container, false);

        cnaButton = lView.findViewById(R.id.cnaButton);
        loginButton = lView.findViewById(R.id.loginButton);
        emailInputLogin = lView.findViewById(R.id.emailInputLogin);
        passwordInputLogin = lView.findViewById(R.id.passwordInputLogin);

        cnaButton.setOnClickListener(cnaListener);
        loginButton.setOnClickListener(loginListener);

        return lView;
    }

    View.OnClickListener cnaListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            liListener.alCNAClicked();
        }
    };

    View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String email = emailInputLogin.getText().toString();
            String password = passwordInputLogin.getText().toString();

            emailInputLogin.setText(R.string.blank);
            passwordInputLogin.setText(R.string.blank);

            final MaterialAlertDialogBuilder lfBuilder = new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()));
            lfBuilder.setTitle(R.string.error_dialog_title)
                    .setPositiveButton(R.string.okButton_dialog, (dialogInterface, i) -> {
                        // Nothing
                    });

            try {
                if (!Pattern.matches("[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+", email)) {
                    throw new Exception();
                }
            } catch (Exception ex) {
                lfBuilder.setMessage(R.string.email_valid).create().show();
                return;
            }

            try {
                if (password.isEmpty()) {
                    throw new Exception();
                }
            } catch (Exception ex) {
                lfBuilder.setMessage(R.string.password_valid).create().show();
                return;
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                        if (task.isSuccessful()) {
                            liListener.alLoginClicked();
                        } else {
                            lfBuilder.setMessage(Objects.requireNonNull(task.getException()).getMessage()).create().show();
                        }
                    });

        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LoginInterfaceListener) {
            liListener = (LoginInterfaceListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement LoginInterfaceListener");
        }
    }

    LoginInterfaceListener liListener;

    public interface LoginInterfaceListener {
        void alLoginClicked();
        void alCNAClicked();
    }
}