package com.example.musicplayer.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.PlayPageAdapter;
import com.example.musicplayer.fragment.PlayPageAlbumFragment;
import com.example.musicplayer.service.MusicService;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * 播放页面(歌曲页面)
 */
public class PlayPageActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{
    // 进度条旁边的当前进度文字，将毫秒化为mm:ss格式
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");
    PlayPageAdapter playPageAdapter;
    // 页面组件
    private TextView music_name, music_author, playing_time, total_time;
    private SeekBar seekBar;
    private ImageButton play_btn, prev_btn, next_btn;
    private ViewPager viewPager;
    // 音乐服务
    private Intent musicIntent;
    private MusicService.MyBinder myBinder;
    private MediaPlayer mediaPlayer;

    // 连接service获取binder
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MusicService.MyBinder) service;
            mediaPlayer = myBinder.getMediaPlayer();
            // 防止myBinder和mediaPlayer报null异常,一些初始化操作在这里完成
            // 填充歌曲信息
            final Map<String, Object> info = myBinder.getPlayingMusicInfo();
            music_name.setText(String.valueOf(info.get("musicName")));
            music_author.setText(String.valueOf(info.get("musicAuthor")));
            // 设置进度条信息
            seekBar.setMax(myBinder.getDuration());
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //判断是否来自用户操作进度条
                    if(fromUser){
                        myBinder.seekToPosition(seekBar.getProgress());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            total_time.setText(time.format(myBinder.getDuration()));
            handler.post(runnable);
            // 这里设置一个空的报错回调,防止报错后调用completion方法导致报错
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return true;
                }
            });
            // 列表循环播放,播放完自动播放下一首
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    myBinder.next();
                    flashPage();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mediaPlayer = null;
            myBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_page);
        // 绑定获取音乐服务的binder
        musicIntent = new Intent(PlayPageActivity.this, MusicService.class);
        bindService(musicIntent, connection, Service.BIND_AUTO_CREATE);
        // 获取页面组件
        music_name = findViewById(R.id.play_page_music_name_text);
        music_author = findViewById(R.id.play_page_author_text);
        seekBar = findViewById(R.id.play_page_seekBar);
        playing_time = findViewById(R.id.play_page_playing_time_text);
        total_time = findViewById(R.id.play_page_total_time_text);
        play_btn = findViewById(R.id.play_page_play_btn);
        prev_btn = findViewById(R.id.play_page_prev_btn);
        next_btn = findViewById(R.id.play_page_next_btn);
        viewPager = findViewById(R.id.play_page_viewPage);
        //设置viewPager的Adapter
        playPageAdapter = new PlayPageAdapter(getSupportFragmentManager(), 0);
        viewPager.setAdapter(playPageAdapter);
        //设置页面切换时的监听器(可选，用了之后要重写它的回调方法处理页面切换时候的事务)
        viewPager.addOnPageChangeListener(this);
        //设置viewPager的初始页
        viewPager.setCurrentItem(0);
        seekBar.setOnClickListener(click);
        play_btn.setOnClickListener(click);
        prev_btn.setOnClickListener(click);
        next_btn.setOnClickListener(click);
    }

    private View.OnClickListener click = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.play_page_play_btn:
                    play(); break;
                case R.id.play_page_prev_btn:
                    prev(); break;
                case R.id.play_page_next_btn:
                    next(); break;
                case R.id.play_page_seekBar:
                    seekToPosition(); break;
            }
        }

        void play(){
            myBinder.play();
        }

        void next() {
            myBinder.next();
            //刷新歌曲信息
            flashPage();
        }

        void prev(){
            myBinder.prev();
            //刷新歌曲信息
            flashPage();
        }

        void seekToPosition(){
            // 滑动进度条
            myBinder.seekToPosition(seekBar.getProgress());
        }

    };

    private Handler handler = new Handler();
    private Runnable runnable =new Runnable() {
        public void run() {
            // 每隔1秒刷新一次进度条和当前播放时间
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            playing_time.setText(time.format(myBinder.getCurrentPosition()));
            handler.postDelayed(this,1000);
        }
    };

    void flashPage(){
        // 刷新歌曲信息
        Map<String, Object> info = myBinder.getPlayingMusicInfo();
        music_name.setText(String.valueOf(info.get("musicName")));
        music_author.setText(String.valueOf(info.get("musicAuthor")));
        seekBar.setProgress(0);
        seekBar.setMax(myBinder.getDuration());
        playing_time.setText("00:00");
        total_time.setText(time.format(myBinder.getDuration()));
        // 刷新专辑图片
        PlayPageAlbumFragment albumFragment = (PlayPageAlbumFragment) playPageAdapter.getItem(0);
        albumFragment.flashPage();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    protected void onDestroy() {
        // 释放资源
        handler.removeCallbacks(runnable);
        unbindService(connection);
        super.onDestroy();
    }
}
