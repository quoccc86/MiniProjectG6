package com.example.mini_project_prm392_g6;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private TextView tvBalanceBefore, tvBalancePre, tvBalanceAfter, tvBetInfo, tvWinner;
    private Button btnBack;

    // Âm thanh
    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;
    private int soundClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvBalanceBefore = findViewById(R.id.tvBalanceBefore);
        tvBalancePre = findViewById(R.id.tvBalancePre);
        tvBalanceAfter = findViewById(R.id.tvBalanceAfter);
        tvBetInfo = findViewById(R.id.tvBetInfo);
        tvWinner = findViewById(R.id.tvWinner);
        btnBack = findViewById(R.id.btnBack);

        // Nhạc nền cho màn kết quả
        mediaPlayer = MediaPlayer.create(this, R.raw.game_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Hiệu ứng nút
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder().setMaxStreams(5).setAudioAttributes(audioAttributes).build();
        soundClick = soundPool.load(this, R.raw.button_click, 1);

        // Nhận dữ liệu từ GameActivity
        Intent intent = getIntent();
        int balanceBefore = intent.getIntExtra("balanceBefore", 0);
        int balancePre = intent.getIntExtra("balancePre", 0);
        int balanceAfter = intent.getIntExtra("balanceAfter", 0);
        int bet1 = intent.getIntExtra("bet1", 0);
        int bet2 = intent.getIntExtra("bet2", 0);
        int bet3 = intent.getIntExtra("bet3", 0);
        int winner = intent.getIntExtra("winner", -1);

        tvBalanceBefore.setText("Số dư trước khi cược: " + balanceBefore + "$");
        tvBalancePre.setText("Số dư còn lại khi đặt cược: " + balancePre + "$");
        tvBalanceAfter.setText("Số dư sau khi cược: " + balanceAfter + "$");
        tvBetInfo.setText("Số và tiền cược: S1: " + bet1 + "$ | S2: " + bet2 + "$ | S3: " + bet3 + "$");
        tvWinner.setText("Người thắng là số: " + winner);

        btnBack.setOnClickListener(v -> {
            soundPool.play(soundClick, 1, 1, 0, 0, 1);
            finish();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
