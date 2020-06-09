package com.example.musicplayer.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.musicplayer.fragment.MainLocalMusicFragment;
import com.example.musicplayer.fragment.MainOnlineMusicFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 基本界面(主界面)切换页面FragmentPagerAdapter
 */
public class MainAdapter extends FragmentPagerAdapter {
    List<Fragment> list = new ArrayList<>();

    public MainAdapter(@NonNull FragmentManager fm, int behavior){
        super(fm, behavior);
        list.add(new MainLocalMusicFragment());
        list.add(new MainOnlineMusicFragment());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // Fragment在FragmentPagerAdapter中创建,防止页面多次创建引用报错
        switch (position) {
            case 0:
                return list.get(0);
            case 1:
                return list.get(1);
            default:
                return list.get(0);
        }
    }

    @Override
    public int getCount() {
        //设置Item的数量
        return 2;
    }
}
