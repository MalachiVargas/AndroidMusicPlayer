package com.example.finalexam;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchRVAdapter extends RecyclerView.Adapter<SearchRVAdapter.SearchViewHolder> {

    private OnAlbumListener onAlbumListener;
    private ArrayList<Album> albums;
    private User userObject;

    public SearchRVAdapter(OnAlbumListener onAlbumListener, ArrayList<Album> albums, User userObject) {
        this.onAlbumListener = onAlbumListener;
        this.albums = albums;
        this.userObject = userObject;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View albumView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_row_cardview, parent, false);
        return new SearchViewHolder(albumView, onAlbumListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        if (albums.size() > 0 && userObject.getLikes() != null) {
            Album album = albums.get(position);
            String title;
            if (album.getTitle().length() > 20) {
                title = album.getTitle().substring(0, 20) + ", ...";
            } else {
                title =  album.getTitle();
            }

            if (userObject.getLikes().isEmpty() || !userObject.getLikes().containsKey(album.getAlbum_id())) {
                holder.likeButton.setImageResource(R.drawable.like_not_favorite);
            } else {
                holder.likeButton.setImageResource(R.drawable.like_favorite);
            }

            holder.albumTitle.setText(title);
            holder.artistName.setText(album.getArtist_name());
            String nb_tracks = Integer.parseInt(album.getNb_tracks()) == 1 ? album.getNb_tracks() + " track" : album.getNb_tracks() + " tracks";
            holder.nbTracks.setText(nb_tracks);


            String imgUrl = album.getCover_small();

            Picasso.get()
                    .load(imgUrl)
                    .resize(75, 75)
                    .centerCrop()
                    .into(holder.coverSmallIV);
        }
    }


    @Override
    public int getItemCount() {
        return this.albums.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {

        TextView albumTitle;
        TextView artistName;
        TextView nbTracks;
        ImageButton likeButton;
        ImageView coverSmallIV;

        OnAlbumListener onAlbumListener;

        public SearchViewHolder(@NonNull View itemView, OnAlbumListener onAlbumListener) {
            super(itemView);

            this.onAlbumListener = onAlbumListener;

            albumTitle = itemView.findViewById(R.id.albumTitle);
            artistName = itemView.findViewById(R.id.artistName);
            nbTracks = itemView.findViewById(R.id.nbTracks);
            coverSmallIV = itemView.findViewById(R.id.coverSmallIV);

            likeButton = itemView.findViewById(R.id.likeButton);
            likeButton.setOnClickListener(likeListener);

            itemView.setOnClickListener(albumListener);
        }

        View.OnClickListener likeListener = view -> {
            onAlbumListener.onLikeClick(getAdapterPosition());
        };

        View.OnClickListener albumListener = view -> onAlbumListener.onAlbumClick(getAdapterPosition());
    }

    public interface OnAlbumListener {
        void onLikeClick(int position);
        void onAlbumClick(int position);
    }

}
