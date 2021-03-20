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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class SharedFragment extends Fragment implements SharedRVAdapter.OnSharedListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public SharedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    RecyclerView sharedRV;
    SharedRVAdapter sharedRVAdapter;
    LinearLayoutManager sharedLM;

    ArrayList<Shared> shares = new ArrayList<>();
    User userObject = new User();
    Shared share;
    Album album;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View sfView = inflater.inflate(R.layout.fragment_shared, container, false);
        sharedRV = sfView.findViewById(R.id.sharedRV);
        setAdapter();
        getShares();

        return sfView;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity mActivity = (AppCompatActivity) getActivity();
        assert mActivity != null;
        Objects.requireNonNull(mActivity.getSupportActionBar()).setTitle(R.string.shared_fragment);
    }

    private void setAdapter() {
        sharedRV.setHasFixedSize(true);
        sharedLM = new LinearLayoutManager(getActivity());
        sharedRV.setLayoutManager(sharedLM);
        sharedRVAdapter = new SharedRVAdapter(this, shares);
        sharedRV.setAdapter(sharedRVAdapter);
    }

    void getShares() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("users").document(user.getUid())
                .collection("shared")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        shares.clear();
                        assert value != null;
                        for (QueryDocumentSnapshot document : value) {
                            share = document.toObject(Shared.class);
                            shares.add(share);
                            sharedRVAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }


    @Override
    public void onSharedClick(int position) {
        Shared share = shares.get(position);
        album = new Album();
        album.setArtist_name(share.getArtist_name());
        album.setTitle(share.getAlbum_title());
        album.setNb_tracks(share.getNb_tracks());
        album.setCover_small(share.getCover_small());
        album.setCover_big(share.getCover_big());
        album.setAlbum_id(share.getAlbum_id());
        album.setArtist_picture(share.getArtist_picture());
        sdiListener.sdiSharedClicked(album);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SharedInterfaceListener) {
            sdiListener = (SharedInterfaceListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement SharedInterfaceListener");
        }
    }

    SharedInterfaceListener sdiListener;

    public interface SharedInterfaceListener {
        void sdiSharedClicked(Album album);
    }
}