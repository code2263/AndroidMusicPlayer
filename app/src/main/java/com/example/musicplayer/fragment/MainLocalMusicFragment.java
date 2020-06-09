package com.example.musicplayer.fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.PlayListAdapter;
import com.example.musicplayer.entity.MusicItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 基本界面(主界面)本地音乐页面Fragment
 */
public class MainLocalMusicFragment extends Fragment {
    private ContentResolver resolver;
    private List<MusicItem> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_local_music, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.fragment_main_local_recyclerView);
        resolver = getActivity().getContentResolver();
        // 获取本地音乐列表
        list = queryData();
        PlayListAdapter adapter = new PlayListAdapter(MainLocalMusicFragment.this.getContext(), R.layout.item_play_list, list);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainLocalMusicFragment.this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    public List<MusicItem> getLocalList(){
        return list;
    }

    private List<MusicItem> queryData(){
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        List<MusicItem> list = new ArrayList<>();
        if(cursor != null) {
            while (cursor.moveToNext()) {
                Integer id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                Integer duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String year = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));
                String mime_type = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String is_music = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
                Integer album_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String album_art = null;
                Uri album_uri = Uri.parse("content://media/external/audio/albums/" + album_id);
                Cursor album_cursor = resolver.query(album_uri, new String[]{"album_art"}, null, null, null);
                if(album_cursor != null) {
                    if (album_cursor.getCount() > 0 && album_cursor.getColumnCount() > 0) {
                        album_cursor.moveToNext();
                        album_art = album_cursor.getString(0);
                    }
                    album_cursor.close();
                }
                MusicItem music = new MusicItem(id, display_name, title, duration, artist, album, year, mime_type, size, data, is_music, album_id, album_art, true, null);
                // 将专辑图片转为bitmap保存
                if (music.getAlbum_art() != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(music.getAlbum_art());
                    music.setAlbum_art_bitmap(bitmap);
                }
                list.add(music);
            }
            cursor.close();
        }
        return list;
    }
}
