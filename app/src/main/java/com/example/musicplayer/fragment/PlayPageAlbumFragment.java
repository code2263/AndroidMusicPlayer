package com.example.musicplayer.fragment;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.musicplayer.R;
import com.example.musicplayer.service.MusicService;

import java.util.Map;

/**
 * 播放页面(歌曲页面)歌曲专辑图片页面Fragment
 */
public class PlayPageAlbumFragment extends Fragment {
    FragmentActivity activity;
    ImageView albumImage;
    // 音乐服务
    private Intent musicIntent;
    private MusicService.MyBinder myBinder;

    // 连接service获取binder
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MusicService.MyBinder) service;
            // 防止myBinder报null异常
            // 设置专辑图片
            flashPage();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myBinder = null;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 绑定获取音乐服务的binder
        activity = this.getActivity();
        musicIntent = new Intent(this.getContext(), MusicService.class);
        activity.bindService(musicIntent, connection, Service.BIND_AUTO_CREATE);
        View view = inflater.inflate(R.layout.fragment_play_page_album, container, false);
        albumImage = view.findViewById(R.id.fragment_play_page_album_imageView);
        return view;
    }

    public void flashPage(){
        // 设置专辑图片
        Map<String, Object> info = myBinder.getPlayingMusicInfo();
        if (info.get("musicImage") != null) {
            albumImage.setImageBitmap((Bitmap) info.get("musicImage"));
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.play_page_default_cover);
            albumImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onDestroy() {
        activity.unbindService(connection);
        super.onDestroy();
    }
}
