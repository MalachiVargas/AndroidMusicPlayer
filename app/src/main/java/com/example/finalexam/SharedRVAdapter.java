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

public class SharedRVAdapter extends RecyclerView.Adapter<SharedRVAdapter.SharedViewHolder> {

    private OnSharedListener onSharedListener;
    private ArrayList<Shared> shares;

    public SharedRVAdapter(OnSharedListener onSharedListener, ArrayList<Shared> shares) {
        this.onSharedListener = onSharedListener;
        this.shares = shares;
    }

    @NonNull
    @Override
    public SharedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View sharedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_row_cardview, parent, false);
        return new SharedRVAdapter.SharedViewHolder(sharedView, onSharedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SharedViewHolder holder, int position) {
        if (shares.size() > 0) {
            Shared share = shares.get(position);
            holder.artistNameSF.setText(share.getArtist_name());
            holder.albumTitleSF.setText(share.getAlbum_title());
            holder.sharedBySF.setText(share.getSharedBy());
            String nb_tracks = Integer.parseInt(share.getNb_tracks()) == 1 ? share.getNb_tracks() + " track" : share.getNb_tracks() + " tracks";
            holder.nbTracksSF.setText(nb_tracks);

            String pattern = "MM/dd/yyyy hh:mm";
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            String dts = "";
            Timestamp ts = Timestamp.valueOf(share.getDate());

            try {
                dts = simpleDateFormat.format(ts);
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.dateSF.setText(dts);

            String imgUrl = share.getCover_small();

            Picasso.get()
                    .load(imgUrl)
                    .resize(75, 75)
                    .centerCrop()
                    .into(holder.coverSmallIVSF);
        }
    }

    @Override
    public int getItemCount() {
        return this.shares.size();
    }

    public static class SharedViewHolder extends RecyclerView.ViewHolder {
        ImageView coverSmallIVSF;
        TextView artistNameSF;
        TextView albumTitleSF;
        TextView sharedBySF;
        TextView dateSF;
        TextView nbTracksSF;

        OnSharedListener onSharedListener;


        public SharedViewHolder(@NonNull View itemView, OnSharedListener onSharedListener) {
            super(itemView);

            this.onSharedListener = onSharedListener;
            coverSmallIVSF = itemView.findViewById(R.id.coverSmallIVSF);
            artistNameSF = itemView.findViewById(R.id.artistNameSF);
            albumTitleSF = itemView.findViewById(R.id.albumTitleSF);
            sharedBySF = itemView.findViewById(R.id.sharedBySF);
            dateSF = itemView.findViewById(R.id.dateSF);
            nbTracksSF = itemView.findViewById(R.id.nbTracksSF);
            itemView.setOnClickListener(sharedListener);

        }

        View.OnClickListener sharedListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSharedListener.onSharedClick(getAdapterPosition());
            }
        };
    }

    public interface OnSharedListener {
        void onSharedClick(int position);
    }

}
