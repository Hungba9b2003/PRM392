package com.example.austproject;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class PublicTrackPlaybackActivity extends AppCompatActivity {
    private ImageView avatar;
    private SeekBar seekBar;
    private TextView currentTime, totalTime, Title;
    private ImageButton PlayPause, Loop, Save, goBack, Previous, Next;
    private ObjectAnimator rotationAnimator;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private MediaPlayer mediaPlayer;
    private boolean loopFlag = false;
    private boolean isPlaying = false;
    private TextView tvLyric;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_track_playback);

        // Ánh xạ UI
        avatar = findViewById(R.id.ivAvatar);
        seekBar = findViewById(R.id.seekBar);
        currentTime = findViewById(R.id.tvCurrentTime);
        totalTime = findViewById(R.id.tvTotalTime);
        Title = findViewById(R.id.tvTitle);
        tvLyric = findViewById(R.id.tvLyric);
        PlayPause = findViewById(R.id.btPlayPause);
        Loop = findViewById(R.id.btLoop);
        Save = findViewById(R.id.btSave);
        goBack = findViewById(R.id.btGoBack);
        Previous = findViewById(R.id.btPrevious);
        Next = findViewById(R.id.btNext);

        // Nhận dữ liệu từ Intent
        String trackAvatar = getIntent().getStringExtra("TRACK_AVATAR");
        String trackTitle = getIntent().getStringExtra("TRACK_TITLE");
        String trackResource = getIntent().getStringExtra("TRACK_RESOURCE");
        ArrayList<String> trackLyric = getIntent().getStringArrayListExtra("TRACK_LYRIC");
        if (trackLyric != null && !trackLyric.isEmpty()) {
            StringBuilder lyricsText = new StringBuilder();
            for (String line : trackLyric) {
                lyricsText.append(line).append("\n");
            }
            tvLyric.setText(lyricsText.toString().trim());
            tvLyric.setVisibility(View.VISIBLE);
        } else {
            tvLyric.setVisibility(View.GONE);
        }
        Title.setText(trackTitle);

        // Set avatar tròn
        Glide.with(this)
                .load(trackAvatar)
                .placeholder(R.drawable.default_album_art)
                .error(R.drawable.default_album_art)
                .circleCrop()
                .into(avatar);

        // Xử lý MediaPlayer
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            if (!trackResource.equals("")) {
                mediaPlayer.setDataSource(trackResource);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> {
                    mp.start();
                    seekBar.setMax(mp.getDuration());
                    totalTime.setText(formatTime(mp.getDuration()));

                    // Khởi động nhạc và animation xoay ảnh
                    startMusic();
                });
            }
        } catch (IOException e) {
            e.printStackTrace(); // In lỗi ra log để debug
            Toast.makeText(this, "Lỗi khi tải nhạc!", Toast.LENGTH_SHORT).show();
        }


        // Sự kiện nút Play/Pause
        PlayPause.setOnClickListener(v -> togglePlayPause());

        // SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    currentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Nút Loop
        Loop.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                loopFlag = !loopFlag;
                mediaPlayer.setLooping(loopFlag);
                Loop.setImageResource(loopFlag ? R.drawable.ic_loop : R.drawable.ic_loop); // Giữ icon, có thể đổi màu nếu cần
                Toast.makeText(this, "Looping " + (loopFlag ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Save
        Save.setOnClickListener(v -> {
            Intent i = new Intent(this, SelectPlaylistActivity.class);
            i.putExtra("TRACK_TITLE", trackTitle);
            i.putExtra("TRACK_RESOURCE", trackResource);
            startActivity(i);
        });

        // Nút Go Back
        goBack.setOnClickListener(v -> finish());

        // Nút Previous (Giảm 30 giây)
        Previous.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                int newPosition = mediaPlayer.getCurrentPosition() - 30000; // Giảm 30s
                if (newPosition < 0) {
                    newPosition = 0; // Không cho lùi quá đầu bài
                }
                mediaPlayer.seekTo(newPosition);
                seekBar.setProgress(newPosition);
                currentTime.setText(formatTime(newPosition));
            }
        });

// Nút Next (Tăng 30 giây)
        Next.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                int newPosition = mediaPlayer.getCurrentPosition() + 30000; // Tăng 30s
                if (newPosition > mediaPlayer.getDuration()) {
                    newPosition = mediaPlayer.getDuration(); // Không cho vượt quá bài hát
                }
                mediaPlayer.seekTo(newPosition);
                seekBar.setProgress(newPosition);
                currentTime.setText(formatTime(newPosition));
            }
        });

    }

    private void startMusic() {
        mediaPlayer.start();
        isPlaying = true;
        PlayPause.setImageResource(R.drawable.ic_pause); // Đổi icon play → pause

        // Bắt đầu animation xoay
        rotationAnimator = ObjectAnimator.ofFloat(avatar, "rotation", 0f, 360f);
        rotationAnimator.setDuration(10000);
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.start();

        // Cập nhật SeekBar
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(updateSeekBar);

        // Khi bài hát kết thúc
        mediaPlayer.setOnCompletionListener(mp -> {
            PlayPause.setImageResource(R.drawable.ic_play);
            isPlaying = false;
        });
    }

    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer.pause();
                PlayPause.setImageResource(R.drawable.ic_play);
                isPlaying = false;
            } else {
                mediaPlayer.start();
                PlayPause.setImageResource(R.drawable.ic_pause);
                isPlaying = true;
            }
        }
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
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
