package com.example.austproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlaylistDetailActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_SONGS = 1001;
    private RecyclerView rvSongs;
    private MusicAdapter musicAdapter,songAdapter;
    private List<MusicItem> musicList = new ArrayList<>();
    private FirebaseFirestore db;
    private String playlistName;
    private String userId = "USER_ID_HERE"; // 🔥 Cập nhật userId thật
    private String playlistId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);
        SharedPreferences sharedPreferences = getSharedPreferences("LibPref", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email", null);
        rvSongs = findViewById(R.id.rvSongs);
        TextView tvPlaylistName = findViewById(R.id.tvPlaylistName);
        Button btnSelectSongs = findViewById(R.id.btnSelectSongs);
        db = FirebaseFirestore.getInstance();
        playlistName = getIntent().getStringExtra("PLAYLIST_NAME");
        playlistId = getIntent().getStringExtra("PLAYLIST_ID");
        tvPlaylistName.setText(playlistName);
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        Log.d("PlaylistDetailActivity", "MusicList before adapter: " + musicList.toString());
        rvSongs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvSongs.setAdapter(musicAdapter);
        fetchSongsFromPlaylist();



        btnSelectSongs.setOnClickListener(v -> {
            Intent intent = new Intent(PlaylistDetailActivity.this, SelectSongsActivity.class);
            intent.putExtra("PLAYLIST_NAME", playlistName);
            intent.putExtra("PLAYLIST_ID", playlistId);
            startActivityForResult(intent, REQUEST_CODE_SELECT_SONGS);
        });

    }

    private void fetchSongsFromPlaylist() {
        TextView tvNoSongs = findViewById(R.id.tvNoSongs);
        SharedPreferences sharedPreferences = getSharedPreferences("LibPref", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email", null);

        Log.d("Firestore", "📌 User email: " + userEmail);

        db.collection("accounts").whereEqualTo("email", userEmail).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        List<Map<String, Object>> playLists = (List<Map<String, Object>>) documentSnapshot.get("playLists");

                        if (playLists != null) {
                            boolean playlistFound = false;

                            for (Map<String, Object> playlist : playLists) {
                                if (playlist.get("id").equals(playlistId)) {
                                    playlistFound = true;
                                    List<String> musicNames = (List<String>) playlist.get("songs");

                                    if (musicNames == null || musicNames.isEmpty()) {
                                        Log.d("Firestore", "🎵 Playlist không có bài hát nào.");
                                        tvNoSongs.setVisibility(View.VISIBLE);
                                        rvSongs.setVisibility(View.GONE);
                                    } else {
                                        Log.d("Firestore", "🎵 Số lượng bài hát trong playlist: " + musicNames.size());
                                        tvNoSongs.setVisibility(View.GONE);
                                        rvSongs.setVisibility(View.VISIBLE);

                                        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                                        List<MusicItem> tempMusicList = new ArrayList<>();

                                        for (String songName : musicNames) {
                                            Task<DocumentSnapshot> task = db.collection("music")
                                                    .whereEqualTo("name", songName)
                                                    .get()
                                                    .continueWith(task1 -> {
                                                        if (task1.isSuccessful() && !task1.getResult().isEmpty()) {
                                                            DocumentSnapshot musicDoc = task1.getResult().getDocuments().get(0);
                                                            String name = musicDoc.getString("name");
                                                            String avatar = musicDoc.getString("avatar");
                                                            List<String> lyric = (List<String>) musicDoc.get("lyric");
                                                            if (lyric == null) {
                                                                lyric = new ArrayList<>();
                                                            }
                                                            String resource = musicDoc.getString("resource");
                                                           tempMusicList.add(new MusicItem(name, resource, avatar, lyric));

                                                        }
                                                        return null;
                                                    });
                                            tasks.add(task);
                                        }

                                        Tasks.whenAll(tasks).addOnCompleteListener(task -> {
                                            musicList.clear();
                                            musicList.addAll(tempMusicList);
                                            musicAdapter = new MusicAdapter(PlaylistDetailActivity.this, musicList);
                                            rvSongs.setAdapter(musicAdapter);
                                            musicAdapter.notifyDataSetChanged();
                                            Log.d("Firestore", "🎶 Số bài hát trong danh sách sau khi fetch: " + musicList.toString());
                                            Log.d("PlaylistDetailActivity", "MusicList before adapter: " + musicList.toString()); // Di chuyển log vào đây
                                        });
                                    }
                                    break;
                                }
                            }

                            if (!playlistFound) {
                                Log.e("Firestore", "❌ Không tìm thấy playlist với ID: " + playlistId);
                            }
                        } else {
                            Log.e("Firestore", "❌ User không có playlist nào.");
                        }
                    } else {
                        Log.e("Firestore", "❌ Không tìm thấy tài khoản của user.");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "❌ Lỗi lấy dữ liệu user: " + e.getMessage()));
    }

    private MusicItem getMusicItem(DocumentSnapshot document) {
        return new MusicItem(
                document.getString("name"),
                document.getString("resource"),
                document.getString("image"),
                document.contains("lyric") ? (List<String>) document.get("lyric") : new ArrayList<>()

        );
    }
    private int getResourceByName(String trackName) {
        switch (trackName) {
            case "Mất kết nối - Dương Domic":
                return R.raw.matketnoi;
            case "Moshi Moshi -Remix":
                return R.raw.moshi_moshi;
            case "Nơi tình yêu kết thúc - Bùi Anh Tuấn":
                return R.raw.noitinhyeuketthuc;
            case "Đổi tư thế - Bình Gold":
                return R.raw.doituthe;
            case "Một tình yêu hai thử thách - Remix":
                return R.raw.mottinhyeuhaithuthach;
            case "Ghệ đẹp - remix":
                return R.raw.gedep;
            default:
                return 0; // Nếu không tìm thấy
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_SONGS && resultCode == RESULT_OK) {
            fetchSongsFromPlaylist();
        }
    }
}
