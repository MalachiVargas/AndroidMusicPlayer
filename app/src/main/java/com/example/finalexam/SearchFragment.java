package com.example.finalexam;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SearchFragment extends Fragment implements SearchRVAdapter.OnAlbumListener {

    private final OkHttpClient client = new OkHttpClient();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getUserObject();
    }

    EditText searchBar;
    MaterialButton searchButton;
    RecyclerView searchRV;
    SearchRVAdapter searchRVAdapter;
    LinearLayoutManager searchLM;

    ArrayList<Album> albums = new ArrayList<>();
    User userObject = new User();
    Album album;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View searchFragment = inflater.inflate(R.layout.fragment_search, container, false);
        searchBar = searchFragment.findViewById(R.id.searchBar);
        searchRV = searchFragment.findViewById(R.id.searchRV);
        searchButton = searchFragment.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(searchListener);
        setAdapter();


        return searchFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity mActivity = (AppCompatActivity) getActivity();
        assert mActivity != null;
        Objects.requireNonNull(mActivity.getSupportActionBar()).setTitle(R.string.search_fragment);
    }

    View.OnClickListener searchListener = view -> getAlbums();

    private void getAlbums() {
        String searchQuery = searchBar.getText().toString();

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse("https://api.deezer.com/search/album")).newBuilder()
                .addQueryParameter("q", searchQuery)
                .build();


        Request getRequest = new Request.Builder()
                .url(url)
                .build();

        client.newCall(getRequest).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray albumList = json.getJSONArray("data");
                    albums.clear();
                    for (int i = 0; i < albumList.length(); i++) {

                        album = new Album();
                        JSONObject jsonObject = albumList.getJSONObject(i);

                        album.setAlbum_id(jsonObject.getString("id"));
                        album.setTitle(jsonObject.getString("title"));
                        album.setCover_small(jsonObject.getString("cover_small"));
                        album.setCover_big(jsonObject.getString("cover_big"));
                        album.setNb_tracks(jsonObject.getString("nb_tracks"));
                        album.setArtist_name(jsonObject.getJSONObject("artist").getString("name"));
                        album.setArtist_picture(jsonObject.getJSONObject("artist").getString("picture"));

                        albums.add(album);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    searchBar.setText(R.string.blank);
                    searchRVAdapter.notifyDataSetChanged();
                });

            }
        });
    }

    private void setAdapter() {
        searchRV.setHasFixedSize(true);
        searchLM = new LinearLayoutManager(getActivity());
        searchRV.setLayoutManager(searchLM);
        searchRVAdapter = new SearchRVAdapter(this, albums, userObject);
        searchRV.setAdapter(searchRVAdapter);
    }

    @Override
    public void onLikeClick(int position) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Album album = albums.get(position);
        if (!userObject.getLikes().containsKey(album.getAlbum_id())) {
            userObject.getLikes().put(album.getAlbum_id(), true);
            addLikes(album);
        } else {
            userObject.getLikes().remove(album.getAlbum_id());
            removeLikes(album);
        }

        HashMap<String, Object> ulObject = new HashMap<>();
        ulObject.put("likes", userObject.getLikes());

        db.collection("users").document(user.getUid())
                .update(ulObject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        searchRVAdapter.notifyDataSetChanged();
                    }
                });
    }

    void getUserObject() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("users").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                User userTemp = value.toObject(User.class);
                assert userTemp != null;
                userObject.setLikes(userTemp.getLikes());
                userObject.setHistory(userTemp.getHistory());
                userObject.setName(userTemp.getName());
                userObject.setUid(userTemp.getUid());
                searchRVAdapter.notifyDataSetChanged();
            }
        });
    }

    void addLikes(Album album) {
        HashMap<String, Object> likesObject = new HashMap<>();
        likesObject.put("album_id", album.getAlbum_id());
        likesObject.put("title", album.getTitle());
        likesObject.put("artist_name", album.getArtist_name());
        likesObject.put("artist_picture", album.getArtist_picture());
        likesObject.put("nb_tracks", album.getNb_tracks());
        likesObject.put("cover_small", album.getCover_small());
        likesObject.put("cover_big", album.getCover_big());


        db.collection("users").document(userObject.getUid())
                .collection("likes").document(album.getAlbum_id())
                .set(likesObject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // nothing
                    }
                });
    }

    void removeLikes(Album album) {
        db.collection("users").document(userObject.getUid())
                .collection("likes").document(album.getAlbum_id())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //nothing
                    }
                });
    }

    @Override
    public void onAlbumClick(int position) {
        siListener.aAlbumClicked(albums.get(position));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SearchInterfaceListener) {
            siListener = (SearchInterfaceListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement SearchInterfaceListener");
        }
    }

    SearchInterfaceListener siListener;

    public interface SearchInterfaceListener {
        void aAlbumClicked(Album album);
    }

}