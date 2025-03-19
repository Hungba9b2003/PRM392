////package com.example.austproject;
////
////import android.content.Context;
////import android.content.Intent;
////import android.content.SharedPreferences;
////import android.os.Bundle;
////import android.util.Log;
////import android.view.View;
////import android.widget.Button;
////
////import androidx.appcompat.app.AppCompatActivity;
////import androidx.recyclerview.widget.LinearLayoutManager;
////import androidx.recyclerview.widget.RecyclerView;
////
////import com.google.firebase.auth.FirebaseAuth;
////import com.google.firebase.firestore.FirebaseFirestore;
////
////import java.util.HashMap;
////import java.util.Map;
////
////public class PublicLibraryActivity extends AppCompatActivity {
////
////    private RecyclerView rvMusic;
////    private Button logoutButton, privateLibraryButton;
////
////    private String[] tracks = {
////            "Candlelight Red - Broken Glass",
////            "Crossfade - Cold",
////            "Evans Blue - Erase My Scars",
////            "Chad Krueger - Hero",
////            "Egypt Central - Home",
////            "Eminem - Lose Yourself",
////            "Eminem - Mockingbird",
////            "Fall Of Envy - Solace",
////            "Linkin Park - In the End",
////            "Linkin Park - Numb",
////            "Linkin Park - Somewhere I Belong",
////            "Memory Of A Melody - Reach",
////            "Moshi Moshi"
////    };
////
////    private int[] trackResources = {
////            R.raw.broken_glass,
////            R.raw.cold,
////            R.raw.erase_my_scars,
////            R.raw.hero,
////            R.raw.home,
////            R.raw.lose_yourself,
////            R.raw.mockingbird,
////            R.raw.solace,
////            R.raw.in_the_end,
////            R.raw.numb,
////            R.raw.somewhere_i_belong,
////            R.raw.reach,
////            R.raw.moshi_moshi
////    };
////
////    private void saveTracksToFirestore() {
////        FirebaseFirestore db = FirebaseFirestore.getInstance();
////
////        for (int i = 0; i < tracks.length; i++) {
////            Map<String, Object> track = new HashMap<>();
////            track.put("name", tracks[i]);
////            track.put("resource", trackResources[i]);  // Lưu resource ID
////
////            db.collection("music")
////                    .add(track)
////                    .addOnSuccessListener(documentReference ->
////                            Log.d("Firestore", "Track added with ID: " + documentReference.getId()))
////                    .addOnFailureListener(e ->
////                            Log.e("Firestore", "Error adding track", e));
////        }
////    }
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_public_library);
////
////        rvMusic = findViewById(R.id.rvMusic);
////        privateLibraryButton = findViewById(R.id.btPrivateLibrary);
////        logoutButton = findViewById(R.id.btLogout);
////
////        // Cấu hình RecyclerView
////        rvMusic.setLayoutManager(new LinearLayoutManager(this));
////        rvMusic.setAdapter(new MusicAdapter(this, tracks, trackResources));
////
////        // Xử lý nút vào thư viện riêng tư
////        privateLibraryButton.setOnClickListener(v -> {
////            Intent intent = new Intent(PublicLibraryActivity.this, PrivateLibraryActivity.class);
////            startActivity(intent);
////        });
////
////        // Xử lý nút Logout
////        logoutButton.setOnClickListener(v -> {
////            FirebaseAuth.getInstance().signOut();
////
////            SharedPreferences sharedPreferences = getSharedPreferences("LibPref", Context.MODE_PRIVATE);
////            SharedPreferences.Editor editor = sharedPreferences.edit();
////            editor.remove("user_id");
////            editor.apply();
////
////            Intent intent = new Intent(PublicLibraryActivity.this, MainActivity.class);
////            startActivity(intent);
////            finish();
////        });
////    }
////}
//package com.example.austproject;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Button;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class PublicLibraryActivity extends AppCompatActivity {
//
//    private RecyclerView rvMusic;
//    private Button logoutButton, privateLibraryButton,btAddMusic;
//    private MusicAdapter adapter;
//    private List<MusicItem> musicList = new ArrayList<>();
//    private FirebaseFirestore db;
//        private String[] tracks = {
//            "Mất kết nối - Dương Domic",
//            "Moshi Moshi -Remix",
//            "Nơi tình yêu kết thúc - Bùi Anh Tuấn",
//            "Đổi tư thế - Bình Gold",
//                "Một tình yêu hai thử thách - Remix",
//                "Ghệ đẹp - remix"
//
//
//
//    };
//
//    private int[] trackResources = {
//            R.raw.matketnoi,
//            R.raw.moshi_moshi,
//            R.raw.noitinhyeuketthuc,
//            R.raw.doituthe,
//            R.raw.mottinhyeuhaithuthach,
//            R.raw.gedep
//    };
//    private String[] singer = {
//            "Dương Domic",
//            "Remix",
//            "Bùi Anh Tuấn",
//            "Bình Gold",
//            "Remix",
//            "Remix"
//    };
//    private String[] avatar ={
//            "https://photo-resize-zmp3.zadn.vn/w600_r1x1_jpeg/cover/8/c/1/6/8c166e2b9a0e45ca9a6c7bef40a81f74.jpg",
//            "https://i1.sndcdn.com/artworks-37AqH34wk1mkTt5J-CHZkzA-t500x500.jpg",
//            "https://i.ytimg.com/vi/4S0jwsH7WYw/maxresdefault.jpg",
//            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRoa0-mxdrGOkuibqzAEtGJ_fUkx_sHeSQ66w&s",
//            "https://avatar-ex-swe.nixcdn.com/song/2023/08/16/e/0/3/5/1692160392297_640.jpg",
//            "https://avatar-ex-swe.nixcdn.com/song/2021/03/24/0/5/7/d/1616557041416_640.jpg"
//
//    };
//
//    private void saveTracksToFirestore() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        for (int i = 0; i < tracks.length; i++) {
//            Map<String, Object> track = new HashMap<>();
//            track.put("name", tracks[i]);
//            track.put("resource", trackResources[i]);  // Lưu resource ID
//            track.put("singer", singer[i]);
//            track.put("avatar", avatar[i]);
//            db.collection("music")
//                    .add(track)
//                    .addOnSuccessListener(documentReference ->
//                            Log.d("Firestore", "Track added with ID: " + documentReference.getId()))
//                    .addOnFailureListener(e ->
//                            Log.e("Firestore", "Error adding track", e));
//        }
//    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_public_library);
////        saveTracksToFirestore();
//        rvMusic = findViewById(R.id.rvMusic);
//        privateLibraryButton = findViewById(R.id.btPrivateLibrary);
//        logoutButton = findViewById(R.id.btLogout);
//
//        // Cấu hình RecyclerView
//        adapter = new MusicAdapter(this, musicList);
//        rvMusic.setLayoutManager(new LinearLayoutManager(this));
//        rvMusic.setAdapter(adapter);
//
//        db = FirebaseFirestore.getInstance();
//
//        // Lấy dữ liệu từ Firestore
//        fetchMusicFromFirestore();
//        privateLibraryButton.setOnClickListener(v -> {
//            Intent intent = new Intent(PublicLibraryActivity.this, PrivateLibraryActivity.class);
//            startActivity(intent);
//        });
//        // Xử lý nút vào thư viện riêng tư
//        privateLibraryButton.setOnClickListener(v -> {
//            Intent intent = new Intent(PublicLibraryActivity.this, PrivateLibraryActivity.class);
//            startActivity(intent);
//        });
//
//        // Xử lý nút Logout
//        logoutButton.setOnClickListener(v -> {
//            FirebaseAuth.getInstance().signOut();
//
//            SharedPreferences sharedPreferences = getSharedPreferences("LibPref", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.remove("user_id");
//            editor.apply();
//
//            Intent intent = new Intent(PublicLibraryActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        });
//
////        btAddMusic = findViewById(R.id.btAddMusic);
////        btAddMusic.setOnClickListener(v -> {
////            Intent intent = new Intent(PublicLibraryActivity.this, AddMusicActivity.class);
////            startActivity(intent);
////        });
//    }
//
//    private void fetchMusicFromFirestore() {
//        db.collection("music")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        musicList.clear();
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            String name = document.getString("name");
//                            int resource = document.getLong("resource").intValue();  // Chuyển resource ID từ Firestore
//                            String avatar = document.getString("avatar");
//                            String lyric = document.getString("lyric");
//                            musicList.add(new MusicItem(name, resource, avatar,lyric));
//                        }
//                        adapter.notifyDataSetChanged();
//                    } else {
//                        Log.e("Firestore", "Lỗi khi lấy dữ liệu", task.getException());
//                    }
//                });
//    }
//}
//
package com.example.austproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PublicLibraryActivity extends AppCompatActivity {
    private ImageButton btnAddPlaylist ;
    private RecyclerView rvRecent, rvPlaylist, rvSuggestions;
    private Button logoutButton, privateLibraryButton, btFavorites, btArtists;
    private MusicAdapter musicAdapter, recentAdapter, playlistAdapter, suggestionAdapter;
    private List<MusicItem> musicList = new ArrayList<>(), recentList = new ArrayList<>(),
            playlist = new ArrayList<>(), suggestions = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_library);

//        rvMusic = findViewById(R.id.rvMusic);
        rvRecent = findViewById(R.id.rvRecentSongs);
        rvPlaylist = findViewById(R.id.rvPlaylists);
        rvSuggestions = findViewById(R.id.rvSuggestedSongs);
        privateLibraryButton = findViewById(R.id.btPrivateLibrary);
        logoutButton = findViewById(R.id.btLogout);
        btFavorites = findViewById(R.id.btFavorites);
        btArtists = findViewById(R.id.btArtists);
        btnAddPlaylist = findViewById(R.id.btnAddPlaylist);
        // Cấu hình RecyclerView
//        rvMusic.setLayoutManager(new LinearLayoutManager(this));
//        rvMusic.setAdapter(musicAdapter = new MusicAdapter(this, musicList));

        rvRecent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRecent.setAdapter(recentAdapter = new MusicAdapter(this, recentList));

        rvPlaylist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvPlaylist.setAdapter(playlistAdapter = new MusicAdapter(this, playlist));

        rvSuggestions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvSuggestions.setAdapter(suggestionAdapter = new MusicAdapter(this, suggestions));

        db = FirebaseFirestore.getInstance();

        fetchMusicFromFirestore();
//        try {
//            fetchRecentMusic();
//        } catch (Exception e) {  // Cần truyền một biến vào catch
//            Log.e("MusicApp", "Lỗi khi lấy danh sách nhạc", e);
//        }

//        fetchPlaylist();
        fetchSuggestions();

        privateLibraryButton.setOnClickListener(v -> startActivity(new Intent(this, PrivateLibraryActivity.class)));
//        btFavorites.setOnClickListener(v -> startActivity(new Intent(this, FavoritesActivity.class)));
//        btArtists.setOnClickListener(v -> startActivity(new Intent(this, ArtistActivity.class)));

        logoutButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("LibPref", Context.MODE_PRIVATE);
            sharedPreferences.edit().remove("user_email").apply();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        btnAddPlaylist.setOnClickListener(v -> showAddPlaylistDialog());

    }

    private void fetchMusicFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("music")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<MusicItem> musicList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String avatar = document.getString("avatar");

                            // Lấy lyric dưới dạng danh sách
                            List<String> lyric = (List<String>) document.get("lyric");

                            // Đảm bảo không bị null (tránh lỗi)
                            if (lyric == null) {
                                lyric = new ArrayList<>();
                            }

                            int resource = getResourceByName(name);

                            musicList.add(new MusicItem(name, resource, avatar, lyric));
                        }

                        // KIỂM TRA: Nếu `musicAdapter` chưa được tạo -> Khởi tạo và set cho RecyclerView
                        if (musicAdapter == null) {
                            musicAdapter = new MusicAdapter(PublicLibraryActivity.this, musicList);
                            rvSuggestions.setAdapter(musicAdapter);
                        } else {
                            musicAdapter = new MusicAdapter(PublicLibraryActivity.this, musicList);
                            rvSuggestions.setAdapter(musicAdapter);
                        }
                    } else {
                        Log.e("FirestoreError", "Error getting documents: ", task.getException());
                    }
                });
    }
    private void showAddPlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Playlist");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String playlistName = input.getText().toString().trim();
            if (!playlistName.isEmpty()) {
                savePlaylistToFirebase(playlistName);
            } else {
                Toast.makeText(this, "Playlist name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void savePlaylistToFirebase(String playlistName) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = user.getUid();

        // Tạo một document trong "playLists" bên trong collection "account"
        DocumentReference userAccountRef = db.collection("account").document(userId);

        // Tạo ID ngẫu nhiên cho playlist
        String playlistId = UUID.randomUUID().toString();

        // Dữ liệu của playlist
        Map<String, Object> playlistData = new HashMap<>();
        playlistData.put("name", playlistName);
        playlistData.put("createdAt", FieldValue.serverTimestamp());

        // Lưu vào Firestore
        userAccountRef.collection("playLists").document(playlistId)
                .set(playlistData)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Playlist created successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to create playlist", Toast.LENGTH_SHORT).show());
    }




    private void fetchRecentMusic() {
        db.collection("recent_music").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                recentList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    recentList.add(getMusicItem(document));
                }
                recentAdapter.notifyDataSetChanged();
            }
        });
    }
//
//    private void fetchPlaylist() {
//        db.collection("playlists").get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                playlist.clear();
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    playlist.add(getMusicItem(document));
//                }
//                playlistAdapter.notifyDataSetChanged();
//            }
//        });
//    }


    private void fetchSuggestions() {
        db.collection("music").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                suggestions.clear();
                List<MusicItem> tempList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    tempList.add(getMusicItem(document));
                }
                Collections.shuffle(tempList);
                suggestions.addAll(tempList.subList(0, Math.min(6, tempList.size())));
                if (musicAdapter != null) {
                    musicAdapter.notifyDataSetChanged();
                } else {
                    Log.e("MusicAdapter", "Adapter is null. Make sure it is initialized before calling notifyDataSetChanged().");
                }
            }
        });
    }

    private MusicItem getMusicItem(QueryDocumentSnapshot document) {
        return new MusicItem(
                document.getString("name"),
                document.getLong("resource").intValue(),
                document.getString("avatar"),
                (List<String>) document.get("lyric") // Sửa tại đây: lấy List<String>
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
}
