package com.example.musicplayer.activity;

import android.app.Instrumentation;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.PlayListAdapter;
import com.example.musicplayer.entity.MusicItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放列表页面
 */
public class PlayListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        RecyclerView recyclerView = findViewById(R.id.play_list_recycler);

        List<MusicItem> data = new ArrayList<>();
        PlayListAdapter adapter = new PlayListAdapter(PlayListActivity.this, R.layout.item_play_list, data);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PlayListActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        // 返回按钮点击事件(返回上一页)
        ImageButton back_button = findViewById(R.id.play_list_back_btn);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {
                            Instrumentation inst = new Instrumentation();
                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                        } catch (Exception e) {
                            Log.e("play_list_back_btn: ", e.toString());
                        }
                    }
                }.start();
            }
        });
    }
}
