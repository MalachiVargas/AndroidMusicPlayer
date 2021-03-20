package com.example.finalexam;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    Button submitButtonCNA;
    Button cancelButtonCNA;

    EditText emailInputCNA;
    EditText nameInputCNA;
    EditText passwordInputCNA;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppCompatActivity mActivity = (AppCompatActivity) getActivity();
        assert mActivity != null;
        Objects.requireNonNull(mActivity.getSupportActionBar()).setTitle(R.string.sign_up_fragment);
        // Inflate the layout for this fragment
        View cnaView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        submitButtonCNA = cnaView.findViewById(R.id.submitButtonCNA);
        cancelButtonCNA = cnaView.findViewById(R.id.cancelButtonCNA);
        emailInputCNA = cnaView.findViewById(R.id.emailInputCNA);
        nameInputCNA = cnaView.findViewById(R.id.nameInputCNA);
        passwordInputCNA = cnaView.findViewById(R.id.passwordInputCNA);

        submitButtonCNA.setOnClickListener(submitListener);
        cancelButtonCNA.setOnClickListener(cancelListener);

        return cnaView;
    }

    View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String name = nameInputCNA.getText().toString();
            String email = emailInputCNA.getText().toString();
            String password = passwordInputCNA.getText().toString();

            nameInputCNA.setText(R.string.blank);
            emailInputCNA.setText(R.string.blank);
            passwordInputCNA.setText(R.string.blank);

            final MaterialAlertDialogBuilder ncaBuilder = new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()));
            ncaBuilder.setTitle(R.string.error_dialog_title)
                    .setPositiveButton(R.string.okButton_dialog, (dialogInterface, i) -> {
                        // Nothing
                    });

            try {
                if (!Pattern.matches("^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}", name)) {
                    throw new Exception();
                }
            } catch (Exception ex) {
                ncaBuilder.setMessage(R.string.name_valid).create().show();
                return;
            }

            try {
                if (!Pattern.matches("[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+", email)) {
                    throw new Exception();
                }
            } catch (Exception ex) {
                ncaBuilder.setMessage(R.string.email_valid).create().show();
                return;
            }

            try {
                if (password.isEmpty()) {
                    throw new Exception();
                }
            } catch (Exception ex) {
                ncaBuilder.setMessage(R.string.password_valid).create().show();
                return;
            }

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                        if (task.isSuccessful()) {

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            Map<String, Boolean> likes = new HashMap<>();
                            Map<String, Boolean> history = new HashMap<>();
                            Map<String, Map<String, Boolean>> shared = new HashMap<>();
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("name", name);
                            userMap.put("uid", user.getUid());
                            userMap.put("likes", likes);
                            userMap.put("history", history);
                            userMap.put("shared", shared);


                            Log.d("demo", "onClick: " + "helklo");
                            db.collection("users").document(user.getUid())
                                    .set(userMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("demo", "onComplete: " + user.getUid());
                                            cnaiListener.acnaSubmitClicked();
                                        }
                                    });

                            assert user != null;
                            Log.d("demo", "onClick: " + user);
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task1 -> {

                                    });
                        } else {
                            ncaBuilder.setMessage(Objects.requireNonNull(task.getException()).getMessage())
                                    .create().show();
                        }
                    });

        }
    };

    View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cnaiListener.acnaCancelClicked();
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof  CNAInterfaceListener) {
            cnaiListener = (CNAInterfaceListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement CNAInterfaceListener");
        }
    }

    CNAInterfaceListener cnaiListener;

    public interface CNAInterfaceListener {
        void acnaCancelClicked();
        void acnaSubmitClicked();
    }
}