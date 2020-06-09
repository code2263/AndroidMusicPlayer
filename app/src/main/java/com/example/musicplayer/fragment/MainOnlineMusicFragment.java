package com.example.musicplayer.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.MainOnlineAdapter;
import com.example.musicplayer.entity.MainOnlienItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 基本界面(主界面)在线音乐页面Fragment
 */
public class MainOnlineMusicFragment extends Fragment {
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_online, container, false);
        recyclerView = view.findViewById(R.id.fragment_main_online_recyclerView);

        // 初始载入空列表
        List<MainOnlienItem> data = new ArrayList<>();
        MainOnlineAdapter adapter = new MainOnlineAdapter(MainOnlineMusicFragment.this.getContext(), R.layout.item_online_rank, data);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainOnlineMusicFragment.this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        // 启动异步线程获取网络信息
        new task().execute();
        return view;
    }

    class task extends AsyncTask<Integer, String, List> {

        @Override
        protected List doInBackground(Integer... integers) {
            List<MainOnlienItem> list = new ArrayList<>();
            try {
                // 获取榜单列表
                MainOnlienItem bill = null;
                // 热歌榜
                if((bill = getBill(2, 3, 0)) != null) {
                    list.add(bill);
                }
                // 新歌榜
                if((bill = getBill(1, 3, 0)) != null) {
                    list.add(bill);
                }
                // 经典老歌榜
                if((bill = getBill(22, 3, 0)) != null) {
                    list.add(bill);
                }
                // 欧美金曲榜
                if((bill = getBill(21, 3, 0)) != null) {
                    list.add(bill);
                }
                // 影视金曲榜
                if((bill = getBill(24, 3, 0)) != null) {
                    list.add(bill);
                }
                // 摇滚榜
                if((bill = getBill(11, 3, 0)) != null) {
                    list.add(bill);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            publishProgress();
            return list;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);
            // 更新列表
            MainOnlineAdapter adapter = new MainOnlineAdapter(MainOnlineMusicFragment.this.getContext(), R.layout.item_online_rank, list);
            recyclerView.setAdapter(adapter);
        }

        // 获取列表item所需要的信息
        private MainOnlienItem getBill(int type, int size, int offset) {
            MainOnlienItem item = null;
            try {
                URL url = new URL("http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&calback=&method=baidu.ting.billboard.billList&type=" + type + "&size=" + size + "&offset=" + offset);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                // 将返回信息解析为json格式
                JSONObject jsonObject = jsonParse(inputStream);
                // 获取需要的信息
                JSONArray song_list = jsonObject.getJSONArray("song_list");
                JSONObject billboard = jsonObject.getJSONObject("billboard");
                Bitmap bitmap = null;
                try {
                    URL imgUrl = new URL(billboard.getString("pic_s260"));
                    HttpURLConnection conn = (HttpURLConnection) imgUrl
                            .openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String rank1 = song_list.getJSONObject(0).get("title") + " - " + song_list.getJSONObject(0).get("author");
                String rank2 = song_list.getJSONObject(1).get("title") + " - " + song_list.getJSONObject(1).get("author");
                String rank3 = "";
                try {
                    rank3 = song_list.getJSONObject(2).get("title") + " - " + song_list.getJSONObject(2).get("author");
                }catch (Exception e){ }
                // 封装
                item = new MainOnlienItem(type, bitmap, rank1, rank2, rank3);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return item;
        }

        // 解析数据为json格式
        private JSONObject jsonParse(InputStream inputStream) {
            JSONObject jsonObject = null;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                //转换成字符串
                String result = "";
                StringBuilder builder = new StringBuilder();
                while ((result = reader.readLine()) != null) {
                    builder.append(result);
                }
                jsonObject = new JSONObject(builder.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }
}
