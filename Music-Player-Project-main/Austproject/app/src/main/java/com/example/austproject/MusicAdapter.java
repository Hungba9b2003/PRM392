package com.example.austproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private Context context;
    private List<MusicItem> musicList;

    public MusicAdapter(Context context, List<MusicItem> musicList) {
        this.context = context;

        // Trộn danh sách để lấy ngẫu nhiên
        Collections.shuffle(musicList);

        // Chỉ lấy tối đa 5 bài hát
        this.musicList = new ArrayList<>(musicList.subList(0, Math.min(6, musicList.size())));
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        MusicItem musicItem = musicList.get(position);
        holder.tvTrackName.setText(musicItem.getName());

        // Load ảnh với Glide
        Glide.with(context)
                .load(musicItem.getAvatar())
                .placeholder(R.drawable.ic_music_placeholder)
                .into(holder.ivTrackImage);

        holder.itemView.setOnClickListener(v -> {
            int resourceId = musicItem.getResource();

            if (resourceId != 0) {
                Intent intent = new Intent(context, PublicTrackPlaybackActivity.class);
                intent.putExtra("TRACK_TITLE", musicItem.getName());
                intent.putExtra("TRACK_RESOURCE", resourceId);
                intent.putExtra("TRACK_AVATAR", musicItem.getAvatar());

                // Chuyển lyric thành ArrayList trước khi gửi
                intent.putStringArrayListExtra("TRACK_LYRIC", new ArrayList<>(musicItem.getLyric()));

                context.startActivity(intent);
            } else {
                Log.e("DEBUG", "Invalid track resource for: " + musicItem.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView tvTrackName;
        ImageView ivTrackImage;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTrackName = itemView.findViewById(R.id.tvTrackName);
            ivTrackImage = itemView.findViewById(R.id.ivTrackImage);
        }
    }
}
