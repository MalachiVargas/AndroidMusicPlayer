package com.example.finalexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity implements LoginFragment.LoginInterfaceListener, SignUpFragment.CNAInterfaceListener {

    MaterialToolbar topAppBar;
    NestedScrollView authContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        authContainer = findViewById(R.id.authContainer);
        topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        authContainer.setNestedScrollingEnabled(false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(mAuthListener);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.authContainer, new LoginFragment(), "Login Fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_app_bar, menu);
        return true;
    }



    FirebaseAuth.AuthStateListener mAuthListener = firebaseAuth -> {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent mainIntent = new Intent(AuthActivity.this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
        }
    };

    @Override
    public void alLoginClicked() {
        // nothing
    }

    @Override
    public void alCNAClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.authContainer, new SignUpFragment(), "Sign Up Fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void acnaCancelClicked() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void acnaSubmitClicked() {
        // nothing
    }
}