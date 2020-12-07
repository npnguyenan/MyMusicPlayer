package com.example.serviceboundmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.serviceboundmusic.MyService.MyBinder;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MyService myService;
    private boolean isBound = false,flag = false;
    private ServiceConnection connection;
    private SeekBar volume,timeline;
    private TextView currentTime, musicTime,nameSong;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_app);

//        final Button btStop = (Button) findViewById(R.id.btnStop);
        final Button btPlay = (Button) findViewById(R.id.btnPlay);
        final Button btSkip = (Button) findViewById(R.id.btnSkip);
        final Button btPre = (Button) findViewById(R.id.btnPre);

        volume = (SeekBar) findViewById(R.id.volume);
        timeline = (SeekBar) findViewById(R.id.timeline);
        currentTime = (TextView) findViewById(R.id.currentTime);
        musicTime = (TextView) findViewById(R.id.musicTime);
        nameSong = (TextView) findViewById(R.id.nameSong);
        timeline.setMax(100);

        //Volume bar
        volume.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float volumeNum =progress/100f;
                        myService.setVolume(volumeNum);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        // Khởi tạo ServiceConnection
        connection = new ServiceConnection() {

            // Phương thức này được hệ thống gọi khi kết nối tới service bị lỗi
            @Override
            public void onServiceDisconnected(ComponentName name) {

                isBound = false;
                flag = false;
            }

            // Phương thức này được hệ thống gọi khi kết nối tới service thành công
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MyBinder binder = (MyBinder) service;
                myService = binder.getService(); // lấy đối tượng MyService
                isBound = true;
                if(myService.isPlaying())
                    handler.removeCallbacks(updater);
                else
                    updateTimeline();

            }
        };

        // Khởi tạo intent
        final Intent intent =
                new Intent(MainActivity.this,
                MyService.class);

        if(!isBound){
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
            flag =true;
        }

        //Sự kiện các button
        //btnPlay
        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag){
                    myService.setVolume(0.5f);
                    myService.seekTo(0);
                    nameSong.setText("'Pineapple'");
                    btPlay.setBackgroundResource(R.drawable.pause);
                    updateTimeline();
                    flag=false;
                }else{
                    if(myService.isPlaying()){
                        handler.removeCallbacks(updater);
                        myService.pause();
                        btPlay.setBackgroundResource(R.drawable.play);
                    }else {
                        myService.fastStart();
                        btPlay.setBackgroundResource(R.drawable.pause);
                        updateTimeline();
                    }
                }
            }
        });
        //btnStop
//        btStop.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // Nếu Service đang hoạt động
//                if(isBound){
//                    // Tắt Service
//                    unbindService(connection);
//                    btPlay.setBackgroundResource(R.drawable.play);
//                    isBound = false;
//                    nameSong.setText("");;
//                    updateTimeline();
//                }
//            }
//        });
        //btnSkip
        btSkip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // nếu service đang hoạt động
                if(isBound){
                    // tiến đến 5s
                    myService.skip();
                    currentTime.setText(milliSecToTimer(myService.getCurrentPosition()));
                }else{
                    Toast.makeText(MainActivity.this,
                            "Service chưa hoạt động", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //btnPre
        btPre.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // nếu service đang hoạt động
                if(isBound){
                    // lùi lại 5s
                    myService.previous();
                    currentTime.setText(milliSecToTimer(myService.getCurrentPosition()));
                }else{
                    Toast.makeText(MainActivity.this,
                            "Service chưa hoạt động", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Chỉnh thời gian nhạc theo thanh timeline
        timeline.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SeekBar seekBar = (SeekBar) v;
                int playPos = (myService.getDuration()/100)*seekBar.getProgress();
                myService.seekTo(playPos);
                currentTime.setText(milliSecToTimer(myService.getCurrentPosition()));
                return false;
            }
        });
    }
    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateTimeline();
            long currentDuration = myService.getCurrentPosition();
            if(isBound){
                currentTime.setText(milliSecToTimer(currentDuration));
                musicTime.setText(milliSecToTimer(myService.getDuration()));
            }
        }
    };
    private void  updateTimeline(){
        int time = (int)(((float)myService.getCurrentPosition()
                / myService.getDuration())*100);
        if(myService.isPlaying()){
            if(isBound){
                timeline.setProgress(time);
                timeline.setSecondaryProgress(time+10);
                handler.postDelayed(updater,100);
            }
            else {
                timeline.setProgress(0);
                currentTime.setText("00:00");
                musicTime.setText("00:00");
            }
        }
    }
    private  String milliSecToTimer(long milliSec){
        String timeString = "";
        String secondString;

        int minutes = (int) milliSec / 1000 / 60;
        int seconds = (int) milliSec / 1000 % 60;

        if(seconds < 10){
            secondString = "0" + seconds;
        }else {
            secondString = "" + seconds;
        }
        timeString = timeString + minutes + ":" + secondString;

        return timeString;
    }
}