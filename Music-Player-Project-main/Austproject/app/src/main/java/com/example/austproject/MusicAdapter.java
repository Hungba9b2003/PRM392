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
    private List<MusicItem> originalList; // Lưu trữ danh sách gốc để phục hồi khi cần tìm kiếm lại

    public MusicAdapter(Context context, List<MusicItem> musicList) {
        this.context = context;

        // Kiểm tra danh sách không bị null trước khi thao tác
        if (musicList != null && !musicList.isEmpty()) {
            // Trộn danh sách để lấy ngẫu nhiên
            Collections.shuffle(musicList);

            // Gán danh sách đầy đủ vào adapter
            this.musicList = new ArrayList<>(musicList);
            this.originalList = new ArrayList<>(musicList); // Lưu lại danh sách gốc
        } else {
            this.musicList = new ArrayList<>(); // Nếu null, tạo danh sách rỗng tránh lỗi
            this.originalList = new ArrayList<>(); // Lưu danh sách rỗng
        }
    }

    public void updateList(List<MusicItem> newList) {
        this.musicList = newList;
        notifyDataSetChanged();  // Cập nhật UI
    }

    // Phương thức để lọc danh sách khi tìm kiếm
    public void filterList(String query) {
        List<MusicItem> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(originalList); // Nếu không có query, phục hồi lại danh sách ban đầu
        } else {
            for (MusicItem item : originalList) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        musicList.clear();
        musicList.addAll(filteredList);
        notifyDataSetChanged();
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
           String resourceId = musicItem.getResource();

            if (!resourceId.equals("")) {
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

