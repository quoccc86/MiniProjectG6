package com.example.mini_project_prm392_g6;

import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNewUser, etNewPass;
    private Button btnSubmitRegister;
    private SharedPreferences sharedPreferences;

    // Âm thanh
    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;
    private int soundClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        etNewUser = findViewById(R.id.etNewUser);
        etNewPass = findViewById(R.id.etNewPass);
        btnSubmitRegister = findViewById(R.id.btnSubmitRegister);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Nhạc nền (dùng chung với màn login)
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

        btnSubmitRegister.setOnClickListener(v -> {
            soundPool.play(soundClick, 1, 1, 0, 0, 1);

            String user = etNewUser.getText().toString().trim();
            String pass = etNewPass.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.length() < 4) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 4 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", user);
            editor.putString("password", pass);
            editor.apply();

            Toast.makeText(this, "Đăng ký thành công! Quay lại màn hình đăng nhập.", Toast.LENGTH_SHORT).show();
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
