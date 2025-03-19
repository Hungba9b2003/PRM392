package com.example.austproject;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PrivateTrackPlaybackActivity extends AppCompatActivity {

    TextView Title;
    Button PlayPause, Loop, goBack, Delete;
    MediaPlayer mediaPlayer;
    SQLiteDatabase myDb;
    boolean loopFlag = false;
    private ImageView Avatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_track_playback);

        Title = findViewById(R.id.tvTitle);
        PlayPause = findViewById(R.id.btPlayPause);
        Loop = findViewById(R.id.btLoop);
        Delete = findViewById(R.id.btDelete);
        goBack = findViewById(R.id.btgoBack);

        myDb = openOrCreateDatabase("AccountsDB", Context.MODE_PRIVATE, null);

        String trackTitle = getIntent().getStringExtra("TRACK_TITLE");
        int trackResource = getIntent().getIntExtra("TRACK_RESOURCE", -1);
        int libraryId = getIntent().getIntExtra("LIBRARY_ID", -1);
        String avatar = getIntent().getStringExtra("TRACK_AVATAR");

        Title.setText(trackTitle);
//        Avatar.set
        if (trackResource != -1) {
            mediaPlayer = MediaPlayer.create(this, trackResource);
            if (mediaPlayer != null) {
                mediaPlayer.start();
                PlayPause.setText("Pause");
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        PlayPause.setText("Play");
                    }
                });
            }
        }

        PlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        PlayPause.setText("Play");
                    } else {
                        mediaPlayer.start();
                        PlayPause.setText("Pause");
                    }
                }
            }
        });

        Loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    loopFlag = !loopFlag;
                    mediaPlayer.setLooping(loopFlag);
                    Loop.setText(loopFlag ? "Disable Loop" : "Enable Loop");
                    Toast.makeText(PrivateTrackPlaybackActivity.this, "Looping " + (loopFlag ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int trackResource = getIntent().getIntExtra("TRACK_RESOURCE", -1);
                if (trackResource != -1) {
                    myDb.execSQL("DELETE FROM library_tracks WHERE library_id = " + libraryId + " AND track_resource = " + trackResource);
                    Toast.makeText(PrivateTrackPlaybackActivity.this, "Track deleted from playlist", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent();
                    i.putExtra("UPDATED", true);
                    setResult(RESULT_OK, i);
                    finish();
                }
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
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}