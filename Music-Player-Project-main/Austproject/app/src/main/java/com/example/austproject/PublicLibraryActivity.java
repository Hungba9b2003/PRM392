
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
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PublicLibraryActivity extends AppCompatActivity {
    private ImageButton btnAddPlaylist ;
    private RecyclerView rvRecent, rvPlaylist, rvSuggestions;
    private Button logoutButton, btFavorites, btArtists;
    private MusicAdapter musicAdapter, recentAdapter, suggestionAdapter;
    private List<MusicItem> musicList = new ArrayList<>(), recentList = new ArrayList<>(),
            suggestions = new ArrayList<>();
    private List<PlaylistItem> playlist = new ArrayList<>();
    private SearchView searchView;
    private PlaylistAdapter playlistAdapter;
    private FirebaseFirestore db;
    private List<MusicItem> filteredMusicList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_public_library);
        rvPlaylist = findViewById(R.id.rvPlaylists);
        rvSuggestions = findViewById(R.id.rvSuggestedSongs);
        logoutButton = findViewById(R.id.btLogout);
        btFavorites = findViewById(R.id.btFavorites);
        btArtists = findViewById(R.id.btArtists);
        btnAddPlaylist = findViewById(R.id.btnAddPlaylist);
//        searchView = findViewById(R.id.searchView);
//        // Cấu hình RecyclerView

        rvPlaylist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvPlaylist.setAdapter(playlistAdapter = new PlaylistAdapter(this, playlist));

        rvSuggestions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvSuggestions.setAdapter(suggestionAdapter = new MusicAdapter(this, musicList));

        db = FirebaseFirestore.getInstance();

        fetchMusicFromFirestore();

        fetchPlaylists();
        fetchSuggestions();

        logoutButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("LibPref", Context.MODE_PRIVATE);
            sharedPreferences.edit().remove("user_email").apply();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        btnAddPlaylist.setOnClickListener(v -> showAddPlaylistDialog());
//        searchView = findViewById(R.id.searchView);
//        if (searchView != null) {
//            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    return false;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    // handle query text change
//                    return false;
//                }
//            });
//        } else {
//            Log.e("SearchView", "SearchView is null, check layout XML.");
//        }

    }
    private void filterMusicList(String query) {
        filteredMusicList.clear();
        if (query.isEmpty()) {
            // Nếu không có từ khóa tìm kiếm, hiển thị tất cả
            filteredMusicList.addAll(musicList);
        } else {
            // Nếu có từ khóa tìm kiếm, lọc danh sách nhạc
            for (MusicItem item : musicList) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredMusicList.add(item);
                }
            }
        }
        musicAdapter.notifyDataSetChanged();  // Cập nhật adapter sau khi lọc
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

                            String resource = document.getString("resource");
//                            if (resource == 0) {
//                                if (document.contains("resource")) {
//                                    Long resLong = document.getLong("resource"); // Lấy giá trị Long từ Firestore
//                                    if (resLong != null && resLong <= Integer.MAX_VALUE && resLong >= Integer.MIN_VALUE) {
//                                        resource = resLong.intValue(); // Chuyển từ Long sang int an toàn
//                                    } else {
//                                        resource = -1; // Giá trị mặc định nếu quá lớn hoặc null
//                                    }
//                                } else {
//                                    resource = -1; // Giá trị mặc định nếu không có trường "resource"
//                                }
//                            }
                            musicList.add(new MusicItem(name, resource, avatar, lyric));
                        }
                        Log.d("Firestore", "🎶 Số bài hát trong danh sách sau khi fetch: " + musicList.toString() );


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
        SharedPreferences sharedPreferences = getSharedPreferences("LibPref", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email", null);

        if (userEmail == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 🔥 Tìm accountId dựa vào email
        db.collection("accounts")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String accountId = document.getId(); // Lấy accountId từ Firestore
                            DocumentReference userRef = db.collection("accounts").document(accountId);

                            // 🔥 Bước 1: Lấy danh sách playLists hiện tại
                            userRef.get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        List<Map<String, Object>> playLists = (List<Map<String, Object>>) documentSnapshot.get("playLists");

                                        if (playLists == null) {
                                            playLists = new ArrayList<>(); // Nếu null thì tạo mới
                                        }

                                        // 🔥 Bước 2: Thêm playlist mới vào danh sách
                                        Map<String, Object> newPlaylist = new HashMap<>();
                                        newPlaylist.put("id", UUID.randomUUID().toString()); // ID ngẫu nhiên
                                        newPlaylist.put("name", playlistName);
                                        newPlaylist.put("createdAt", new Date()); // 🔥 Thay thế FieldValue.serverTimestamp()

                                        playLists.add(newPlaylist);

                                        // 🔥 Bước 3: Cập nhật danh sách playLists lên Firestore
                                        userRef.update("playLists", playLists)
                                                .addOnSuccessListener(aVoid ->
                                                        Toast.makeText(this, "Playlist added successfully!", Toast.LENGTH_SHORT).show()
                                                )
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(this, "Failed to add playlist: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                                );
                                    });

                            return; // Thoát vòng lặp sau khi xử lý
                        }
                    } else {
                        Toast.makeText(this, "User account not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error retrieving account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
private void fetchPlaylists() {
    SharedPreferences sharedPreferences = getSharedPreferences("LibPref", Context.MODE_PRIVATE);
    String userEmail = sharedPreferences.getString("user_email", null);

    if (userEmail == null) {
        Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
        return;
    }

    db.collection("accounts")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                if (!querySnapshot.isEmpty()) {
                    DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                    List<Map<String, Object>> playLists = (List<Map<String, Object>>) userDoc.get("playLists");

                    playlist.clear();
                    if (playLists != null) {
                        for (Map<String, Object> playlistMap : playLists) {
                            String playlistName = (String) playlistMap.get("name");
                            String playlistId = (String) playlistMap.get("id");

                            if (playlistName != null && playlistId != null) {
                                playlist.add(new PlaylistItem(playlistId, playlistName));
                            }
                        }
                    }

                    playlistAdapter.notifyDataSetChanged();
                    Log.d("Playlist", "Số lượng playlist: " + playlist.size());
                } else {
                    Toast.makeText(PublicLibraryActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(PublicLibraryActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Playlist", "Error fetching playlists", e);
            });
}

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

    private MusicItem getMusicItem(DocumentSnapshot document) { // Chấp nhận cả DocumentSnapshot
        return new MusicItem(
                document.getString("name"),
                document.getString("resource"), // Kiểm tra null
                document.getString("avatar"),
                document.contains("lyric") ? (List<String>) document.get("lyric") : new ArrayList<>() // Kiểm tra null
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
