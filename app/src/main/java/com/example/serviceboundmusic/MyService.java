package com.example.serviceboundmusic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

public class MyService extends Service {
    private MyPlayer myPlayer;
    private IBinder binder;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ServiceDemo", "Đã gọi onCreate()");

        myPlayer = new MyPlayer(this);
        binder = new MyBinder(); // do MyBinder được extends Binder

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("ServiceDemo", "Đã gọi onBind()");
        myPlayer.play();
        // trả về đối tượng binder cho ActivityMain
        return binder;

    }
    // Kết thúc một Service
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("ServiceDemo", "Đã gọi onBind()");
        myPlayer.stop();
        return super.onUnbind(intent);
    }

    // Xây dựng các phương thức thực hiện nhiệm vụ,
    // ở đây mình demo phương thức tua bài hát
    public void setVolume(float volumeNum){
        myPlayer.setVolume(volumeNum);
    }
    public void pause(){
        myPlayer.pause();
    }
    public void skip(){
        myPlayer.skip(5000); // tua 5s
    }
    public void previous(){
        myPlayer.previous(5000); // lùi 5s
    }

    public void fastStart(){
        myPlayer.fastStart();
    }
    public boolean isPlaying(){
        if(myPlayer.isPlaying()){
            return true;
        }else return false;
    }
    public int getDuration(){
       int duration = myPlayer.getDuration();
       return duration;
    }
    public MediaPlayer mediaPlayer(){
        return myPlayer.mediaPlayer;
    }
    public void seekTo(int progress){
        myPlayer.seekTo(progress);
    }
    public long getCurrentPosition(){
        return myPlayer.getCurrentPosition();
    }
    public class MyBinder extends Binder {

        // phương thức này trả về đối tượng MyService
        public MyService getService() {

            return MyService.this;
        }
    }

}
// Xây dựng một đối tượng riêng để chơi nhạc
class MyPlayer {
    // đối tượng này giúp phát một bài nhạc
    public MediaPlayer mediaPlayer;

    public MyPlayer(Context context) {
        // Nạp bài nhạc vào mediaPlayer
        mediaPlayer = MediaPlayer.create(context, R.raw.pineapple);
        // Đặt chế độ phát lặp lại liên tục
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0,0);
    }
    public int getDuration(){
        int duration = mediaPlayer.getDuration();
        return duration;
    }
    public long getCurrentPosition(){
       return mediaPlayer.getCurrentPosition();
    }
    public boolean isPlaying(){
        if(mediaPlayer.isPlaying()){
            return true;
        }else return false;
    }
    public void setVolume(float volumeNum){
        mediaPlayer.setVolume(volumeNum,volumeNum);
    }
    public void pause(){
        mediaPlayer.pause();
    }
    public void seekTo(int progress){
        mediaPlayer.seekTo(progress);
    }
    public void skip(int pos){
        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+pos);
    }
    public void previous(int pos){
        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-pos);
    }
    public void fastStart(){
        mediaPlayer.start();
    }

    // phát nhạc
    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
    // dừng phát nhạc
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

}
