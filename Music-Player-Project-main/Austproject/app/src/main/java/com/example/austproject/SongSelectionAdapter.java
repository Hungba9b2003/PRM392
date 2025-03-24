package com.example.austproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class SongSelectionAdapter extends RecyclerView.Adapter<SongSelectionAdapter.SongViewHolder> {
    private List<MusicItem> songs;
    private List<String> selectedSongs;

    public SongSelectionAdapter(List<MusicItem> songs, List<String> selectedSongs) {
        this.songs = songs;
        this.selectedSongs = selectedSongs;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_selection, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        MusicItem song = songs.get(position);
        holder.tvSongName.setText(song.getName());

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(song.getAvatar())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .into(holder.ivSongImage);

        // Check the checkbox state based on the selectedSongs list
        holder.checkBox.setOnCheckedChangeListener(null); // Remove listener to avoid duplication on scroll
        holder.checkBox.setChecked(selectedSongs.contains(song.getName()));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedSongs.contains(song.getName())) {
                    selectedSongs.add(song.getName());  // Add to selected songs if not already present
                }
            } else {
                selectedSongs.remove(song.getName());  // Remove from selected songs if unchecked
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView tvSongName;
        ImageView ivSongImage;
        CheckBox checkBox;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            ivSongImage = itemView.findViewById(R.id.ivSongImage);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}

