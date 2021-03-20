package com.example.finalexam;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AlbumSharingFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String ARG_AS = "Album Sharing";
    Album album;

    public AlbumSharingFragment() {
        // Required empty public constructor
    }

    public static AlbumSharingFragment newInstance(Album album) {
        AlbumSharingFragment fragment = new AlbumSharingFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_AS, album);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            album = (Album) getArguments().getSerializable(ARG_AS);
        }
    }

    TextView albumTitleAS;
    ListView usersLV;
    ArrayAdapter<String> ulvAdapter;
    ArrayList<String> users = new ArrayList<>();
    ArrayList<String> uids = new ArrayList<>();
    User userObject = new User();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppCompatActivity mActivity = (AppCompatActivity) getActivity();
        assert mActivity != null;
        Objects.requireNonNull(mActivity.getSupportActionBar()).setTitle(R.string.album_sharing_fragment);
        // Inflate the layout for this fragment
        View asView = inflater.inflate(R.layout.fragment_album_sharing, container, false);
            usersLV = asView.findViewById(R.id.usersLV);
            albumTitleAS = asView.findViewById(R.id.albumTitleAS);
            albumTitleAS.setText(album.getTitle());
            ulvAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, users);
            getUserObject();
            usersLV.setAdapter(ulvAdapter);
            usersLV.setOnItemClickListener(userListener);
        return asView;
    }

    AdapterView.OnItemClickListener userListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            FirebaseUser userFB = FirebaseAuth.getInstance().getCurrentUser();
            String uid = uids.get(i);

            Log.d("demo", "onItemClick: " + userObject.getShared().get(uid));
            Log.d("demo", "onItemClick: " + userObject.getShared().isEmpty());
            if (userObject.getShared() == null || userObject.getShared().isEmpty() ) {
                Map<String, Map<String, Boolean>> hmU = new HashMap<>();
                Map<String, Boolean> hmA = new HashMap<>();
                hmA.put(album.getAlbum_id(), true);
                hmU.put(uid, hmA);
                userObject.setShared(hmU);
            } else if (!userObject.getShared().containsKey(uid)) {
                Log.d("demo", "onItemClick:" + uid);
                Map<String, Boolean> hmA = new HashMap<>();
                hmA.put(album.getAlbum_id(), true);
                userObject.getShared().put(uid, hmA);
            } else if (!userObject.getShared().get(uid).containsKey(album.getAlbum_id())) {
                userObject.getShared().get(uid).put(album.getAlbum_id(), true);
            }
            HashMap<String, Object> usObject = new HashMap<>();
            usObject.put("shared", userObject.getShared());

            db.collection("users").document(userFB.getUid())
                    .update(usObject)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            addShared(uid);
                        }
                    });
        }
    };

    void getUserObject() {
        FirebaseUser userFB = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("users").document(userFB.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                User userTemp = value.toObject(User.class);
                assert userTemp != null;
                userObject.setShared(userTemp.getShared());
                userObject.setHistory(userTemp.getHistory());
                userObject.setLikes(userTemp.getLikes());
                userObject.setName(userTemp.getName());
                userObject.setUid(userTemp.getUid());
                getUserStrings();
            }
        });
    }

    void addShared(String uid) {
        HashMap<String, Object> sharedObject = new HashMap<>();
        sharedObject.put("nb_tracks", album.getNb_tracks());
        sharedObject.put("sharedBy", userObject.getName());
        sharedObject.put("album_title", album.getTitle());
        sharedObject.put("artist_name", album.getArtist_name());
        sharedObject.put("cover_small", album.getCover_small());
        sharedObject.put("album_id", album.getAlbum_id());
        sharedObject.put("artist_picture", album.getArtist_picture());
        sharedObject.put("cover_big", album.getCover_big());

        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        String dts = ts.toString();
        sharedObject.put("date", dts);


        db.collection("users").document(uid)
                .collection("shared").document(album.getAlbum_id())
                .set(sharedObject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ulListener.aUserClicked();
                    }
                });
    }

    private void getUserStrings() {
        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                users.clear();
                assert value != null;
                for (QueryDocumentSnapshot document : value) {
                    if (!document.getString("name").equals(userObject.getName())) {
                        users.add(document.getString("name"));
                        uids.add(document.getString("uid"));
                    }
                }
                ulvAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UserListIL) {
            ulListener = (UserListIL) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement UserListIL");
        }
    }

    UserListIL ulListener;

    public interface UserListIL {
        void aUserClicked();
    }

}