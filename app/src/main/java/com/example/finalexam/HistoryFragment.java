package com.example.finalexam;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import java.util.HashMap;
import java.util.Objects;

public class HistoryFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    Button clearHistoryButtonHF;
    RecyclerView clearHistoryRVHF;
    HistoryRVAdapter historyRVAdapter;
    LinearLayoutManager historyLM;

    ArrayList<Track> tracks = new ArrayList<>();
    User userObject = new User();
    Track track;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View historyView = inflater.inflate(R.layout.fragment_history, container, false);
        clearHistoryButtonHF = historyView.findViewById(R.id.clearHistoryButtonHF);
        clearHistoryRVHF = historyView.findViewById(R.id.clearHistoryRVHF);

        clearHistoryButtonHF.setOnClickListener(clearListener);
        setAdapter();
        getUserObject();

        return historyView;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity mActivity = (AppCompatActivity) getActivity();
        assert mActivity != null;
        Objects.requireNonNull(mActivity.getSupportActionBar()).setTitle(R.string.history_fragment);
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
                historyRVAdapter.notifyDataSetChanged();
                getHistory();
            }
        });
    }

    void getHistory() {
        db.collection("users").document(userObject.getUid())
                .collection("history")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        tracks.clear();
                        assert value != null;
                        for (QueryDocumentSnapshot document : value) {
                            track = document.toObject(Track.class);
                            tracks.add(track);
                            historyRVAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void setAdapter() {
        clearHistoryRVHF.setHasFixedSize(true);
        historyLM = new LinearLayoutManager(getActivity());
        clearHistoryRVHF.setLayoutManager(historyLM);
        historyRVAdapter = new HistoryRVAdapter(tracks);
        clearHistoryRVHF.setAdapter(historyRVAdapter);
    }

    View.OnClickListener clearListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            ArrayList<String> trackHistoryList = new ArrayList<>(userObject.getHistory().keySet());
            Log.d("demo", "onClick: " + trackHistoryList);
            for (String track_id : trackHistoryList) {
                Log.d("demo", "onClick: " + track_id);
                db.collection("users").document(userObject.getUid())
                        .collection("history").document(track_id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // nothing
                            }
                        });
                userObject.getHistory().remove(track_id);

                HashMap<String, Object> uhObject = new HashMap<>();
                uhObject.put("history", userObject.getHistory());

                db.collection("users").document(user.getUid())
                        .update(uhObject)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // nothing
                            }
                        });
            }

        }
    };
}