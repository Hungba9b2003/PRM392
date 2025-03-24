package com.example.austproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private Context context;
    private List<PlaylistItem> playlists; // Chứa cả id và name

    public PlaylistAdapter(Context context, List<PlaylistItem> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        PlaylistItem playlistItem = playlists.get(position);
        holder.tvPlaylistName.setText(playlistItem.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlaylistDetailActivity.class);
            intent.putExtra("PLAYLIST_NAME", playlistItem.getName());
            intent.putExtra("PLAYLIST_ID", playlistItem.getId()); // 🔥 Truyền id vào intent
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaylistName;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaylistName = itemView.findViewById(R.id.tvPlaylistName);
        }
    }
}

