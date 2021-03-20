package com.example.finalexam;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HistoryRVAdapter extends RecyclerView.Adapter<HistoryRVAdapter.HistoryViewHolder> {
    private ArrayList<Track> tracks;

    public HistoryRVAdapter(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View historyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_row_cardview, parent, false);
        return new HistoryRVAdapter.HistoryViewHolder(historyView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        if (tracks.size() > 0 ) {
            Track track = tracks.get(position);
            String imgUrl = track.getCover_small();

            Picasso.get()
                    .load(imgUrl)
                    .resize(75, 75)
                    .centerCrop()
                    .into(holder.coverSmallHF);
            holder.albumTitleHF.setText(track.getAlbum_title());
            holder.artistNameHF.setText(track.getArtist_name());
            holder.trackTitleHF.setText(track.getTrack_title());

            String pattern = "MM/dd/yyyy hh:mm";
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            String dts = "";
            Timestamp ts = Timestamp.valueOf(track.getDate());

            try {
                dts = simpleDateFormat.format(ts);
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.dateHF.setText(dts);
        }
    }

    @Override
    public int getItemCount() {
        return this.tracks.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView coverSmallHF;
        TextView albumTitleHF;
        TextView artistNameHF;
        TextView trackTitleHF;
        TextView dateHF;
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            coverSmallHF = itemView.findViewById(R.id.coverSmallHF);
            albumTitleHF = itemView.findViewById(R.id.albumTitleHF);
            artistNameHF = itemView.findViewById(R.id.artistNameHF);
            trackTitleHF = itemView.findViewById(R.id.trackTitleHF);
            dateHF = itemView.findViewById(R.id.dateHF);
        }
    }
}
