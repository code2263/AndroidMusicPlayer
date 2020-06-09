package com.example.musicplayer.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.musicplayer.fragment.MainFragment;
import com.example.musicplayer.fragment.PlayPageAlbumFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放页面(歌曲页面)切换页面FragmentPagerAdapter
 */
public class PlayPageAdapter extends FragmentPagerAdapter {

    List<Fragment> list = new ArrayList<>();

    public PlayPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        list.add(new PlayPageAlbumFragment());
        list.add(new MainFragment("这是歌词页面"));
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
