package com.example.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.musicplayer.entity.MusicItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台音乐播放服务
 */
public class MusicService extends Service {
    MediaPlayer mediaPlayer;
    // 当前播放列表
    List<MusicItem> playList;
    // 当前播放歌曲序号
    int index = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    public class MyBinder extends Binder {
        /** 获取MediaPlay对象 */
        public MediaPlayer getMediaPlayer(){
            return mediaPlayer;
        }

        /** 获取歌曲信息 */
        public Map<String, Object> getPlayingMusicInfo(){
            Map<String, Object> map = new HashMap<>();
            map.put("musicName", playList.get(index).getTitle());
            map.put("musicAuthor", playList.get(index).getArtist());
            map.put("musicImage", playList.get(index).getAlbum_art_bitmap());
            map.put("duration", mediaPlayer.getDuration());
            map.put("index", index);
            return map;
        }

        /** 获取当前播放列表 */
        public List<MusicItem> getPlayList(){
            return playList;
        }

        /** 设置当前播放列表 */
        public void setPlayList(List<MusicItem> list){
            playList = list;
        }

        /** 设置当前播放序号index */
        public void setIndex(int i){
            index = i;
        }

        /** 播放当前列表第i首歌 */
        public void playByIndex(int i){
            try {
                if(i >= playList.size() || i < 0){
                    index = 0;
                } else {
                    index = i;
                }
                MusicItem musicItem = playList.get(index);
                // 切歌之前先重置，释放掉之前的资源
                mediaPlayer.reset();
                // 判断是否本地音乐,设置播放源
                if(musicItem.getOn_local()) {
                    mediaPlayer.setDataSource(musicItem.getData());
                } else {
                    Uri uri = Uri.parse(musicItem.getData());
                    mediaPlayer.setDataSource(MusicService.this, uri);
                }
                // 通过异步的方式装载媒体资源
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 装载完毕 开始播放流媒体
                        mediaPlayer.start();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MusicService.this, "播放错误", Toast.LENGTH_SHORT).show();
            }
        }

        /** 播放/暂停音乐 */
        public void play(){
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }

        /** 下一首 */
        public void next() {
            playByIndex(++index);
        }

        /** 上一首 */
        public void prev() {
            playByIndex(--index);
        }

        /** 获取歌曲长度 **/
        public int getDuration() {
            // mediaPlayer未加载完歌曲资源时,getDuration会出错返回超长的时间
            //mediaPlayer.getDuration()
            return playList.get(index).getDuration();
        }

        /** 获取播放位置 */
        public int getCurrentPosition() {
            return mediaPlayer.getCurrentPosition();
        }

        /** 设置播放位置 */
        public void seekToPosition(int sec){
            mediaPlayer.seekTo(sec);
        }
    }
}
