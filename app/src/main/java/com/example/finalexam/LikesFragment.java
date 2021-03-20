package com.example.finalexam;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class LikesFragment extends Fragment implements SearchRVAdapter.OnAlbumListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LikesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    RecyclerView likesRV;
    SearchRVAdapter likesRVAdapter;
    LinearLayoutManager likesLM;

    ArrayList<Album> albums = new ArrayList<>();
    User userObject = new User();
    Album album;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View likesView = inflater.inflate(R.layout.fragment_likes, container, false);
        likesRV = likesView.findViewById(R.id.likesRV);
        setAdapter();
        getUserObject();
        return likesView;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity mActivity = (AppCompatActivity) getActivity();
        assert mActivity != null;
        Objects.requireNonNull(mActivity.getSupportActionBar()).setTitle(R.string.likes_fragment);
    }

    void getUserObject() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("users").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                User userTemp = value.toObject(User.class);
                assert userTemp != null;
                userObject.setHistory(userTemp.getHistory());
                userObject.setLikes(userTemp.getLikes());
                userObject.setName(userTemp.getName());
                userObject.setUid(userTemp.getUid());
                likesRVAdapter.notifyDataSetChanged();
                getLikes();
            }
        });
    }

    void getLikes() {
        db.collection("users").document(userObject.getUid())
                .collection("likes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        albums.clear();
                        assert value != null;
                        for (QueryDocumentSnapshot document : value) {
                            album = document.toObject(Album.class);
                            albums.add(album);
                            likesRVAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void setAdapter() {
        likesRV.setHasFixedSize(true);
        likesLM = new LinearLayoutManager(getActivity());
        likesRV.setLayoutManager(likesLM);
        likesRVAdapter = new SearchRVAdapter(this, albums, userObject);
        likesRV.setAdapter(likesRVAdapter);
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
    public void onLikeClick(int position) {
        Album album = albums.get(position);
        if (!userObject.getLikes().containsKey(album.getAlbum_id())) {
            userObject.getLikes().put(album.getAlbum_id(), true);
            addLikes(album);
        } else {
            userObject.getLikes().remove(album.getAlbum_id());
            removeLikes(album);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        HashMap<String, Object> ulObject = new HashMap<>();
        ulObject.put("likes", userObject.getLikes());

        db.collection("users").document(user.getUid())
                .update(ulObject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        likesRVAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onAlbumClick(int position) {
        liListener.liAlbumClicked(albums.get(position));
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LikesInterfaceListener) {
            liListener = (LikesInterfaceListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement LikesInterfaceListener");
        }
    }

    LikesInterfaceListener liListener;


    public interface LikesInterfaceListener {
        void liAlbumClicked(Album album);
    }
}