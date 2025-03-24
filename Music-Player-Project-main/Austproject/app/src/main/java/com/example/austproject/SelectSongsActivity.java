package com.example.austproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelectSongsActivity extends AppCompatActivity {
    private RecyclerView rvAllSongs;
    private Button btnAddToPlaylist;
    private SongSelectionAdapter adapter;
    private List<MusicItem> allSongs = new ArrayList<>();
    private List<String> selectedSongs = new ArrayList<>();
    private FirebaseFirestore db;
    private String playlistName;
    private String playlistId;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_songs);
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
        rvAllSongs = findViewById(R.id.rvAllSongs);
        btnAddToPlaylist = findViewById(R.id.btnAddToPlaylist);

        playlistName = getIntent().getStringExtra("PLAYLIST_NAME");
        playlistId = getIntent().getStringExtra("PLAYLIST_ID");

        SharedPreferences sharedPreferences = getSharedPreferences("LibPref", Context.MODE_PRIVATE);
        userEmail = sharedPreferences.getString("user_email", null);

        db = FirebaseFirestore.getInstance();
        loadAllSongs();
        fetchPlaylistSongs(); // Lấy danh sách bài hát từ playlist

        rvAllSongs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongSelectionAdapter(allSongs, selectedSongs);
        rvAllSongs.setAdapter(adapter);

        btnAddToPlaylist.setOnClickListener(v -> addSongsToPlaylist());
    }

    private void fetchPlaylistSongs() {
        db.collection("accounts")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        List<Map<String, Object>> playLists = (List<Map<String, Object>>) documentSnapshot.get("playLists");

                        if (playLists != null) {
                            for (Map<String, Object> playlist : playLists) {
                                if (playlist.get("id").equals(playlistId)) {
                                    List<String> songs = (List<String>) playlist.get("songs");
                                    if (songs != null) {
                                        selectedSongs.addAll(songs);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged(); // Cập nhật adapter sau khi lấy dữ liệu
                });
    }

    private void loadAllSongs() {
        db.collection("music").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allSongs.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        MusicItem musicItem = new MusicItem(
                                doc.getString("name"),
                                "",
                                doc.getString("avatar"),
                                doc.get("lyric") != null ? (List<String>) doc.get("lyric") : List.of()
                        );
                        allSongs.add(musicItem);
                    }
                    adapter.notifyDataSetChanged();
                });
    }


    private void addSongsToPlaylist() {
        Log.e("Firestore", "Fetching user document with email: " + userEmail);
        if (selectedSongs.isEmpty() || userEmail == null) {
            Log.e("Firestore", "❌ selectedSongs is empty OR userEmail is null");
            return;
        }

        Log.d("Firestore", "✅ Starting addSongsToPlaylist...");

        db.collection("accounts")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d("Firestore", "✅ Firestore document fetched!");

                    if (!querySnapshot.isEmpty()) { // 🔥 Check if any document is returned
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0); // 🔥 Get the first document

                        List<Map<String, Object>> playLists = (List<Map<String, Object>>) documentSnapshot.get("playLists");

                        if (playLists != null) {
                            Log.d("Firestore", "✅ User has " + playLists.size() + " playlists");

                            boolean playlistFound = false;

                            for (Map<String, Object> playlist : playLists) {
                                Log.d("Firestore", "📌 Checking playlist: " + playlist.get("id"));

                                if (playlist.get("id").equals(playlistId)) {
                                    Log.d("Firestore", "✅ Playlist found! ID: " + playlistId);
                                    playlistFound = true;

                                    // Clear the existing songs in the playlist
                                    List<String> songs = new ArrayList<>();

                                    // Add only the selected songs
                                    int addedCount = 0;
                                    for (MusicItem song : allSongs) {
                                        if (selectedSongs.contains(song.getName())) {
                                            songs.add(song.getName());
                                            addedCount++;
                                        }
                                    }

                                    Log.d("Firestore", "🎵 Added " + addedCount + " new songs to playlist");

                                    playlist.put("songs", songs);
                                    break;
                                }
                            }

                            if (playlistFound) {
                                db.collection("accounts")
                                        .document(documentSnapshot.getId()) // 🔥 Use correct document ID to update
                                        .update("playLists", playLists)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("Firestore", "✅ Firestore updated successfully!");

                                            Intent intent = new Intent();
                                            intent.putExtra("PLAYLIST_UPDATED", true);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> Log.e("Firestore", "❌ Update failed: " + e.getMessage()));
                            } else {
                                Log.e("Firestore", "❌ Playlist with ID " + playlistId + " not found!");
                            }
                        } else {
                            Log.e("Firestore", "❌ No playlists found for user");
                        }
                    } else {
                        Log.e("Firestore", "❌ User document does not exist");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "❌ Failed to fetch user data: " + e.getMessage()));
    }





}
