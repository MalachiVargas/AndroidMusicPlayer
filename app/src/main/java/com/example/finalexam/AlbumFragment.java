package com.example.finalexam;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class AlbumFragment extends Fragment implements TrackRVAdapter.OnTrackListener {

    private static final String ARG_ALBUM = "Album";
    private final OkHttpClient client = new OkHttpClient();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Album album;

    public AlbumFragment() {
        // Required empty public constructor
    }

    public static AlbumFragment newInstance(Album album) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ALBUM, album);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            album = (Album) getArguments().getSerializable(ARG_ALBUM);
        }
    }

    TextView albumTitleAF;
    TextView artistNameAF;
    TextView nbTracksAF;
    ImageButton shareButtonAF;
    ImageView bigCoverAF;
    ImageView smallCoverAF;

    RecyclerView trackListRVAF;
    TrackRVAdapter trackRVAdapter;
    LinearLayoutManager trackLM;

    ArrayList<Track> tracks = new ArrayList<>();
    User userObject = new User();
    Track track;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppCompatActivity mActivity = (AppCompatActivity) getActivity();
        assert mActivity != null;
        Objects.requireNonNull(mActivity.getSupportActionBar()).setTitle(R.string.album_fragment);
        // Inflate the layout for this fragment
        View albumView = inflater.inflate(R.layout.fragment_album, container, false);
        albumTitleAF = albumView.findViewById(R.id.albumTitleAF);
        artistNameAF = albumView.findViewById(R.id.artistNameAF);
        nbTracksAF = albumView.findViewById(R.id.nbTracksAF);
        shareButtonAF = albumView.findViewById(R.id.shareButtonAF);
        bigCoverAF = albumView.findViewById(R.id.bigCoverAF);
        smallCoverAF = albumView.findViewById(R.id.smallCoverAF);

        trackListRVAF = albumView.findViewById(R.id.trackListRVAF);

        albumTitleAF.setText(album.getTitle());
        artistNameAF.setText(album.getArtist_name());
        String nb_tracks = Integer.parseInt(album.getNb_tracks()) == 1 ? album.getNb_tracks() + " track" : album.getNb_tracks() + " tracks";
        nbTracksAF.setText(nb_tracks);

        getUserObject();
        setAdapter();
        getTracks();

        String bigURl = album.getCover_big();
        String smallUrl = album.getArtist_picture();

        Picasso.get()
                .load(bigURl)
                .resize(120, 120)
                .centerCrop()
                .into(bigCoverAF);

        Picasso.get()
                .load(smallUrl)
                .resize(75, 75)
                .centerCrop()
                .into(smallCoverAF);

        shareButtonAF.setOnClickListener(shareListener);

        return albumView;
    }

    View.OnClickListener shareListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            aiListener.aShareClicked();
        }
    };

    private void getTracks() {

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse("https://api.deezer.com/album")).newBuilder()
                .addPathSegment(album.getAlbum_id())
                .addPathSegment("tracks")
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
                    tracks.clear();
                    for (int i = 0; i < albumList.length(); i++) {
                        track = new Track();

                        JSONObject jsonObject = albumList.getJSONObject(i);

                        track.setTrack_id(jsonObject.getString("id"));
                        track.setTrack_title(jsonObject.getString("title"));
                        track.setTrack_duration(jsonObject.getString("duration"));
                        track.setPreview(jsonObject.getString("preview"));

                        track.setAlbum_title(album.getTitle());
                        track.setArtist_name(album.getArtist_name());
                        track.setCover_small(album.getCover_small());

                        tracks.add(track);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    trackRVAdapter.notifyDataSetChanged();
                });

            }
        });
    }

    private void setAdapter() {
        trackListRVAF.setHasFixedSize(true);
        trackLM = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        trackListRVAF.setLayoutManager(trackLM);
        trackRVAdapter = new TrackRVAdapter(this, tracks, userObject);
        trackListRVAF.setAdapter(trackRVAdapter);
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
            }
        });
    }

    void addHistory(Track track) {
        HashMap<String, Object> historyObject = new HashMap<>();
        historyObject.put("track_id", track.getTrack_id());
        historyObject.put("track_title", track.getTrack_title());
        historyObject.put("album_title", track.getAlbum_title());
        historyObject.put("artist_name", track.getArtist_name());
        historyObject.put("cover_small", track.getCover_small());
        historyObject.put("date", track.getDate());


        db.collection("users").document(userObject.getUid())
                .collection("history").document(track.getTrack_id())
                .set(historyObject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // nothing
                    }
                });
    }

    MediaPlayer mediaPlayer;
    Track prevTrack;
    @Override
    public void onPlayClick(int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Track track = tracks.get(position);

        if (!userObject.getHistory().containsKey(track.getTrack_id())) {
            userObject.getHistory().put(track.getTrack_id(), true);
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            String dts = ts.toString();
            track.setDate(dts);
            addHistory(track);
        }

        HashMap<String, Object> uhObject = new HashMap<>();
        uhObject.put("history", userObject.getHistory());

        db.collection("users").document(user.getUid())
                .update(uhObject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        trackRVAdapter.notifyDataSetChanged();
                    }
                });


        String url = track.getPreview();
        Uri uri = Uri.parse(url);

        if (prevTrack != null && !track.getTrack_id().equals(prevTrack.getTrack_id())
                && mediaPlayer != null) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    prevTrack.setPlaying(!prevTrack.isPlaying());
                }
            });

            mediaPlayer = MediaPlayer.create(getContext(), uri);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.start();
                    track.setPlaying(!track.isPlaying());
                    trackRVAdapter.notifyDataSetChanged();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.release();
                            track.setPlaying(!track.isPlaying());
                            trackRVAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });

        } else if (prevTrack != null && prevTrack.getTrack_id().equals(track.getTrack_id()) && track.isPlaying() && mediaPlayer != null) {
            mediaPlayer.pause();
            track.setPlaying(!track.isPlaying());
        } else if (prevTrack != null && prevTrack.getTrack_id().equals(track.getTrack_id()) && !track.isPlaying() && mediaPlayer != null) {
            if (!(!mediaPlayer.isPlaying() && mediaPlayer.getCurrentPosition() > 1)) {
                mediaPlayer = MediaPlayer.create(getContext(), uri);
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.start();
                    track.setPlaying(!track.isPlaying());
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            track.setPlaying(!track.isPlaying());
                            trackRVAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        } else {
            mediaPlayer = MediaPlayer.create(getContext(), uri);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.start();
                    track.setPlaying(!track.isPlaying());
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.release();
                            track.setPlaying(!track.isPlaying());
                            trackRVAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });

        }

        prevTrack = track;

    }

    AlbumInterfaceListener aiListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AlbumInterfaceListener) {
            aiListener = (AlbumInterfaceListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement AlbumInterfaceListener");
        }
    }

    public interface AlbumInterfaceListener {
        void aShareClicked();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            });
        }
    }
}