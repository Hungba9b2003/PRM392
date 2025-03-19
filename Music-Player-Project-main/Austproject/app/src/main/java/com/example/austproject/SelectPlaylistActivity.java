package com.example.austproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SelectPlaylistActivity extends AppCompatActivity {

    ListView lvLibraries;
    SQLiteDatabase myDb;
    ArrayList<String> libraryList;
    ArrayList<Integer> libraryIds;
    ArrayAdapter<String> adapter;
    int userId;
    Button Cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_playlist);

        lvLibraries = findViewById(R.id.lvLibraries);
        myDb = openOrCreateDatabase("AccountsDB", Context.MODE_PRIVATE, null);

        libraryList = new ArrayList<>();
        libraryIds = new ArrayList<>();
        String trackTitle = getIntent().getStringExtra("TRACK_TITLE");
        int trackResource = getIntent().getIntExtra("TRACK_RESOURCE", -1);

        Cancel = findViewById(R.id.btCancel);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, libraryList);
        lvLibraries.setAdapter(adapter);

        SharedPreferences sharedPreferences = getSharedPreferences("LibPref", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        if (userId != -1) {
            loadUserPlaylists(trackTitle);
        }

        lvLibraries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int library_ID = libraryIds.get(position);

                if (!isTrackInPlaylist(library_ID, trackTitle)) {
                    addTrackToLibrary(library_ID, trackTitle, trackResource);
                    Toast.makeText(SelectPlaylistActivity.this, "Track added to library", Toast.LENGTH_SHORT).show();
                    loadUserPlaylists(trackTitle);
                    finish();
                }
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadUserPlaylists(String trackTitle) {
        Cursor c = myDb.rawQuery("SELECT id, title FROM privatelib WHERE user_id = " + userId, null);

        libraryList.clear();
        libraryIds.clear();

        while (c.moveToNext()) {
            int libraryId = c.getInt(0);
            String libraryTitle = c.getString(1);
            if (!isTrackInPlaylist(libraryId, trackTitle)) {
                libraryList.add(libraryTitle);
                libraryIds.add(libraryId);
            }
        }
        c.close();
        if (libraryList.isEmpty()) {
            Toast.makeText(this, "No available playlists!", Toast.LENGTH_SHORT).show();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private boolean isTrackInPlaylist(int libraryId, String trackTitle) {
        Cursor c = myDb.rawQuery("SELECT 1 FROM library_tracks WHERE library_id = " + libraryId + " AND track_title = '" + trackTitle + "'", null);
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    private void addTrackToLibrary(int libraryId, String trackTitle, int trackResource) {
        myDb.execSQL("INSERT INTO library_tracks (library_id, track_title, track_resource) VALUES (" + libraryId + ", '" + trackTitle + "', '" + trackResource + "');");
    }
}