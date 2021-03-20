package com.example.finalexam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.os.Bundle;
import android.view.Menu;

import com.google.android.material.appbar.MaterialToolbar;

public class AlbumActivity extends AppCompatActivity implements AlbumFragment.AlbumInterfaceListener, AlbumSharingFragment.UserListIL {

    Album album;
    NestedScrollView albumContainer;
    MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        albumContainer = findViewById(R.id.albumContainer);
        albumContainer.setNestedScrollingEnabled(false);

        topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().hasExtra(MainActivity.ALBUM_KEY)) {
            album = (Album) getIntent().getSerializableExtra(MainActivity.ALBUM_KEY);
            assert album != null;

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.albumContainer, AlbumFragment.newInstance(album), "Album Fragment")
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_app_bar, menu);
        return true;
    }

    @Override
    public void aShareClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.albumContainer, AlbumSharingFragment.newInstance(album), "Album Sharing Fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void aUserClicked() {
        getSupportFragmentManager().popBackStack();
    }
}