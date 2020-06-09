package com.example.musicplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.RankActivity;
import com.example.musicplayer.entity.MainOnlienItem;

import java.util.List;

/**
 * 基本界面(主界面)在线音乐大图排行榜列表RecyclerViewAdapter
 */
public class MainOnlineAdapter extends RecyclerView.Adapter<MainOnlineAdapter.ViewHolder>{
    private Context context;
    private int resourceId;
    private List<MainOnlienItem> listData;

    public MainOnlineAdapter(Context context, int resourceId, List<MainOnlienItem> listData) {
        this.context = context;
        this.resourceId = resourceId;
        this.listData = listData;
    }

    @NonNull
    @Override
    public MainOnlineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MainOnlineAdapter.ViewHolder viewHolder;
        View view;
        view = LayoutInflater.from(context).inflate(resourceId, parent, false);
        viewHolder = new MainOnlineAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainOnlineAdapter.ViewHolder holder, final int position) {
        final MainOnlienItem item = listData.get(position);
        holder.image.setImageBitmap(item.getImage());
        holder.rank1.setText("1." + item.getRank1());
        holder.rank2.setText("2." + item.getRank2());
        holder.rank3.setText("3." + item.getRank3());

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RankActivity.class);
                intent.putExtra("type", String.valueOf(item.getType()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView rank1;
        TextView rank2;
        TextView rank3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_online_rank_image);
            rank1 = itemView.findViewById(R.id.item_online_rank_text1);
            rank2 = itemView.findViewById(R.id.item_online_rank_text2);
            rank3 = itemView.findViewById(R.id.item_online_rank_text3);
        }
    }
}
