package com.brainset.ocr;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.NumberPicker;

import androidx.fragment.app.Fragment;

import java.util.Locale;

public class Timer extends Fragment {
    TextView countdownTimer;
    Button start, pause, cancel, resume, startFocusTime;
    CountDownTimer timer;
    long timeLeftInMillis;
    boolean focusTimeEnabled = false;
    View view;
    Activity a;

    NumberPicker hoursPicker;
    NumberPicker minutesPicker;
    NumberPicker secondsPicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.study_timer, container, false);
        FocusMode.checkFocusMode(a);

        countdownTimer = view.findViewById(R.id.countdown_timer);
        start = view.findViewById(R.id.start);
        pause = view.findViewById(R.id.pause);
        cancel = view.findViewById(R.id.cancel);
        resume = view.findViewById(R.id.resume);
        startFocusTime = view.findViewById(R.id.focusTimeBtn);
        a = this.getActivity();

        hoursPicker = view.findViewById(R.id.hoursPicker);
        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(99);

        minutesPicker = view.findViewById(R.id.minutesPicker);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);

        secondsPicker = view.findViewById(R.id.secondsPicker);
        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);


        startFocusTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startFocusTime.getText().toString().equals("FocusTimeDisabled")) {
                    startFocusTime.setText("FocusTimeEnabled");
                    startFocusTime.setBackgroundColor(Color.parseColor("#15DB4D"));
                    focusTimeEnabled = true;
                    Log.e("ft", focusTimeEnabled + "");
                } else {
                    startFocusTime.setText("FocusTimeDisabled");
                    startFocusTime.setBackgroundColor(Color.parseColor("#FF0000"));
                    focusTimeEnabled = false;
                }
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel(); // Cancel any existing timer
                }
                long hours = hoursPicker.getValue();
                long minutes = minutesPicker.getValue();
                long seconds = secondsPicker.getValue();
                timeLeftInMillis = (hours * 3600000) + (minutes * 60000) + (seconds * 1000);
                startTime(timeLeftInMillis);
                start.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    pauseTimer();
            }
        });

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeTimer();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTimer();
            }
        });

        return view;
    }

    private void startTime(long time) {
        if (focusTimeEnabled) {
            GlobalData.user.inFocusMode = true;
            FocusMode.checkFocusMode(a);
        }
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                countdownTimer.setText("00:00:00");

                Toast.makeText(a, "Break Time", Toast.LENGTH_SHORT).show();
                MediaPlayer alarm = MediaPlayer.create(a, R.raw.summitalarm);
                alarm.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });
                alarm.start();
            }
        }.start();
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
        }
        pause.setVisibility(View.GONE);
        resume.setVisibility(View.VISIBLE);
    }

    private void resumeTimer() {
        startTime(timeLeftInMillis);
        pause.setVisibility(View.VISIBLE);
        resume.setVisibility(View.GONE);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
        countdownTimer.setText("00:00:00");
        timeLeftInMillis = 0;
        start.setVisibility(View.VISIBLE);
        pause.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        resume.setVisibility(View.GONE);
    }

    private void updateCountDownText() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        countdownTimer.setText(timeFormatted);
    }
}
