package com.example.musicplayer.adapter;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.PlayPageActivity;
import com.example.musicplayer.entity.MusicItem;
import com.example.musicplayer.service.MusicService;

import java.util.List;

/**
 * 播放列表RecyclerViewAdapter
 */
public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {
    private Context context;
    private int resourceId;
    private List<MusicItem> listData;
    private Intent musicIntent, playIntent;
    private MusicService.MyBinder myBinder;

    // 初始化构造函数
    public PlayListAdapter(Context context, int resourceId, List<MusicItem> listData) {
        this.context = context;
        this.resourceId = resourceId;
        this.listData = listData;
    }

    // 连接service获取binder
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MusicService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myBinder = null;
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View view;
        view = LayoutInflater.from(context).inflate(resourceId, parent, false);
        viewHolder = new ViewHolder(view);
        // 绑定获取音乐服务的binder
        musicIntent = new Intent(context, MusicService.class);
        context.bindService(musicIntent, connection, Service.BIND_AUTO_CREATE);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final MusicItem item = listData.get(position);
        // 列表item内容填充
        holder.id = item.getId();
        // bitmap图片
        if(item.getAlbum_art_bitmap() != null) {
            holder.image.setImageBitmap(item.getAlbum_art_bitmap());
        } else {
            holder.image.setImageResource(R.drawable.play_page_default_cover);
        }
        holder.music_name.setText(item.getTitle());
        holder.author_name.setText(item.getArtist());
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 设置当前播放列表
                myBinder.setPlayList(listData);
                myBinder.playByIndex(position);
                playIntent = new Intent(context, PlayPageActivity.class);
                context.startActivity(playIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        int id;
        ImageView image;
        TextView music_name;
        TextView author_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_play_list_image);
            music_name = itemView.findViewById(R.id.item_play_list_music_name);
            author_name = itemView.findViewById(R.id.item_play_list_author_name);
        }
    }
}
