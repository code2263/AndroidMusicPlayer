package com.example.musicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.musicplayer.adapter.MainAdapter;
import com.example.musicplayer.R;
import com.example.musicplayer.entity.MusicItem;
import com.example.musicplayer.fragment.MainLocalMusicFragment;
import com.example.musicplayer.service.MusicService;

import java.util.ArrayList;
import java.util.List;

/**
 * 基本界面(主界面)
 */
public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{
    MainAdapter mainAdapter;
    //页面组件
    ViewPager viewPager;
    TextView musicName, musicAuthor;
    Button local, online;
    ImageButton playlist, play, next;
    View detail;
    //音乐服务
    Intent intent;
    MusicService.MyBinder myBinder;
    MediaPlayer mediaPlayer;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MusicService.MyBinder) service;
            mediaPlayer = myBinder.getMediaPlayer();
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
        setContentView(R.layout.activity_main);
        //开启音乐服务
        intent = new Intent(MainActivity.this, MusicService.class);
        startService(intent);
        //绑定音乐服务
        bindService(intent, connection, Service.BIND_AUTO_CREATE);
        //获取组件
        local = findViewById(R.id.main_local_music_btn);
        online = findViewById(R.id.main_online_music_btn);
        playlist = findViewById(R.id.main_play_bar_playlist_btn);
        play = findViewById(R.id.main_play_bar_play_btn);
        next = findViewById(R.id.main_play_bar_next_btn);
        detail = findViewById(R.id.main_play_bar_detail_view);
        viewPager = findViewById(R.id.main_play_viewPager);
        musicName = findViewById(R.id.main_play_bar_music_name_text);
        musicAuthor = findViewById(R.id.main_play_bar_author_name_text);
        local.setOnClickListener(click);
        online.setOnClickListener(click);
        playlist.setOnClickListener(click);
        play.setOnClickListener(click);
        next.setOnClickListener(click);
        detail.setOnClickListener(click);
        //设置viewPager的Adapter
        mainAdapter = new MainAdapter(getSupportFragmentManager(), 0);
        viewPager.setAdapter(mainAdapter);
        //设置页面切换时的监听器(可选，用了之后要重写它的回调方法处理页面切换时候的事务)
        viewPager.addOnPageChangeListener(this);
        //设置viewPager的初始页为本地音乐列表
        viewPager.setCurrentItem(0);
    }

    private View.OnClickListener click = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.main_local_music_btn:
                    changeToLocalMusic(); break;
                case R.id.main_online_music_btn:
                    changeToOnlineMusic(); break;
                case R.id.main_play_bar_playlist_btn:
                    toPlayList(); break;
                case R.id.main_play_bar_detail_view:
                    toDetail(); break;
                case R.id.main_play_bar_play_btn:
                    play(); break;
                case R.id.main_play_bar_next_btn:
                    next(); break;
            }
        }

        void changeToLocalMusic(){
            // 本地音乐按钮点击事件(切换到本地音乐列表fragment)
            viewPager.setCurrentItem(0);
            local.setTextColor(Color.parseColor("#FFFFFF"));
            online.setTextColor(Color.parseColor("#999999"));
        }

        void changeToOnlineMusic(){
            // 在线音乐按钮点击事件(切换到在线音乐列表fragment)
            viewPager.setCurrentItem(1);
            local.setTextColor(Color.parseColor("#999999"));
            online.setTextColor(Color.parseColor("#FFFFFF"));
        }

        void toDetail(){
            // 下方歌曲点击事件(跳转到播放&歌词页面)
            // 若播放列表里没有歌曲,则赋值本地音乐列表,防止播放页面初始化失败报错闪退
            if(myBinder.getPlayList() == null) {
                System.out.println("==================================");
                MainLocalMusicFragment fragment = (MainLocalMusicFragment) mainAdapter.getItem(0);
                List<MusicItem> localList = fragment.getLocalList();
                if (localList == null || localList.size() == 0){
                    localList = new ArrayList<>();
                    localList.add(new MusicItem());
                }
                myBinder.setPlayList(localList);
            }
            Intent intent = new Intent(MainActivity.this, PlayPageActivity.class);
            startActivity(intent);
        }

        void toPlayList(){
            // 播放列表按钮点击事件(跳转到播放列表页面)
            // 若播放列表里没有歌曲,则赋值本地音乐列表,防止播放页面初始化失败报错闪退
            if(myBinder.getPlayList() == null) {
                MainLocalMusicFragment fragment = (MainLocalMusicFragment) mainAdapter.getItem(0);
                List<MusicItem> localList = fragment.getLocalList();
                if (localList == null || localList.size() == 0){
                    localList = new ArrayList<>();
                    localList.add(new MusicItem());
                }
                myBinder.setPlayList(localList);
            }
            Intent intent = new Intent(MainActivity.this, PlayListActivity.class);
            startActivity(intent);
        }

        void play(){
            myBinder.play();
        }

        void next(){
            myBinder.next();
        }
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position == 0) {
            local.setTextColor(Color.parseColor("#FFFFFF"));
            online.setTextColor(Color.parseColor("#999999"));
        } else {
            local.setTextColor(Color.parseColor("#999999"));
            online.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onDestroy() {
        //关闭程序时关闭音乐服务
        unbindService(connection);
        stopService(intent);
        super.onDestroy();
    }
}
