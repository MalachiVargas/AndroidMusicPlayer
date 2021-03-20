package com.example.finalexam;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class TrackRVAdapter extends RecyclerView.Adapter<TrackRVAdapter.TrackViewHolder> {

    private OnTrackListener onTrackListener;
    private ArrayList<Track> tracks;
    private User userObject;
    Track track;

    public TrackRVAdapter(OnTrackListener onTrackListener, ArrayList<Track> tracks, User userObject) {
        this.onTrackListener = onTrackListener;
        this.tracks = tracks;
        this.userObject = userObject;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View albumView = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_row_cardview, parent, false);
        return new TrackRVAdapter.TrackViewHolder(albumView, onTrackListener, tracks);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        if (tracks.size() > 0 && userObject.getLikes() != null) {
            track = tracks.get(position);
            if (track.isPlaying()) {
                holder.playButtonCV.setImageResource(R.drawable.pause_button);
            } else {
                holder.playButtonCV.setImageResource(R.drawable.play_button);
            }

            int time = Integer.parseInt(track.getTrack_duration());
            int minutes =  time / 60;
            int seconds = time % 60;
            String duration = String.format(Locale.US, "%d:%02d", minutes, seconds);

            holder.trackDurationCV.setText(duration);
            holder.trackTitleCV.setText(track.getTrack_title());
        }
    }

    @Override
    public int getItemCount() {
        return this.tracks.size();
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder {

        TextView trackTitleCV;
        TextView trackDurationCV;
        ImageButton playButtonCV;

        OnTrackListener onTrackListener;
        ArrayList<Track> tracks;

        public TrackViewHolder(@NonNull View itemView, OnTrackListener onTrackListener, ArrayList<Track> tracks) {
            super(itemView);

            this.onTrackListener = onTrackListener;
            this.tracks = tracks;

            trackTitleCV = itemView.findViewById(R.id.trackTitleCV);
            trackDurationCV = itemView.findViewById(R.id.trackDurationCV);
            playButtonCV = itemView.findViewById(R.id.playButtonCV);

            playButtonCV.setOnClickListener(playListener);
        }

        View.OnClickListener playListener = view -> {
            onTrackListener.onPlayClick(getAdapterPosition());
        };

    }

    public interface OnTrackListener {
        void onPlayClick(int position);
    }

}
