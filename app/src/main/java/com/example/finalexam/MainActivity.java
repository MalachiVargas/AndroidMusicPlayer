package com.example.finalexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements SearchFragment.SearchInterfaceListener,
        LikesFragment.LikesInterfaceListener, SharedFragment.SharedInterfaceListener {

    MaterialToolbar mainAppBar;
    ViewPager2 mainViewPager;
    ViewPagerAdapter viewPagerAdapter;
    TabLayout mainTabLayout;

    final static public String ALBUM_KEY = "ALBUM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewPager = findViewById(R.id.mainViewPager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        mainViewPager.setAdapter(viewPagerAdapter);

        mainAppBar = findViewById(R.id.mainAppBar);
        setSupportActionBar(mainAppBar);

        mainTabLayout = findViewById(R.id.mainTabLayout);

        new TabLayoutMediator(mainTabLayout, mainViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                String[] tabNames = new String[]{"Search", "Likes", "History", "Shared"};
                tab.setText(tabNames[position]);
            }
        }).attach();

    }

    @Override
    public void aAlbumClicked(Album album) {
        Intent albumIntent = new Intent(MainActivity.this, AlbumActivity.class);
        albumIntent.putExtra(ALBUM_KEY, album);
        startActivity(albumIntent);
    }

    @Override
    public void liAlbumClicked(Album album) {
        Intent albumIntent = new Intent(MainActivity.this, AlbumActivity.class);
        albumIntent.putExtra(ALBUM_KEY, album);
        startActivity(albumIntent);
    }

    @Override
    public void sdiSharedClicked(Album album) {
        Intent albumIntent = new Intent(MainActivity.this, AlbumActivity.class);
        albumIntent.putExtra(ALBUM_KEY, album);
        startActivity(albumIntent);
    }

    public static class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            switch (position) {
                case 0:
                    return new SearchFragment();
                case 1:
                    return new LikesFragment();
                case 2:
                    return new HistoryFragment();
                case 3:
                    return new SharedFragment();
            }

            return null;
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.loTopAppBar) {

            FirebaseAuth.getInstance().signOut();

            Intent mainIntent = new Intent(MainActivity.this, AuthActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}