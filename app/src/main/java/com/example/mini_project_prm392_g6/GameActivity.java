package com.example.mini_project_prm392_g6;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private TextView tvBalance;
    private CheckBox cbCar1, cbCar2, cbCar3;
    private EditText etBetCar1, etBetCar2, etBetCar3;
    private ImageView imgRunner1, imgRunner2, imgRunner3;
    private Button btnStart, btnResetRound, btnResetAll;

    private int balance = 100;
    private boolean raceInProgress = false;
    private Handler handler = new Handler();
    private Runnable raceRunnable;

    // Âm thanh
    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;
    private int soundClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        tvBalance = findViewById(R.id.tvBalance);

        cbCar1 = findViewById(R.id.cbCar1);
        cbCar2 = findViewById(R.id.cbCar2);
        cbCar3 = findViewById(R.id.cbCar3);

        etBetCar1 = findViewById(R.id.etBetCar1);
        etBetCar2 = findViewById(R.id.etBetCar2);
        etBetCar3 = findViewById(R.id.etBetCar3);

        imgRunner1 = findViewById(R.id.imgRunner1);
        imgRunner2 = findViewById(R.id.imgRunner2);
        imgRunner3 = findViewById(R.id.imgRunner3);

        btnStart = findViewById(R.id.btnStart);
        btnResetRound = findViewById(R.id.btnResetRound);
        btnResetAll = findViewById(R.id.btnResetAll);

        tvBalance.setText("Số dư: " + balance + "$");

        // Khởi tạo nhạc nền
        mediaPlayer = MediaPlayer.create(this, R.raw.game_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Khởi tạo SoundPool cho hiệu ứng nút
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();

        soundClick = soundPool.load(this, R.raw.button_click, 1);

        btnStart.setOnClickListener(v -> {
            soundPool.play(soundClick, 1, 1, 0, 0, 1);
            if (!raceInProgress) startRace();
        });

        btnResetRound.setOnClickListener(v -> {
            soundPool.play(soundClick, 1, 1, 0, 0, 1);
            resetRound();
        });

        btnResetAll.setOnClickListener(v -> {
            soundPool.play(soundClick, 1, 1, 0, 0, 1);
            resetAll();
        });
    }

    private void startRace() {
        int bet1 = getBet(etBetCar1);
        int bet2 = getBet(etBetCar2);
        int bet3 = getBet(etBetCar3);

        if ((cbCar1.isChecked() && bet1 == 0) ||
            (cbCar2.isChecked() && bet2 == 0) ||
            (cbCar3.isChecked() && bet3 == 0)) {
            Toast.makeText(this, "Người được chọn phải có tiền cược!", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((!cbCar1.isChecked() && bet1 > 0) ||
            (!cbCar2.isChecked() && bet2 > 0) ||
            (!cbCar3.isChecked() && bet3 > 0)) {
            Toast.makeText(this, "Chỉ nhập tiền cược cho người được chọn!", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalBet = bet1 + bet2 + bet3;
        if (totalBet == 0 || (!cbCar1.isChecked() && !cbCar2.isChecked() && !cbCar3.isChecked())) {
            Toast.makeText(this, "Vui lòng chọn người và nhập tiền cược!", Toast.LENGTH_SHORT).show();
            return;
        }

        int balanceBefore = balance;
        balance -= totalBet;
        int balancePre = balance;
        tvBalance.setText("Số dư: " + balance + "$");

        // Reset vị trí nhân vật
        imgRunner1.setTranslationX(0f);
        imgRunner2.setTranslationX(0f);
        imgRunner3.setTranslationX(0f);

        raceInProgress = true;
        setInputsEnabled(false);

        // Ẩn nút khi đang đua
        btnStart.setVisibility(View.GONE);
        btnResetRound.setVisibility(View.GONE);
        btnResetAll.setVisibility(View.GONE);

        // Khởi động animation chạy
        imgRunner1.setBackgroundResource(R.drawable.runner_animation);
        imgRunner2.setBackgroundResource(R.drawable.runner_animation);
        imgRunner3.setBackgroundResource(R.drawable.runner_animation);

        AnimationDrawable anim1 = (AnimationDrawable) imgRunner1.getBackground();
        AnimationDrawable anim2 = (AnimationDrawable) imgRunner2.getBackground();
        AnimationDrawable anim3 = (AnimationDrawable) imgRunner3.getBackground();

        anim1.start();
        anim2.start();
        anim3.start();

        Random random = new Random();

        raceRunnable = new Runnable() {
            float pos1 = 0f, pos2 = 0f, pos3 = 0f;
            float finishLine = 800f; // tuỳ chỉnh theo chiều rộng màn hình

            @Override
            public void run() {
                if (!raceInProgress) return;

                pos1 += random.nextInt(20);
                pos2 += random.nextInt(20);
                pos3 += random.nextInt(20);

                imgRunner1.setTranslationX(pos1);
                imgRunner2.setTranslationX(pos2);
                imgRunner3.setTranslationX(pos3);

                if (pos1 >= finishLine || pos2 >= finishLine || pos3 >= finishLine) {
                    raceInProgress = false;
                    int winner = pos1 >= finishLine ? 1 :
                            pos2 >= finishLine ? 2 : 3;

                    if (winner == 1 && cbCar1.isChecked()) balance += bet1 * 2;
                    if (winner == 2 && cbCar2.isChecked()) balance += bet2 * 2;
                    if (winner == 3 && cbCar3.isChecked()) balance += bet3 * 2;

                    tvBalance.setText("Số dư: " + balance + "$");
                    setInputsEnabled(true);

                    btnStart.setVisibility(View.VISIBLE);
                    btnResetRound.setVisibility(View.VISIBLE);
                    btnResetAll.setVisibility(View.VISIBLE);

                    Intent intent = new Intent(GameActivity.this, ResultActivity.class);
                    intent.putExtra("balanceBefore", balanceBefore);
                    intent.putExtra("balancePre", balancePre);
                    intent.putExtra("balanceAfter", balance);
                    intent.putExtra("bet1", bet1);
                    intent.putExtra("bet2", bet2);
                    intent.putExtra("bet3", bet3);
                    intent.putExtra("winner", winner);
                    startActivity(intent);

                } else {
                    handler.postDelayed(this, 200);
                }
            }
        };

        handler.post(raceRunnable);
    }

    private int getBet(EditText et) {
        String text = et.getText().toString().trim();
        if (text.isEmpty()) return 0;
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void resetRound() {
        handler.removeCallbacks(raceRunnable);
        raceInProgress = false;

        cbCar1.setChecked(false);
        cbCar2.setChecked(false);
        cbCar3.setChecked(false);

        etBetCar1.setText("");
        etBetCar2.setText("");
        etBetCar3.setText("");

        imgRunner1.setTranslationX(0f);
        imgRunner2.setTranslationX(0f);
        imgRunner3.setTranslationX(0f);

        tvBalance.setText("Số dư: " + balance + "$");
        setInputsEnabled(true);

        btnStart.setVisibility(View.VISIBLE);
        btnResetRound.setVisibility(View.VISIBLE);
        btnResetAll.setVisibility(View.VISIBLE);
    }

    private void resetAll() {
        resetRound();
        balance = 100;
        tvBalance.setText("Số dư: " + balance + "$");
    }

    private void setInputsEnabled(boolean enabled) {
        cbCar1.setEnabled(enabled);
        cbCar2.setEnabled(enabled);
        cbCar3.setEnabled(enabled);

        etBetCar1.setEnabled(enabled);
        etBetCar2.setEnabled(enabled);
        etBetCar3.setEnabled(enabled);
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

        imgRunner1.setBackground(null);
        imgRunner2.setBackground(null);
        imgRunner3.setBackground(null);

        imgRunner1.setTranslationX(0f);
        imgRunner2.setTranslationX(0f);
        imgRunner3.setTranslationX(0f);

        raceInProgress = false;
        setInputsEnabled(true);

        btnStart.setVisibility(View.VISIBLE);
        btnResetRound.setVisibility(View.VISIBLE);
        btnResetAll.setVisibility(View.VISIBLE);
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