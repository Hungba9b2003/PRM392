package com.example.austproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EditPrivateLibraryActivity extends AppCompatActivity {

    EditText libTitle;
    ListView Tracks;
    Button SaveTitle, DeleteLibrary, goBack;
    SQLiteDatabase myDb;
    int libraryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_private_library);

        libTitle = findViewById(R.id.etlibTitle);
        Tracks = findViewById(R.id.lvTracks);
        SaveTitle = findViewById(R.id.btSaveTitle);
        DeleteLibrary = findViewById(R.id.btDeleteLib);
        goBack = findViewById(R.id.btgoBack);

        myDb = openOrCreateDatabase("AccountsDB", Context.MODE_PRIVATE, null);
        libraryId = getIntent().getIntExtra("LIBRARY_ID", -1);
        loadLibraryDetails();

        SaveTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle = libTitle.getText().toString().trim();
                if (!newTitle.isEmpty()) {
                    myDb.execSQL("UPDATE privatelib SET title='" + newTitle + "' WHERE id=" + libraryId);
                    Intent i = new Intent();
                    i.putExtra("UPDATED_TITLE", newTitle);
                    setResult(RESULT_OK, i);
                    Toast.makeText(EditPrivateLibraryActivity.this, "Title updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditPrivateLibraryActivity.this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        DeleteLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.execSQL("DELETE FROM privatelib WHERE id='" + libraryId + "'");
                Toast.makeText(EditPrivateLibraryActivity.this, "Playlist deleted!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(EditPrivateLibraryActivity.this, PrivateLibraryActivity.class);
                startActivity(i);
                finish();
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            boolean updated = data.getBooleanExtra("UPDATED", false);
            if (updated) {
                loadLibraryDetails();
            }
        }
    }

    private void loadLibraryDetails() {
        Cursor c = myDb.rawQuery("SELECT title FROM privatelib WHERE id=" + libraryId, null);
        if (c.moveToFirst()) {
            libTitle.setText(c.getString(0));
        }
        c.close();

        c = myDb.rawQuery("SELECT track_title, track_resource FROM library_tracks WHERE library_id=" + libraryId, null);
        ArrayList<String> TrackList = new ArrayList<>();
        ArrayList<Integer> TrackResourceList = new ArrayList<>();
        while (c.moveToNext()) {
            TrackList.add(c.getString(0));
            TrackResourceList.add(c.getInt(1));
        }
        c.close();

        ArrayAdapter<String> trackAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, TrackList);
        Tracks.setAdapter(trackAdapter);
        Tracks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTrack = TrackList.get(position);
                int selectedTrackResource = TrackResourceList.get(position);

                Intent i = new Intent(EditPrivateLibraryActivity.this, PrivateTrackPlaybackActivity.class);
                i.putExtra("TRACK_TITLE", selectedTrack);
                i.putExtra("TRACK_RESOURCE", selectedTrackResource);
                i.putExtra("LIBRARY_ID", libraryId);
                startActivityForResult(i, 1);
            }
        });
    }
}