package com.example.austproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class PrivateLibraryActivity extends AppCompatActivity {

    ListView privateLibrary;
    Button addLibrary, goBack;
    SQLiteDatabase myDb;
    ArrayList<String> libraryList;
    ArrayAdapter<String> adapter;
    private static final int EDIT_LIBRARY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_library);

//        privateLibrary = findViewById(R.id.lvprivateLibrary);
        addLibrary = findViewById(R.id.btaddLibrary);
        goBack = findViewById(R.id.btgoBack);

        myDb = openOrCreateDatabase("AccountsDB", Context.MODE_PRIVATE, null);
        myDb.execSQL("CREATE TABLE IF NOT EXISTS privatelib (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, user_id INTEGER);");
        myDb.execSQL("CREATE TABLE IF NOT EXISTS library_tracks (id INTEGER PRIMARY KEY AUTOINCREMENT, library_id INTEGER, track_title TEXT, track_resource INTEGER, FOREIGN KEY(library_id) REFERENCES privatelib(id));");

        SharedPreferences sharedPreferences = getSharedPreferences("LibPref", Context.MODE_PRIVATE);
        final int userId = sharedPreferences.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        libraryList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, libraryList);
        loadLibraries();
        privateLibrary.setAdapter(adapter);

        addLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PrivateLibraryActivity.this);
                builder.setTitle("Add Playlist");

                final EditText input = new EditText(PrivateLibraryActivity.this);
                input.setHint("Enter playlist name");
                builder.setView(input);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = input.getText().toString().trim();

                        if (!title.isEmpty()) {
                            SharedPreferences sharedPreferences = getSharedPreferences("LibPref", Context.MODE_PRIVATE);
                            int userId = sharedPreferences.getInt("user_id", -1);

                            if (userId != -1) {
                                myDb.execSQL("INSERT INTO privatelib (title, user_id) VALUES('" + title + "', '" + userId + "');");
                                Toast.makeText(PrivateLibraryActivity.this, "Playlist added!", Toast.LENGTH_SHORT).show();
                                loadLibraries();
                            }
                        } else {
                            Toast.makeText(PrivateLibraryActivity.this, "Playlist name cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        });

        privateLibrary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                String title = libraryList.get(index);
                int libID = getLibraryId(title);
                if (libID != -1) {
                    Intent i = new Intent(PrivateLibraryActivity.this, EditPrivateLibraryActivity.class);
                    i.putExtra("LIBRARY_ID", libID);
                    i.putExtra("LIBRARY_TITLE", title);
                    startActivityForResult(i, EDIT_LIBRARY_REQUEST_CODE);
                }
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PrivateLibraryActivity.this, PublicLibraryActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_LIBRARY_REQUEST_CODE && resultCode == RESULT_OK) {
            loadLibraries();
            adapter.notifyDataSetChanged();
        }
    }

    private void loadLibraries() {
        libraryList.clear();
        SharedPreferences sharedPreferences = getSharedPreferences("LibPref", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId != -1) {
            Cursor c = myDb.rawQuery("SELECT title FROM privatelib WHERE user_id='" + String.valueOf(userId) + "'", null);
            if (c.moveToFirst()) {
                do {
                    libraryList.add(c.getString(0));
                } while (c.moveToNext());
            }
            c.close();
        }
        adapter.notifyDataSetChanged();
    }

    private int getLibraryId(String title) {
        SharedPreferences sharedPreferences = getSharedPreferences("LibPref", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        Cursor c = myDb.rawQuery("SELECT id FROM privatelib WHERE title = '" + title + "' AND user_id = '" + String.valueOf(userId) + "'", null);
        if (c.moveToFirst()) {
            int id = c.getInt(0);
            c.close();
            return id;
        }
        c.close();
        return -1;
    }
}